package doggytalents.common.util;

import doggytalents.api.feature.FoodHandler;
import doggytalents.api.inferface.AbstractDog;
import doggytalents.api.inferface.IDogFoodHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.apache.commons.lang3.tuple.Pair;
import org.joml.Math;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

public class InventoryUtil {

    public static Pair<ItemStack, Integer> findStack(ResourceHandler<ItemResource> source, Predicate<ItemStack> searchCriteria) {
        for (int i = 0; i < source.size(); i++) {

            ItemStack stack = net.neoforged.neoforge.transfer.item.ItemUtil.getStack(source, i);
            if (searchCriteria.test(stack)) {
                return Pair.of(stack.copy(), i);
            }
        }

        return null;
    }

    public static void transferStacks(ResourceHandler<ItemResource> source, ResourceHandler<ItemResource> target) {
        try (var transaction = Transaction.openRoot()) {
            for (int i = 0; i < source.size(); i++) {
                var resource = source.getResource(i);
                int amount = source.getAmountAsInt(i);
                if (resource.isEmpty() || amount == 0) {
                    continue;
                }
                int inserted = insertResource(target, resource, amount, transaction);
                int extracted = source.extract(i, resource, inserted, transaction);
                if (extracted != inserted) {
                    throw new IllegalStateException("Source inventory changed during item transfer");
                }
            }
            transaction.commit();
        }
    }

    public static ItemStack addItem(ResourceHandler<ItemResource> target, ItemStack remaining) {
        if (remaining.isEmpty()) {
            return remaining;
        }

        var resource = ItemResource.of(remaining);
        int inserted;
        try (var transaction = Transaction.openRoot()) {
            inserted = insertResource(target, resource, remaining.getCount(), transaction);
            transaction.commit();
        }
        int remainder = remaining.getCount() - inserted;
        return remainder == 0 ? ItemStack.EMPTY : remaining.copyWithCount(remainder);
    }

    private static int insertResource(ResourceHandler<ItemResource> target, ItemResource resource,
            int amount, TransactionContext transaction) {
        int inserted = 0;
        // Try to merge the stack into existing stack with same item first
        for (int i = 0; i < target.size(); i++) {
            var current = target.getResource(i);
            if (current.isEmpty() || current.getItem() != resource.getItem())
                continue;
            inserted += target.insert(i, resource, amount - inserted, transaction);
            if (inserted == amount) {
                break;
            }
        }

        if (inserted == amount) {
            return inserted;
        }

        // Try to insert item into all slots
        for (int i = 0; i < target.size(); i++) {
            inserted += target.insert(i, resource, amount - inserted, transaction);
            if (inserted == amount) {
                break;
            }
        }
        return inserted;
    }

    // Same as net.minecraft.inventory.container.Container.calcRedstoneFromInventory but for item resource handlers
    public static int calcRedstoneFromInventory(@Nullable ResourceHandler<ItemResource> inv) {
        if (inv == null) {
           return 0;
        } else {
           int i = 0;
           float f = 0.0F;

           for (int j = 0; j < inv.size(); ++j) {
              ItemStack itemstack = net.neoforged.neoforge.transfer.item.ItemUtil.getStack(inv, j);
              if (!itemstack.isEmpty()) {
                 f += itemstack.getCount() / (float)Math.min(
                     inv.getCapacityAsInt(j, inv.getResource(j)), itemstack.getMaxStackSize());
                 ++i;
              }
           }

           f = f / inv.size();
           return Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);
        }
     }

    public static @Nullable ItemStack findStackWithItemFromHands(Player player, Item item) {
        if (item == null) return null;
        if (player == null) return null;
        var stack0 = player.getMainHandItem();
        var stack1 = player.getOffhandItem();
        if (stack0 != null && stack0.getItem() == item) 
            return stack0;
        if (stack1 != null && stack1.getItem() == item)
            return stack1;
        return null; 
    }

    public static int maxStackSizeWithContainer(Container container, int slot, ItemStack stack) {
        var stack_maxSize = stack.getMaxStackSize();
        var container_maxSize = container.getMaxStackSize(); 
        return Math.min(stack_maxSize, container_maxSize);
    }

    public static int maxStackSizeWithContainer(ResourceHandler<ItemResource> container, int slot, ItemStack stack) {
        var stack_maxSize = stack.getMaxStackSize();
        var container_maxSize = container.getCapacityAsInt(slot, ItemResource.of(stack));
        return Math.min(stack_maxSize, container_maxSize);
    }

}
