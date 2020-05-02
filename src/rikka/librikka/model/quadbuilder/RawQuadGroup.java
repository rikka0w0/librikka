package rikka.librikka.model.quadbuilder;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.math.Vec3f;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RawQuadGroup implements IRawModel<RawQuadGroup> {
    private final LinkedList<IRawElement> elements = new LinkedList();

    public RawQuadGroup() {
    }

    public RawQuadGroup(IRawElement... rawModels) {
        this.add(rawModels);
    }

    public void add(IRawElement... rawModels) {
        for (IRawElement rawModel : rawModels) {
            this.elements.add(rawModel);
        }
    }

    public void merge(RawQuadGroup group) {
        elements.addAll(group.elements);
    }

    @Override
    public RawQuadGroup clone() {
        RawQuadGroup ret = new RawQuadGroup();
        for (IRawElement part : this.elements)
            ret.add(part.clone());
        return ret;
    }

    @Override
    public RawQuadGroup translateCoord(float x, float y, float z) {
        for (IRawModel part : this.elements)
            part.translateCoord(x, y, z);

        return this;
    }

    public RawQuadGroup translateCoord(Vec3f offset) {
    	return this.translateCoord(offset.x, offset.y, offset.z);
    }
    
    @Override
    public RawQuadGroup rotateAroundX(float angle) {
        for (IRawModel part : this.elements)
            part.rotateAroundX(angle);

        return this;
    }

    @Override
    public RawQuadGroup rotateAroundY(float angle) {
        for (IRawModel part : this.elements)
            part.rotateAroundY(angle);

        return this;
    }

    @Override
    public RawQuadGroup rotateAroundZ(float angle) {
        for (IRawModel part : this.elements)
            part.rotateAroundZ(angle);

        return this;
    }

    @Override
    public RawQuadGroup rotateToVec(float xStart, float yStart, float zStart,
                                    float xEnd, float yEnd, float zEnd) {
        for (IRawModel part : this.elements)
            part.rotateToVec(xStart, yStart, zStart, xEnd, yEnd, zEnd);

        return this;
    }
    
    public RawQuadGroup rotateToVec(Vec3f vec1, Vec3f vec2) {
    	return this.rotateToVec(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z);
    }

    @Override
    public RawQuadGroup rotateToDirection(Direction direction) {
        for (IRawModel part : this.elements)
            part.rotateToDirection(direction);

        return this;
    }

    @Override
    public RawQuadGroup rotateAroundVector(float angle, float x, float y, float z) {
        for (IRawModel part : this.elements)
            part.rotateAroundVector(angle, x, y, z);

        return this;
    }

    public RawQuadGroup rotateAroundVector(float angle, Vec3f vec) {
    	return this.rotateAroundVector(angle, vec.x, vec.y, vec.z);
    }
    
	@Override
	public RawQuadGroup scale(float scale) {
		for (IRawModel part : this.elements)
			part.scale(scale);
		return this;
	}
    
    @Override
    public void bake(List<BakedQuad> list) {
        for (IRawModel part : this.elements)
            part.bake(list);
    }
}
