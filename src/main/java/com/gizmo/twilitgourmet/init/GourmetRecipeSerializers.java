package com.gizmo.twilitgourmet.init;

import com.gizmo.twilitgourmet.item.recipe.CheeseAgingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import twilightforest.TwilightForestMod;

public class GourmetRecipeSerializers {

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, TwilightForestMod.ID);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CheeseAgingRecipe>> CHEESE_AGING_SERIALIZER = RECIPE_SERIALIZERS.register("cheese_aging", CheeseAgingRecipe.Serializer::new);
}
