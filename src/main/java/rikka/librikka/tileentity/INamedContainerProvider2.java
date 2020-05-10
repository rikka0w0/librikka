package rikka.librikka.tileentity;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public interface INamedContainerProvider2 extends INamedContainerProvider {
	default ITextComponent getDisplayName() {
		TileEntity te = (TileEntity) this;
		return new TranslationTextComponent(te.getBlockState().getBlock().getTranslationKey());
	}
}
