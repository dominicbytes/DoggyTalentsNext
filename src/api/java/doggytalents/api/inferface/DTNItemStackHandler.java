package doggytalents.api.inferface;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Doggy Talents item storage backed by NeoForge's transactional resource API.
 *
 * <p>The stack-oriented methods retain the mod's established source API while
 * menu and automation integrations use this handler as a resource handler.</p>
 */
public class DTNItemStackHandler extends ItemStacksResourceHandler {

    public DTNItemStackHandler(int size) {
        super(size);
    }

    public void setSize(int size) {
        setStacks(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        set(slot, ItemResource.of(stack), stack.getCount());
    }

    public int getSlots() {
        return size();
    }

    /**
     * Returns the stored stack reference for compatibility with dog equipment,
     * which intentionally shares tool stacks with the dog's held item.
     */
    public ItemStack getStackInSlot(int slot) {
        return this.stacks.get(slot);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        try (var transaction = Transaction.openRoot()) {
            int inserted = insert(slot, ItemResource.of(stack), stack.getCount(), transaction);
            if (!simulate) {
                transaction.commit();
            }
            int remaining = stack.getCount() - inserted;
            return remaining == 0 ? ItemStack.EMPTY : stack.copyWithCount(remaining);
        }
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        var resource = getResource(slot);
        if (resource.isEmpty()) {
            return ItemStack.EMPTY;
        }

        try (var transaction = Transaction.openRoot()) {
            int extracted = extract(slot, resource, Math.min(amount, resource.getMaxStackSize()), transaction);
            if (!simulate) {
                transaction.commit();
            }
            return resource.toStack(extracted);
        }
    }

    public int getSlotLimit(int slot) {
        return Item.ABSOLUTE_MAX_STACK_SIZE;
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public boolean isValid(int slot, ItemResource resource) {
        return !resource.isEmpty() && resource.test(stack -> isItemValid(slot, stack));
    }

    @Override
    protected int getCapacity(int slot, ItemResource resource) {
        return resource.isEmpty()
            ? getSlotLimit(slot)
            : Math.min(getSlotLimit(slot), resource.getMaxStackSize());
    }

    @Override
    protected final void onContentsChanged(int slot, ItemStack previousContents) {
        onContentsChanged(slot);
    }

    protected void onContentsChanged(int slot) {}

    protected void onLoad() {}

    @Override
    public void serialize(ValueOutput output) {
        ValueOutput.TypedOutputList<ItemStackWithSlot> itemList = output.list("Items", ItemStackWithSlot.CODEC);
        for (int i = 0; i < stacks.size(); i++) {
            var stack = stacks.get(i);
            if (!stack.isEmpty()) {
                itemList.add(new ItemStackWithSlot(i, stack));
            }
        }
        output.putInt("Size", stacks.size());
    }

    @Override
    public void deserialize(ValueInput input) {
        setSize(input.getIntOr("Size", stacks.size()));
        input.listOrEmpty("Items", ItemStackWithSlot.CODEC).forEach(slot -> {
            if (slot.isValidInContainer(stacks.size())) {
                stacks.set(slot.slot(), slot.stack());
            }
        });
        onLoad();
    }
}
