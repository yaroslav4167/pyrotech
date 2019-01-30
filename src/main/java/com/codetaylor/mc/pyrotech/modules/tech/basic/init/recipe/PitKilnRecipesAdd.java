package com.codetaylor.mc.pyrotech.modules.tech.basic.init.recipe;

import com.codetaylor.mc.pyrotech.Reference;
import com.codetaylor.mc.pyrotech.modules.pyrotech.ModulePyrotech;
import com.codetaylor.mc.pyrotech.modules.pyrotech.block.BlockCobblestone;
import com.codetaylor.mc.pyrotech.modules.pyrotech.block.BlockRock;
import com.codetaylor.mc.pyrotech.modules.pyrotech.init.ModuleBlocks;
import com.codetaylor.mc.pyrotech.modules.pyrotech.init.ModuleItems;
import com.codetaylor.mc.pyrotech.modules.pyrotech.item.ItemMaterial;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.KilnPitRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.registries.IForgeRegistry;

public class PitKilnRecipesAdd {

  public static void apply(IForgeRegistry<KilnPitRecipe> registry) {

    // Brick
    registry.register(new KilnPitRecipe(
        new ItemStack(Items.BRICK),
        Ingredient.fromStacks(ItemMaterial.EnumType.UNFIRED_BRICK.asStack()),
        Reference.PitKiln.DEFAULT_BURN_TIME_TICKS,
        Reference.PitKiln.DEFAULT_FAILURE_CHANCE,
        new ItemStack[]{
            ItemMaterial.EnumType.PIT_ASH.asStack(),
            ItemMaterial.EnumType.POTTERY_SHARD.asStack(),
            ItemMaterial.EnumType.POTTERY_FRAGMENTS.asStack()
        }
    ).setRegistryName(ModulePyrotech.MOD_ID, "brick"));

    // Charcoal Flakes
    registry.register(new KilnPitRecipe(
        ItemMaterial.EnumType.CHARCOAL_FLAKES.asStack(),
        Ingredient.fromStacks(new ItemStack(ModuleBlocks.ROCK, 1, BlockRock.EnumType.WOOD_CHIPS.getMeta())),
        Reference.PitKiln.DEFAULT_BURN_TIME_TICKS,
        Reference.PitKiln.DEFAULT_FAILURE_CHANCE,
        new ItemStack[]{
            ItemMaterial.EnumType.PIT_ASH.asStack()
        }
    ).setRegistryName(ModulePyrotech.MOD_ID, "charcoal_flakes"));

    // Quicklime
    registry.register(new KilnPitRecipe(
        ItemMaterial.EnumType.QUICKLIME.asStack(),
        Ingredient.fromStacks(new ItemStack(ModuleBlocks.ROCK, 1, BlockRock.EnumType.LIMESTONE.getMeta())),
        Reference.PitKiln.DEFAULT_BURN_TIME_TICKS,
        Reference.PitKiln.DEFAULT_FAILURE_CHANCE,
        new ItemStack[]{
            ItemMaterial.EnumType.PIT_ASH.asStack()
        }
    ).setRegistryName(ModulePyrotech.MOD_ID, "quicklime"));

    // Stone Slab
    registry.register(new KilnPitRecipe(
        new ItemStack(Blocks.STONE_SLAB, 1, 0),
        Ingredient.fromStacks(new ItemStack(Blocks.STONE_SLAB, 1, 3)),
        Reference.PitKiln.DEFAULT_BURN_TIME_TICKS,
        Reference.PitKiln.DEFAULT_FAILURE_CHANCE,
        new ItemStack[]{
            ItemMaterial.EnumType.PIT_ASH.asStack(),
            new ItemStack(ModuleItems.ROCK, 3, 0)
        }
    ).setRegistryName(ModulePyrotech.MOD_ID, "stone_slab"));

    // Stone
    registry.register(new KilnPitRecipe(
        new ItemStack(Blocks.STONE, 1, 0),
        Ingredient.fromStacks(new ItemStack(Blocks.COBBLESTONE, 1, 0)),
        Reference.PitKiln.DEFAULT_BURN_TIME_TICKS,
        Reference.PitKiln.DEFAULT_FAILURE_CHANCE,
        new ItemStack[]{
            ItemMaterial.EnumType.PIT_ASH.asStack(),
            new ItemStack(ModuleItems.ROCK, 5, BlockRock.EnumType.STONE.getMeta())
        }
    ).setRegistryName(ModulePyrotech.MOD_ID, "stone"));

    // Stone - Andesite
    registry.register(new KilnPitRecipe(
        new ItemStack(Blocks.STONE, 1, 5),
        Ingredient.fromStacks(new ItemStack(ModuleBlocks.COBBLESTONE, 1, BlockCobblestone.EnumType.ANDESITE.getMeta())),
        Reference.PitKiln.DEFAULT_BURN_TIME_TICKS,
        Reference.PitKiln.DEFAULT_FAILURE_CHANCE,
        new ItemStack[]{
            ItemMaterial.EnumType.PIT_ASH.asStack(),
            new ItemStack(ModuleItems.ROCK, 5, BlockRock.EnumType.ANDESITE.getMeta())
        }
    ).setRegistryName(ModulePyrotech.MOD_ID, "stone_andesite"));

    // Stone - Granite
    registry.register(new KilnPitRecipe(
        new ItemStack(Blocks.STONE, 1, 1),
        Ingredient.fromStacks(new ItemStack(ModuleBlocks.COBBLESTONE, 1, BlockCobblestone.EnumType.GRANITE.getMeta())),
        Reference.PitKiln.DEFAULT_BURN_TIME_TICKS,
        Reference.PitKiln.DEFAULT_FAILURE_CHANCE,
        new ItemStack[]{
            ItemMaterial.EnumType.PIT_ASH.asStack(),
            new ItemStack(ModuleItems.ROCK, 5, BlockRock.EnumType.GRANITE.getMeta())
        }
    ).setRegistryName(ModulePyrotech.MOD_ID, "stone_granite"));

    // Stone - Diorite
    registry.register(new KilnPitRecipe(
        new ItemStack(Blocks.STONE, 1, 3),
        Ingredient.fromStacks(new ItemStack(ModuleBlocks.COBBLESTONE, 1, BlockCobblestone.EnumType.DIORITE.getMeta())),
        Reference.PitKiln.DEFAULT_BURN_TIME_TICKS,
        Reference.PitKiln.DEFAULT_FAILURE_CHANCE,
        new ItemStack[]{
            ItemMaterial.EnumType.PIT_ASH.asStack(),
            new ItemStack(ModuleItems.ROCK, 5, BlockRock.EnumType.DIORITE.getMeta())
        }
    ).setRegistryName(ModulePyrotech.MOD_ID, "stone_diorite"));

  }
}