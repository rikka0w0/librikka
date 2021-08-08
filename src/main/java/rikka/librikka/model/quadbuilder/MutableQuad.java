package rikka.librikka.model.quadbuilder;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Modified from BuildCraft source code */
@OnlyIn(Dist.CLIENT)
public class MutableQuad implements IRawGroupWrapper<MutableQuad, MutableVertex>, IRawElement<MutableQuad> {
    public final MutableVertex vertex_0;
    public final MutableVertex vertex_1;
    public final MutableVertex vertex_2;
    public final MutableVertex vertex_3;
    protected final List<MutableVertex> vertices = new LinkedList<>();

    private final VertexFormat format;
    private int tintIndex = -1;
    private Direction face = null;
    private boolean shade = false;
    private TextureAtlasSprite sprite = null;

    public MutableQuad(BakedQuad quad) {
    	format = DefaultVertexFormat.BLOCK;
        tintIndex = quad.getTintIndex();
        face = quad.getDirection();
        sprite = quad.getSprite();
        shade = quad.isShade();

        int[] data = quad.getVertices();
        int stride = data.length / 4;

        vertex_0 = new MutableVertex(data, 0, format);
        vertex_1 = new MutableVertex(data, stride, format);
        vertex_2 = new MutableVertex(data, stride * 2, format);
        vertex_3 = new MutableVertex(data, stride * 3, format);
        
        vertices.add(vertex_0);
        vertices.add(vertex_1);
        vertices.add(vertex_2);
        vertices.add(vertex_3);
    }
    
    private MutableQuad(MutableQuad quad) {
        this.format = quad.format;
        tintIndex = quad.tintIndex;
        face = quad.face;
        sprite = quad.sprite;
        shade = quad.shade;

        vertex_0 = quad.vertex_0.clone();
        vertex_1 = quad.vertex_1.clone();
        vertex_2 = quad.vertex_2.clone();
        vertex_3 = quad.vertex_3.clone();
        
        vertices.add(vertex_0);
        vertices.add(vertex_1);
        vertices.add(vertex_2);
        vertices.add(vertex_3);
    }

    public int getTint() {
        return tintIndex;
    }

    public MutableQuad setTint(int tint) {
        tintIndex = tint;
        return this;
    }

    public BakedQuad bake() {
        int[] data = new int[format.getVertexSize()];
        int stride = data.length / 4;
        vertex_0.toBakedItem(data, 0);
        vertex_1.toBakedItem(data, stride);
        vertex_2.toBakedItem(data, stride*2);
        vertex_3.toBakedItem(data, stride*3);

        // Rikka's Patch
        // Fix normal vector
        int normal= BakedQuadHelper.calculatePackedNormal(
                vertex_0.position_x, vertex_0.position_y, vertex_0.position_z,
                vertex_1.position_x, vertex_1.position_y, vertex_1.position_z,
                vertex_2.position_x, vertex_2.position_y, vertex_2.position_z,
                vertex_3.position_x, vertex_3.position_y, vertex_3.position_z
                );
        data[stride-1] = normal;
        data[stride*2-1] = normal;
        data[stride*3-1] = normal;
        data[stride*4-1] = normal;

        return new BakedQuad(data, tintIndex, face, sprite, shade);
    }

	@Override
	public List<MutableVertex> getElements() {
		return vertices;
	}

	@Override
	public void bake(List<BakedQuad> list) {
		list.add(this.bake());
	}

	@Override
	public MutableQuad clone() {
		return new MutableQuad(this);
	}
}
