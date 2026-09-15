package com.gizmo.twilitgourmet.item.recipe;

import com.gizmo.twilitgourmet.init.GourmetRecipeSerializers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import twilightforest.item.recipe.DryingRecipe;

import java.util.Optional;

public class CheeseAgingRecipe extends DryingRecipe {

	private final Optional<RotInfo> rotInfo;
	
	public CheeseAgingRecipe(Ingredient ingredient, ItemStack result, int dryingTime, Optional<RotInfo> rotInfo) {
		super(ingredient, result, dryingTime);
		this.rotInfo = rotInfo;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return GourmetRecipeSerializers.CHEESE_AGING_SERIALIZER.get();
	}

	public Optional<RotInfo> getRotInfo() {
		return this.rotInfo;
	}

	public static class Serializer implements RecipeSerializer<CheeseAgingRecipe> {

		public static final MapCodec<CheeseAgingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(DryingRecipe::getInput),
				ItemStack.STRICT_CODEC.fieldOf("result").forGetter(DryingRecipe::getResult),
				Codec.INT.fieldOf("aging_time").forGetter(DryingRecipe::getDryingTime),
				RotInfo.CODEC.optionalFieldOf("rot_handler").forGetter(CheeseAgingRecipe::getRotInfo)
		).apply(instance, CheeseAgingRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, CheeseAgingRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC, DryingRecipe::getInput,
				ItemStack.STREAM_CODEC, DryingRecipe::getResult,
				ByteBufCodecs.INT, DryingRecipe::getDryingTime,
				ByteBufCodecs.optional(RotInfo.STREAM_CODEC), CheeseAgingRecipe::getRotInfo,
				CheeseAgingRecipe::new);

		@Override
		public MapCodec<CheeseAgingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CheeseAgingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
	
	public record RotInfo(ItemStack rotsInto, int checkRadius, boolean checkSkylight, HolderSet<Block> rotCausers) {
		public static final Codec<RotInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ItemStack.STRICT_CODEC.fieldOf("rots_into").forGetter(RotInfo::rotsInto),
				Codec.INT.fieldOf("check_radius").forGetter(RotInfo::checkRadius),
				Codec.BOOL.fieldOf("check_skylight").forGetter(RotInfo::checkSkylight),
				RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("rot_causers").forGetter(RotInfo::rotCausers)
		).apply(instance, RotInfo::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, RotInfo> STREAM_CODEC = StreamCodec.composite(
				ItemStack.STREAM_CODEC, RotInfo::rotsInto,
				ByteBufCodecs.INT, RotInfo::checkRadius,
				ByteBufCodecs.BOOL, RotInfo::checkSkylight,
				ByteBufCodecs.holderSet(Registries.BLOCK), RotInfo::rotCausers,
				RotInfo::new);
	}
}
