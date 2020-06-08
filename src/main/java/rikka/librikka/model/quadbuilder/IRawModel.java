package rikka.librikka.model.quadbuilder;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IRawModel<T extends IRawModel<?>> extends ITransformable<T>, IBakeable {
	T clone();
}
