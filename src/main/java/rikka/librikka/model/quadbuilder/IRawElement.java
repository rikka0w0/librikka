package rikka.librikka.model.quadbuilder;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IRawElement<T extends IRawElement> extends IRawModel<T> {
	T clone();
}
