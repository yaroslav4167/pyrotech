package com.codetaylor.mc.pyrotech.modules.tech.basic.tile.spi;

import com.codetaylor.mc.athenaeum.inventory.ObservableStackHandler;
import com.codetaylor.mc.athenaeum.network.tile.data.TileDataFloat;
import com.codetaylor.mc.athenaeum.network.tile.data.TileDataItemStackHandler;
import com.codetaylor.mc.athenaeum.network.tile.spi.ITileData;
import com.codetaylor.mc.athenaeum.network.tile.spi.ITileDataItemStackHandler;
import com.codetaylor.mc.athenaeum.util.ArrayHelper;
import com.codetaylor.mc.athenaeum.util.BlockHelper;
import com.codetaylor.mc.athenaeum.util.StackHelper;
import com.codetaylor.mc.pyrotech.interaction.api.Transform;
import com.codetaylor.mc.pyrotech.interaction.spi.IInteraction;
import com.codetaylor.mc.pyrotech.interaction.spi.ITileInteractable;
import com.codetaylor.mc.pyrotech.interaction.spi.InteractionItemStack;
import com.codetaylor.mc.pyrotech.interaction.spi.InteractionUseItemBase;
import com.codetaylor.mc.pyrotech.library.spi.tile.TileNetBase;
import com.codetaylor.mc.pyrotech.library.util.Util;
import com.codetaylor.mc.pyrotech.modules.core.ModuleCore;
import com.codetaylor.mc.pyrotech.modules.core.network.SCPacketParticleProgress;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.block.spi.BlockAnvilBase;
import com.codetaylor.mc.pyrotech.modules.tech.basic.network.SCPacketParticleAnvilHit;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.AnvilRecipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileAnvilBase
    extends TileNetBase
    implements ITileInteractable {

  private final TileDataItemStackHandler<InputStackHandler> tileDataItemStackHandler;
  private InputStackHandler stackHandler;
  private TileDataFloat recipeProgress;

  private int durabilityUntilNextDamage;

  private IInteraction[] interactions;
  private AxisAlignedBB renderBounds;

  public TileAnvilBase() {

    super(ModuleTechBasic.TILE_DATA_SERVICE);

    this.stackHandler = new InputStackHandler(this);
    this.stackHandler.addObserver((handler, slot) -> {
      this.recipeProgress.set(0);
      this.markDirty();
    });

    this.recipeProgress = new TileDataFloat(0);

    // --- Network ---

    this.tileDataItemStackHandler = new TileDataItemStackHandler<>(this.stackHandler);

    this.registerTileDataForNetwork(new ITileData[]{
        this.tileDataItemStackHandler,
        this.recipeProgress
    });

    // --- Interactions ---

    this.interactions = new IInteraction[]{
        new InteractionItem(),
        new Interaction(this, this.stackHandler),
        new InteractionHit()
    };
    this.durabilityUntilNextDamage = this.getHitsPerDamage();
  }

  @Override
  public boolean shouldRefresh(
      World world,
      BlockPos pos,
      @Nonnull IBlockState oldState,
      @Nonnull IBlockState newState
  ) {

    if (oldState.getBlock() == newState.getBlock()) {
      return false;
    }

    return super.shouldRefresh(world, pos, oldState, newState);
  }

  // ---------------------------------------------------------------------------
  // - Accessors
  // ---------------------------------------------------------------------------

  public void setDamage(int damage) {

    this.world.setBlockState(this.pos, this.getBlock().getDefaultState()
        .withProperty(BlockAnvilBase.DAMAGE, damage), 3);
  }

  public int getDamage() {

    return this.world.getBlockState(this.pos).getValue(BlockAnvilBase.DAMAGE);
  }

  public void setDurabilityUntilNextDamage(int durabilityUntilNextDamage) {

    this.durabilityUntilNextDamage = durabilityUntilNextDamage;
    this.markDirty();
  }

  public int getDurabilityUntilNextDamage() {

    return this.durabilityUntilNextDamage;
  }

  public void setRecipeProgress(float recipeProgress) {

    this.recipeProgress.set(recipeProgress);
  }

  public float getRecipeProgress() {

    return this.recipeProgress.get();
  }

  public ItemStackHandler getStackHandler() {

    return this.stackHandler;
  }

  // ---------------------------------------------------------------------------
  // - Subclass
  // ---------------------------------------------------------------------------

  public abstract int getBloomAnvilDamagePerHit();

  protected abstract int getHitsPerDamage();

  protected abstract double getExhaustionCostPerCraftComplete();

  protected abstract double getExhaustionCostPerHit();

  protected abstract int getHammerHitReduction(ResourceLocation resourceLocation);

  protected abstract String[] getPickaxeWhitelist();

  protected abstract String[] getPickaxeBlacklist();

  protected abstract int getMinimumHungerToUse();

  public abstract AnvilRecipe.EnumTier getRecipeTier();

  @Nonnull
  protected abstract BlockAnvilBase getBlock();

  // ---------------------------------------------------------------------------
  // - Capabilities
  // ---------------------------------------------------------------------------

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {

    return this.allowAutomation()
        && (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
  }

  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {

    if (this.allowAutomation()
        && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {

      //noinspection unchecked
      return (T) this.stackHandler;
    }

    return null;
  }

  protected abstract boolean allowAutomation();

  // ---------------------------------------------------------------------------
  // - Network
  // ---------------------------------------------------------------------------

  @Override
  public void onTileDataUpdate() {

    if (this.tileDataItemStackHandler.isDirty()) {
      BlockHelper.notifyBlockUpdate(this.world, this.pos);
      this.world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
    }
  }

  // ---------------------------------------------------------------------------
  // - Serialization
  // ---------------------------------------------------------------------------

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {

    super.writeToNBT(compound);
    compound.setTag("stackHandler", this.stackHandler.serializeNBT());
    compound.setInteger("durabilityUntilNextDamage", this.durabilityUntilNextDamage);
    compound.setFloat("recipeProgress", this.recipeProgress.get());
    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {

    super.readFromNBT(compound);
    this.stackHandler.deserializeNBT(compound.getCompoundTag("stackHandler"));
    this.durabilityUntilNextDamage = compound.getInteger("durabilityUntilNextDamage");
    this.recipeProgress.set(compound.getFloat("recipeProgress"));
  }

  // ---------------------------------------------------------------------------
  // - Rendering
  // ---------------------------------------------------------------------------

  @Nonnull
  @Override
  public AxisAlignedBB getRenderBoundingBox() {

    if (this.renderBounds == null) {
      this.renderBounds = new AxisAlignedBB(this.getPos()).expand(0, 0.5, 0);
    }

    return this.renderBounds;
  }

  @Override
  public boolean shouldRenderInPass(int pass) {

    return (pass == 0) || (pass == 1);
  }

  // ---------------------------------------------------------------------------
  // - Interactions
  // ---------------------------------------------------------------------------

  @Override
  public IInteraction[] getInteractions() {

    return this.interactions;
  }

  private class InteractionItem
      extends InteractionUseItemBase<TileAnvilBase> {

    /* package */ InteractionItem() {

      super(new EnumFacing[]{EnumFacing.UP}, BlockAnvilBase.AABB);
    }
  }

  private class Interaction
      extends InteractionItemStack<TileAnvilBase> {

    private final TileAnvilBase tile;

    /* package */ Interaction(TileAnvilBase tile, ItemStackHandler stackHandler) {

      super(new ItemStackHandler[]{stackHandler}, 0, new EnumFacing[]{EnumFacing.UP}, BlockAnvilBase.AABB, new Transform(
          Transform.translate(0.5, 0.75, 0.5),
          Transform.rotate(),
          Transform.scale(0.75, 0.75, 0.75)
      ));
      this.tile = tile;
    }

    @Override
    protected boolean doItemStackValidation(ItemStack itemStack) {

      return (AnvilRecipe.getRecipe(itemStack, this.tile.getRecipeTier()) != null);
    }

    @Override
    protected void onInsert(EnumType type, ItemStack itemStack, World world, EntityPlayer player, BlockPos pos) {

      super.onInsert(type, itemStack, world, player, pos);

      if (!world.isRemote) {
        world.playSound(
            null,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            SoundEvents.BLOCK_WOOD_PLACE,
            SoundCategory.BLOCKS,
            0.5f,
            (float) (1 + Util.RANDOM.nextGaussian() * 0.4f)
        );
      }
    }
  }

  private class InteractionHit
      extends InteractionUseItemBase<TileAnvilBase> {

    /* package */ InteractionHit() {

      super(new EnumFacing[]{EnumFacing.UP}, BlockAnvilBase.AABB);
    }

    @Override
    protected boolean allowInteraction(TileAnvilBase tile, World world, BlockPos hitPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing hitSide, float hitX, float hitY, float hitZ) {

      if (player.getFoodStats().getFoodLevel() < tile.getMinimumHungerToUse()) {
        return false;
      }

      ItemStack heldItemStack = player.getHeldItem(hand);
      Item heldItem = heldItemStack.getItem();

      ResourceLocation resourceLocation = heldItem.getRegistryName();

      if (resourceLocation == null) {
        return false;
      }

      ItemStackHandler stackHandler = tile.getStackHandler();
      ItemStack itemStack = stackHandler.extractItem(0, stackHandler.getSlotLimit(0), true);
      AnvilRecipe recipe = AnvilRecipe.getRecipe(itemStack, tile.getRecipeTier());

      if (recipe == null) {
        return false;
      }

      /*
        if explicitly declared in hammer config, is hammer
        else if tool is pickaxe
          if tool is not explicitly blacklisted as pickaxe, is pickaxe
        else if tool is explicitly whitelisted as pickaxe, is pickaxe
        else, is neither
       */

      if (tile.getHammerHitReduction(resourceLocation) > -1) {
        // held item is hammer
        return (recipe.getType() == AnvilRecipe.EnumType.HAMMER);

      } else if (heldItem.getToolClasses(heldItemStack).contains("pickaxe")) {
        // held item is pickaxe
        if (!ArrayHelper.contains(tile.getPickaxeBlacklist(), resourceLocation.toString())) {
          return (recipe.getType() == AnvilRecipe.EnumType.PICKAXE);
        }

      } else if (ArrayHelper.contains(tile.getPickaxeWhitelist(), resourceLocation.toString())) {
        // held item is pickaxe
        return (recipe.getType() == AnvilRecipe.EnumType.PICKAXE);
      }

      return false;
    }

    @Override
    protected boolean doInteraction(TileAnvilBase tile, World world, BlockPos hitPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing hitSide, float hitX, float hitY, float hitZ) {

      ItemStackHandler stackHandler = tile.getStackHandler();
      ItemStack itemStack = stackHandler.extractItem(0, stackHandler.getSlotLimit(0), true);
      AnvilRecipe recipe = AnvilRecipe.getRecipe(itemStack, tile.getRecipeTier());

      boolean isExtendedRecipe = (recipe instanceof AnvilRecipe.IExtendedRecipe);

      if (!world.isRemote) {

        // Server logic

        if (tile.getExhaustionCostPerHit() > 0) {
          player.addExhaustion((float) tile.getExhaustionCostPerHit());
        }

        // Decrement the tile's damage and reset the hits
        // remaining until next damage. If the damage reaches the threshold,
        // destroy the block and drop its contents.

        if (tile.getDurabilityUntilNextDamage() <= 1) {

          tile.setDurabilityUntilNextDamage(tile.getHitsPerDamage());

          if (tile.getDamage() + 1 < 4) {
            tile.setDamage(tile.getDamage() + 1);

          } else {
            StackHelper.spawnStackHandlerContentsOnTop(world, tile.getStackHandler(), tile.getPos());
            world.destroyBlock(tile.getPos(), false);
            return true;
          }
        }

        // Play sound for hit.
        world.playSound(
            null,
            player.posX,
            player.posY,
            player.posZ,
            SoundEvents.BLOCK_STONE_HIT,
            SoundCategory.BLOCKS,
            0.75f,
            (float) (1 + Util.RANDOM.nextGaussian() * 0.4f)
        );

        // Decrement the durability until next damage and progress or
        // complete the recipe.

        if (recipe != null) {

          if (isExtendedRecipe) {
            ((AnvilRecipe.IExtendedRecipe) recipe).applyDamage(world, tile);

          } else {
            tile.setDurabilityUntilNextDamage(tile.getDurabilityUntilNextDamage() - 1);
          }

          if (tile.getRecipeProgress() < 1) {
            ItemStack heldItemMainHand = player.getHeldItemMainhand();
            Item item = heldItemMainHand.getItem();
            int hitReduction;

            if (item.getToolClasses(heldItemMainHand).contains("pickaxe")) {
              hitReduction = item.getHarvestLevel(heldItemMainHand, "pickaxe", player, null);

            } else {
              hitReduction = tile.getHammerHitReduction(item.getRegistryName());
            }

            int hits = Math.max(1, recipe.getHits() - hitReduction);
            float recipeProgressIncrement = 1f / hits;

            if (isExtendedRecipe) {
              recipeProgressIncrement = ((AnvilRecipe.IExtendedRecipe) recipe).getModifiedRecipeProgressIncrement(recipeProgressIncrement, tile, player);
            }

            tile.setRecipeProgress(tile.getRecipeProgress() + recipeProgressIncrement);
          }

          if (tile.getRecipeProgress() >= 0.9999) {

            if (isExtendedRecipe) {
              //noinspection unchecked
              ((AnvilRecipe.IExtendedRecipe) recipe).onRecipeCompleted(tile, world, stackHandler, recipe, player);

            } else {
              stackHandler.extractItem(0, stackHandler.getSlotLimit(0), false);
              StackHelper.spawnStackOnTop(world, recipe.getOutput(), tile.getPos(), 0);
            }

            world.playSound(
                player,
                player.posX,
                player.posY,
                player.posZ,
                SoundEvents.BLOCK_STONE_BREAK,
                SoundCategory.BLOCKS,
                1,
                (float) (1 + Util.RANDOM.nextGaussian() * 0.4f)
            );

            if (tile.getExhaustionCostPerCraftComplete() > 0) {
              player.addExhaustion((float) tile.getExhaustionCostPerCraftComplete());
            }

            tile.markDirty();
            BlockHelper.notifyBlockUpdate(world, tile.getPos());
          }

          // Client particles
          ModuleCore.PACKET_SERVICE.sendToAllAround(
              new SCPacketParticleProgress(hitPos.getX() + 0.5, hitPos.getY() + 1, hitPos.getZ() + 0.5, 2),
              world.provider.getDimension(),
              hitPos
          );
          ModuleTechBasic.PACKET_SERVICE.sendToAllAround(new SCPacketParticleAnvilHit(tile.pos, hitX, hitY, hitZ), tile);
        }
      }

      return true;
    }
  }

  // ---------------------------------------------------------------------------
  // - Stack Handlers
  // ---------------------------------------------------------------------------

  private class InputStackHandler
      extends ObservableStackHandler
      implements ITileDataItemStackHandler {

    private final TileAnvilBase tile;

    public InputStackHandler(TileAnvilBase tile) {

      super(1);
      this.tile = tile;
    }

    @Override
    public int getSlotLimit(int slot) {

      return 1;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

      if (!stack.isEmpty()
          && AnvilRecipe.getRecipe(stack, this.tile.getRecipeTier()) != null) {
        return super.insertItem(slot, stack, simulate);
      }

      return stack;
    }
  }

}
