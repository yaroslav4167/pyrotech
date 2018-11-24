package com.codetaylor.mc.pyrotech.modules.pyrotech.tile;

import com.codetaylor.mc.athenaeum.inventory.ObservableStackHandler;
import com.codetaylor.mc.athenaeum.util.OreDictHelper;
import com.codetaylor.mc.athenaeum.util.StackHelper;
import com.codetaylor.mc.pyrotech.modules.pyrotech.ModulePyrotech;
import com.codetaylor.mc.pyrotech.modules.pyrotech.client.render.Transform;
import com.codetaylor.mc.pyrotech.modules.pyrotech.interaction.IInteraction;
import com.codetaylor.mc.pyrotech.modules.pyrotech.interaction.ITileInteractable;
import com.codetaylor.mc.pyrotech.modules.pyrotech.interaction.InteractionBounds;
import com.codetaylor.mc.pyrotech.modules.pyrotech.interaction.InteractionItemStack;
import com.codetaylor.mc.pyrotech.modules.pyrotech.network.ITileData;
import com.codetaylor.mc.pyrotech.modules.pyrotech.network.ITileDataItemStackHandler;
import com.codetaylor.mc.pyrotech.modules.pyrotech.network.data.TileDataItemStackHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class TileWoodRack
    extends TileNetworkedBase
    implements ITileInteractable {

  private StackHandler stackHandler;

  private IInteraction[] interactions;

  public TileWoodRack() {

    // --- Init ---

    super(ModulePyrotech.TILE_DATA_SERVICE);

    this.stackHandler = new StackHandler(9);
    this.stackHandler.addObserver((handler, slot) -> this.markDirty());

    // --- Network ---

    this.registerTileData(new ITileData[]{
        new TileDataItemStackHandler<>(this.stackHandler)
    });

    // --- Interactions ---

    this.interactions = new IInteraction[]{
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 0),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 1),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 2),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 3),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 4),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 5),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 6),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 7),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 8)
    };
  }

  public void dropContents() {

    StackHelper.spawnStackHandlerContentsOnTop(this.world, this.stackHandler, this.pos);
  }

  // ---------------------------------------------------------------------------
  // - Rendering
  // ---------------------------------------------------------------------------

  @Override
  public boolean shouldRenderInPass(int pass) {

    return (pass == 0) || (pass == 1);
  }

  // ---------------------------------------------------------------------------
  // - Serialization
  // ---------------------------------------------------------------------------

  @Override
  public void readFromNBT(NBTTagCompound compound) {

    super.readFromNBT(compound);

    this.stackHandler.deserializeNBT(compound.getCompoundTag("stackHandler"));
  }

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {

    super.writeToNBT(compound);

    compound.setTag("stackHandler", this.stackHandler.serializeNBT());

    return compound;
  }

  // ---------------------------------------------------------------------------
  // - Interactions
  // ---------------------------------------------------------------------------

  @Override
  public IInteraction[] getInteractions() {

    return this.interactions;
  }

  private static class Interaction
      extends InteractionItemStack {

    private static final double ONE_THIRD = 1.0 / 3.0;
    private static final double ONE_SIXTH = 1.0 / 6.0;

    public Interaction(ItemStackHandler[] stackHandlers, int slot) {

      super(stackHandlers, slot, new EnumFacing[]{EnumFacing.UP}, Interaction.createInteractionBounds(slot), Interaction.createTransform(slot));
    }

    private static Transform createTransform(int slot) {

      int x = slot % 3;
      int z = slot / 3;

      return new Transform(
          Transform.translate(x * ONE_THIRD + ONE_SIXTH, 8f / 16f, z * ONE_THIRD + ONE_SIXTH),
          Transform.rotate(),
          Transform.scale(ONE_THIRD, 12.0 / 16.0, ONE_THIRD)
      );
    }

    private static InteractionBounds createInteractionBounds(int slot) {

      int x = slot % 3;
      int z = slot / 3;

      return new InteractionBounds(x * ONE_THIRD, z * ONE_THIRD, x * ONE_THIRD + ONE_THIRD, z * ONE_THIRD + ONE_THIRD);
    }

    @Override
    protected boolean doItemStackValidation(ItemStack itemStack) {

      return OreDictHelper.contains("logWood", itemStack);
    }
  }

  private class StackHandler
      extends ObservableStackHandler
      implements ITileDataItemStackHandler {

    public StackHandler(int size) {

      super(size);
    }
  }
}