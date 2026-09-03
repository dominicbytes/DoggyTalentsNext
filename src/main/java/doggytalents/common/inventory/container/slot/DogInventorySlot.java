package doggytalents.common.inventory.container.slot;

import doggytalents.api.inferface.DTNItemStackHandler;
import doggytalents.common.entity.Dog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class DogInventorySlot extends ResourceHandlerSlot {

    private boolean enabled = true;
    private Player player;
    private Dog dog;
    private final DTNItemStackHandler itemHandler;
    private int absolute_col, relative_row, relative_col;

    public DogInventorySlot(Dog dogIn, Player playerIn, DTNItemStackHandler itemHandler, int absolute_col, int relative_row, int relative_col, int index, int xPosition, int yPosition) {
        super(itemHandler, itemHandler::set, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.player = playerIn;
        this.absolute_col = absolute_col;
        this.relative_row = relative_row;
        this.relative_col = relative_col;
        this.dog = dogIn;
    }

    public DogInventorySlot(DogInventorySlot prev, int newX) {
        super(prev.itemHandler, prev.itemHandler::set, prev.getSlotIndex(), newX, prev.y);
        this.itemHandler = prev.itemHandler;
        this.player = prev.player;
        this.absolute_col = prev.absolute_col;
        this.relative_row = prev.relative_row;
        this.relative_col = prev.relative_col;
        this.dog = prev.dog;
        // Preserve the menu-assigned slot index when replacing this slot in place.
        {
            Slot n = this;
            Slot o = prev;
            n.index = o.index;
        }
    }

    public void setEnabled(boolean flag) {
        this.enabled = flag;
    }

    // Don't accept items when disabled, this means disabled slots cannot be shift clicked into
    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.isActive() && super.mayPlace(stack);
    }

//    @Override
//    public boolean canTakeStack(PlayerEntity playerIn) {
//        return super.canTakeStack(playerIn);
//    }

    @Override
    public boolean isActive() {
        return this.enabled && this.dog.isDoingFine() && this.dog.distanceToSqr(this.player) < 400;
    }

    public Dog getDog() {
        return this.dog;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getAbsoluteCol() {
        return this.absolute_col;
    }

    public int getRelativeRow() {
        return this.relative_row;
    }

    public int getRelativeCol() {
        return this.relative_col;
    }
}
