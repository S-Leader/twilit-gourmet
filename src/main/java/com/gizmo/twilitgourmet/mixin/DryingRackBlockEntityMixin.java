package com.gizmo.twilitgourmet.mixin;

import com.gizmo.twilitgourmet.init.GourmetDataMaps;
import com.gizmo.twilitgourmet.item.recipe.CheeseAgingRecipe;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import twilightforest.block.entity.DryingRackBlockEntity;
import twilightforest.item.recipe.DryingRecipe;

@Mixin(DryingRackBlockEntity.class)
public class DryingRackBlockEntityMixin {

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Ltwilightforest/block/entity/DryingRackBlockEntity;setTheItem(Lnet/minecraft/world/item/ItemStack;)V"))
	private static void doAgingLogic(DryingRackBlockEntity instance, ItemStack newItem, Operation<Void> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos, @Local(name = "recipeholder") RecipeHolder<DryingRecipe> recipe) {
		if (recipe.value() instanceof CheeseAgingRecipe aging) {
			if (aging.getRotInfo().isPresent()) {
				CheeseAgingRecipe.RotInfo info = aging.getRotInfo().get();
				boolean rotConditionsMet = false;
				for (BlockPos checkPos : BlockPos.betweenClosedStream(new AABB(pos).inflate(info.checkRadius())).toList()) {
					if (info.checkSkylight() && level.canSeeSky(checkPos)) {
						rotConditionsMet = true;
						break;
					} else if (level.getBlockState(checkPos).is(info.rotCausers())) {
						rotConditionsMet = true;
						break;
					}
				}

				if (rotConditionsMet) {
					original.call(instance, info.rotsInto());
					return;
				}
			}

			ItemStack smokedItem = newItem.getItem().builtInRegistryHolder().getData(GourmetDataMaps.DRYING_RACK_SMOKING);
			//TODO tag this shit
			if (smokedItem != null && level.getBlockState(pos.below()).is(Blocks.CAMPFIRE)) {
				original.call(instance, smokedItem);
				return;
			}
		}
		original.call(instance, newItem);
	}
}
