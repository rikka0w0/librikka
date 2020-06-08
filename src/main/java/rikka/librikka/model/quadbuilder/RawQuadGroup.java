package rikka.librikka.model.quadbuilder;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RawQuadGroup implements IRawGroup<RawQuadGroup> {
    private final List<IRawElement<?>> elements = new LinkedList<>();

    public RawQuadGroup() {
    }

    public RawQuadGroup(IRawElement<?>... rawModels) {
        this.add(rawModels);
    }

    @Override
    public RawQuadGroup clone() {
        RawQuadGroup ret = new RawQuadGroup();
        for (IRawElement<?> part : this.elements)
            ret.add(part.clone());
        return ret;
    }
    
    @Override
    public void bake(List<BakedQuad> list) {
        for (IRawModel<?> part : this.elements)
            part.bake(list);
    }

	@Override
	public List<IRawElement<?>> getElements() {
		return this.elements;
	}
}
