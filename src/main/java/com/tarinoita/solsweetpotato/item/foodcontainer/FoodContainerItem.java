package com.tarinoita.solsweetpotato.item.foodcontainer;

import com.tarinoita.solsweetpotato.integration.Origins;
import com.tarinoita.solsweetpotato.tracking.FoodList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public class FoodContainerItem extends Item {
    private String displayName;
    private int nslots;

    public FoodContainerItem(int nslots, String displayName) {
        super(new Properties().stacksTo(1).component(net.minecraft.core.component.DataComponents.CONTAINER, net.minecraft.world.item.component.ItemContainerContents.EMPTY));

        this.displayName = displayName;
        this.nslots = nslots;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide && player.isCrouching()) {
            ((ServerPlayer) player).openMenu(new FoodContainerProvider(displayName, hand), buf -> buf.writeEnum(hand));
        }

        if (!player.isCrouching()) {
            return processRightClick(world, player, hand);
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    private InteractionResultHolder<ItemStack> processRightClick(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isInventoryEmpty(stack) ||
                (ModList.get().isLoaded("origins") && Origins.hasRestrictedDiet(player))) {
            return InteractionResultHolder.pass(stack);
        }

        if (player.canEat(false)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    private static boolean isInventoryEmpty(ItemStack container) {
        IItemHandlerModifiable handler = getInventory(container);
        if (handler == null) {
            return true;
        }

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getFoodProperties(null) != null) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public static IItemHandlerModifiable getInventory(ItemStack bag) {
        if (bag.getItem() instanceof FoodContainerItem item)
            return new FoodContainerCapabilityProvider(bag, item.nslots);
        return null;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.EAT; }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (world.isClientSide || !(entity instanceof Player)) {
            return stack;
        }

        Player player = (Player) entity;
        IItemHandlerModifiable handler = getInventory(stack);
        if (handler == null) {
            return stack;
        }

        int bestFoodSlot = getBestFoodSlot(handler, player);
        if (bestFoodSlot < 0) {
            return stack;
        }

        ItemStack bestFood = handler.getStackInSlot(bestFoodSlot);
        ItemStack foodCopy = bestFood.copy();
        if (bestFood.getFoodProperties(entity) != null && !bestFood.isEmpty()) {
            ItemStack result = bestFood.finishUsingItem(world, entity);
            handler.setStackInSlot(bestFoodSlot, result);
            // put bowls/bottles etc. into player inventory
            if (result.getFoodProperties(entity) == null) {
                handler.setStackInSlot(bestFoodSlot, ItemStack.EMPTY);
                Player playerEntity = (Player) entity;

                if (!playerEntity.getInventory().add(result)) {
                    playerEntity.drop(result, false);
                }
            }

            if (!world.isClientSide) {
                // Fire an event instead of directly updating the food list, so that
                // SoL: Carrot Edition registers the eaten food too.
                EventHooks.onItemUseFinish(player, foodCopy, 0, result);
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity){
        return 32;
    }

    public static int getBestFoodSlot(IItemHandlerModifiable handler, Player player) {
        FoodList foodList = FoodList.get(player);

        double maxDiversity = -Double.MAX_VALUE;
        int bestFoodSlot = -1;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack food = handler.getStackInSlot(i);

            if (food.getFoodProperties(player) == null || food.isEmpty())
                continue;
            double diversityChange = foodList.simulateFoodAdd(food.getItem());
            if (diversityChange > maxDiversity) {
                maxDiversity = diversityChange;
                bestFoodSlot = i;
            }
        }

        return bestFoodSlot;
    }
}
