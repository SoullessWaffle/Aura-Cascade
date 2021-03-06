package pixlepix.auracascade.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pixlepix.auracascade.block.tile.TileBookshelfCoordinator;
import pixlepix.auracascade.block.tile.TileStorageBookshelf;
import pixlepix.auracascade.data.StorageItemStack;

/**
 * Created by localmacaccount on 1/24/15.
 */
public class SlotCoordinator extends Slot {

    public int realIndex;
    public StorageItemStack storage;
    private TileBookshelfCoordinator te;


    public SlotCoordinator(int p_i1824_2_, int p_i1824_3_, int p_i1824_4_, TileBookshelfCoordinator te, StorageItemStack storageItemStack) {
        super(te, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        this.storage = storageItemStack;
        this.te = te;

    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
        te.markDirty();
    }

    @Override
    public ItemStack getStack() {
        return storage != null ? storage.toItemStack() : null;
    }

    @Override
    public void putStack(ItemStack stack) {
        if (te.getWorld().isRemote) {
            storage = stack == null ? null : new StorageItemStack(stack);
        } else {
            StorageItemStack lost = null;
            StorageItemStack gained = null;
            StorageItemStack newStack = new StorageItemStack(stack);

            //Calculate change from previous state
            if (newStack.equalsType(storage)) {
                int delta = newStack.stackSize - storage.stackSize;
                if (delta > 0) {
                    gained = storage.copy();
                    gained.stackSize = delta;
                }
                if (delta < 0) {
                    lost = storage.copy();
                    lost.stackSize = -delta;
                }
            } else {
                lost = storage != null ? storage.copy() : null;
                gained = newStack.copy();
            }
            if (lost != null) {
                outer:
                for (TileStorageBookshelf bookshelf : te.getBookshelves()) {
                    for (int i = 0; i < bookshelf.getSizeInventory(); i++) {
                        ItemStack stackInShelf = bookshelf.getStackInSlot(i);
                        if (new StorageItemStack(stackInShelf).equalsType(lost)) {
                            int delta = Math.min(lost.stackSize, stackInShelf.stackSize);
                            lost.stackSize -= delta;
                            stackInShelf.stackSize -= delta;
                            if (stackInShelf.stackSize == 0) {
                                bookshelf.setInventorySlotContents(i, null);
                            }
                            bookshelf.markDirty();
                            if (lost.stackSize <= 0) {
                                break outer;

                            }
                        }
                    }
                }
            }
            if (gained != null) {
                outer:
                for (TileStorageBookshelf bookshelf : te.getBookshelves()) {
                    for (int i = 0; i < bookshelf.getSizeInventory(); i++) {
                        ItemStack stackInShelf = bookshelf.getStackInSlot(i);
                        ItemStack testStack = null;
                        if (stackInShelf != null && new StorageItemStack(stackInShelf).equalsType(gained)) {
                            testStack = stackInShelf.copy();
                        }
                        if (stackInShelf == null) {
                            testStack = gained.toItemStack();
                            testStack.stackSize = 1;
                        }
                        if (testStack != null) {
                            while (bookshelf.isItemValidForSlotSensitive(i, testStack) && testStack.stackSize < testStack.getMaxStackSize()) {
                                testStack.stackSize++;
                                gained.stackSize++;
                            }
                            //We've gone one too far
                            testStack.stackSize--;
                            gained.stackSize--;

                            if (testStack.stackSize > 0) {
                                bookshelf.setInventorySlotContents(i, testStack);
                            }
                            if (gained.stackSize <= 0) {
                                break outer;

                            }
                        }
                    }
                }

            }


            storage = stack == null ? null : new StorageItemStack(stack);
        }
    }


    @Override
    public int getSlotStackLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isHere(IInventory inv, int slot) {
        return inv == te && new StorageItemStack(te.getStackInSlot(slot)).equalsType(storage);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return te.isItemValidForSlotSensitive(getSlotIndex(), stack);
    }

}
