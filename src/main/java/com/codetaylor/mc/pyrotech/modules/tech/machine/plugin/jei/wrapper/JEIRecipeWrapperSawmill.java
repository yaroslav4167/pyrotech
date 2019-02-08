package com.codetaylor.mc.pyrotech.modules.tech.machine.plugin.jei.wrapper;

import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.spi.MachineRecipeBaseSawmill;
import com.codetaylor.mc.pyrotech.modules.tech.refractory.plugin.jei.wrapper.JEIRecipeWrapperTimed;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JEIRecipeWrapperSawmill
    extends JEIRecipeWrapperTimed {

  private final List<List<ItemStack>> inputs;
  private final ItemStack output;

  public JEIRecipeWrapperSawmill(MachineRecipeBaseSawmill recipe) {

    super(recipe);

    this.inputs = new ArrayList<>(2);
    this.inputs.add(Arrays.asList(recipe.getInput().getMatchingStacks()));
    this.inputs.add(Arrays.asList(recipe.getBlade().getMatchingStacks()));

    this.output = recipe.getOutput();
  }

  @Override
  public void getIngredients(@Nonnull IIngredients ingredients) {

    ingredients.setInputLists(VanillaTypes.ITEM, this.inputs);
    ingredients.setOutput(VanillaTypes.ITEM, this.output);
  }

  @Override
  protected int getTimeDisplayY() {

    return super.getTimeDisplayY() - 2;
  }
}