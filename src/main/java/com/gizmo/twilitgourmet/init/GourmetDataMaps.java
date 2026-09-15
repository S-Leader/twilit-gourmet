package com.gizmo.twilitgourmet.init;

import com.gizmo.twilitgourmet.Syrup;
import com.gizmo.twilitgourmet.TwilitGourmet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class GourmetDataMaps {

	public static final DataMapType<Block, ResourceKey<Syrup>> SYRUP = DataMapType.builder(TwilitGourmet.prefix("syrup"), Registries.BLOCK, ResourceKey.codec(TwilitGourmet.SYRUP_KEY)).synced(ResourceKey.codec(TwilitGourmet.SYRUP_KEY), false).build();
	public static final DataMapType<Item, ItemStack> DRYING_RACK_SMOKING = DataMapType.builder(TwilitGourmet.prefix("drying_rack_smoking"), Registries.ITEM, ItemStack.CODEC).synced(ItemStack.CODEC, false).build();

}
