package com.tarinoita.solsweetpotato.item;

import com.tarinoita.solsweetpotato.client.gui.FoodBookScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class FoodBookItem extends Item {
	public FoodBookItem() {
		super(new Properties());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if (world.isClientSide) {
            FoodBookScreen.open(player);
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
	}
}
