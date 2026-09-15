package com.gizmo.twilightgourmet.datagen.helpers;

import com.gizmo.twilitgourmet.item.recipe.CheeseAgingRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderSet;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CheeseAgingRecipeBuilder implements RecipeBuilder {
	private final Ingredient input;
	private final ItemStack result;
	private final int time;
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	@Nullable
	private CheeseAgingRecipe.RotInfo info;

	private CheeseAgingRecipeBuilder(Ingredient input, ItemStack result, int dryingTime) {
		this.input = input;
		this.result = result;
		this.time = dryingTime;
	}

	public static CheeseAgingRecipeBuilder aging(ItemLike input, ItemLike result) {
		return aging(Ingredient.of(input), new ItemStack(result));
	}

	public static CheeseAgingRecipeBuilder aging(ItemLike input, ItemLike result, float dryingMinutes) {
		return aging(Ingredient.of(input), new ItemStack(result), dryingMinutes);
	}

	public static CheeseAgingRecipeBuilder aging(Ingredient input, ItemStack result) {
		return aging(input, result, 5);
	}

	public static CheeseAgingRecipeBuilder aging(Ingredient input, ItemStack result, float dryingMinutes) {
		return new CheeseAgingRecipeBuilder(input, result, (int) (20 * 60 * dryingMinutes));
	}

	public CheeseAgingRecipeBuilder withRottingCriteria(ItemLike rotInto, int scanRadius, boolean checkSkylight, HolderSet<Block> rotCausers) {
		return this.withRottingCriteria(new ItemStack(rotInto), scanRadius, checkSkylight, rotCausers);
	}

	public CheeseAgingRecipeBuilder withRottingCriteria(ItemStack rotInto, int scanRadius, boolean checkSkylight, HolderSet<Block> rotCausers) {
		this.info = new CheeseAgingRecipe.RotInfo(rotInto, scanRadius, checkSkylight, rotCausers);
		return this;
	}

	public CheeseAgingRecipeBuilder unlockedBy(String key, Criterion<?> criterion) {
		this.criteria.put(key, criterion);
		return this;
	}

	@Override
	public RecipeBuilder group(@Nullable String groupName) {
		return this;
	}

	@Override
	public Item getResult() {
		return this.result.getItem();
	}

	@Override
	public void save(RecipeOutput output, ResourceLocation id) {
		this.ensureValid(id);
		Advancement.Builder builder = output.advancement()
			.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
			.rewards(AdvancementRewards.Builder.recipe(id))
			.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(builder::addCriterion);
		CheeseAgingRecipe recipe = new CheeseAgingRecipe(this.input, this.result, this.time, Optional.ofNullable(this.info));
		output.accept(id, recipe, builder.build(id.withPrefix("recipes/aging/")));
	}

	private void ensureValid(ResourceLocation location) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + location);
		}
	}
}
