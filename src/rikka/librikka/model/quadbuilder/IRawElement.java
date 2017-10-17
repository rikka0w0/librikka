package rikka.librikka.model.quadbuilder;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRawElement<T extends IRawElement> extends IRawModel<T> {
    @Override
    T clone();
}
