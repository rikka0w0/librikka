package rikka.librikka.blockentity;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public interface INamedMenuProvider extends MenuProvider {
	default Component getDisplayName() {
		BlockEntity te = (BlockEntity) this;
		return new TranslatableComponent(te.getBlockState().getBlock().getDescriptionId());
	}
}
