package com.gizmo.twilightgourmet.datagen.data.loot;

import com.gizmo.twilitgourmet.block.FiddleheadCropBlock;
import com.gizmo.twilitgourmet.block.GiantAppleBlock;
import com.gizmo.twilitgourmet.block.PancakeStackBlock;
import com.gizmo.twilitgourmet.init.GourmetBlocks;
import com.gizmo.twilitgourmet.init.GourmetItems;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.loot.CanItemPerformAbility;
import twilightforest.init.TFBlocks;
import vectorwing.farmersdelight.common.block.FeastBlock;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;
import vectorwing.farmersdelight.common.item.KnifeItem;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockLootTables extends BlockLootSubProvider {

	protected static final LootItemCondition.Builder HAS_SHEARS = CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST);

	public BlockLootTables(HolderLookup.Provider registries) {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
	}

	@Override
	protected void generate() {
		this.add(GourmetBlocks.GIANT_APPLE.get(), LootTable.lootTable().withPool(LootPool.lootPool()
				.add(AlternativesEntry.alternatives(AlternativesEntry.alternatives(GiantAppleBlock.SLICES.getPossibleValues(), slices -> {
					if (slices != 8) {
						return LootItem.lootTableItem(GourmetItems.APPLE_SLICE.get())
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(GourmetBlocks.GIANT_APPLE.get())
										.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(GiantAppleBlock.SLICES, slices)))
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(slices)));
					}
					return LootItem.lootTableItem(GourmetBlocks.GIANT_APPLE.get());
				})))));

		this.dropSelf(GourmetBlocks.TREE_TAPPER.get());
		this.dropOther(GourmetBlocks.SYRUP_CAULDRON.get(), Blocks.CAULDRON);

		this.dropNamedContainer(GourmetBlocks.TWILIGHT_OAK_CABINET.get());
		this.dropNamedContainer(GourmetBlocks.CANOPY_CABINET.get());
		this.dropNamedContainer(GourmetBlocks.MANGROVE_CABINET.get());
		this.dropNamedContainer(GourmetBlocks.DARK_CABINET.get());
		this.dropNamedContainer(GourmetBlocks.TIME_CABINET.get());
		this.dropNamedContainer(GourmetBlocks.TRANSFORMATION_CABINET.get());
		this.dropNamedContainer(GourmetBlocks.MINING_CABINET.get());
		this.dropNamedContainer(GourmetBlocks.SORTING_CABINET.get());
		this.add(GourmetBlocks.MUSHGLOOM_COLONY.get(), block -> this.mushroomColony(block, TFBlocks.MUSHGLOOM));
		this.add(GourmetBlocks.PANCAKE_STACK.get(), block -> LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(block))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
								.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PancakeStackBlock.PANCAKES, 3))))
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(Items.BOWL))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
								.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PancakeStackBlock.PANCAKES, 3)).invert())));
		this.add(GourmetBlocks.CRAB_CAKES.get(), block -> LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(block))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
								.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, 4))))
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(Items.BOWL))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
								.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, 4)).invert())));

		this.add(GourmetBlocks.COOKED_CRAB.get(), block -> LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(block))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
								.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, 6))))
				//only drop fragments if the plate isnt empty or full
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(GourmetItems.CRAB_SHELL_FRAGMENT)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
								.when(AnyOfCondition.anyOf(
										LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
												.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, 6)),
										LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
												.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, 0))).invert())))
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(Items.BOWL))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
								.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, 6)).invert())));

		this.add(GourmetBlocks.MAZE_CHEESECAKE.get(), LootTable.lootTable());
		this.add(GourmetBlocks.CREMESCHNITTE.get(), LootTable.lootTable());
		this.add(GourmetBlocks.CRAB_QUICHE.get(), LootTable.lootTable());
		this.dropWhenSilkTouch(GourmetBlocks.BREADCRUMBS.get());

		this.add(GourmetBlocks.FIDDLEHEAD.get(), block -> {
			var fullyGrown = LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FiddleheadCropBlock.AGE, 3));
			return this.applyExplosionDecay(block, LootTable.lootTable()
					//single variant
					.withPool(LootPool.lootPool().add(EntryGroup.list(
									LootItem.lootTableItem(GourmetItems.FIDDLEHEAD)
											.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
											.when(fullyGrown)
											.otherwise(LootItem.lootTableItem(GourmetItems.FIDDLEHEAD_SPORES)),

									LootItem.lootTableItem(GourmetItems.FIDDLEHEAD_SPORES)
											.when(fullyGrown)
											.apply(ApplyBonusCount.addBonusBinomialDistributionCount(this.registries.holderOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))))
							.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FiddleheadCropBlock.VARIANT, FiddleheadCropBlock.Variant.SINGLE))))
					//upper half broken
					.withPool(LootPool.lootPool().add(EntryGroup.list(
									LootItem.lootTableItem(GourmetItems.FIDDLEHEAD)
											.apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 5.0F)))
											.when(fullyGrown)
											.otherwise(LootItem.lootTableItem(GourmetItems.FIDDLEHEAD_SPORES)),

									LootItem.lootTableItem(GourmetItems.FIDDLEHEAD_SPORES)
											.when(fullyGrown)
											.apply(ApplyBonusCount.addBonusBinomialDistributionCount(this.registries.holderOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))))
							.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FiddleheadCropBlock.VARIANT, FiddleheadCropBlock.Variant.UPPER)))
							.when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(block)
									.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FiddleheadCropBlock.VARIANT, FiddleheadCropBlock.Variant.LOWER))
							), new BlockPos(0, -1, 0))))
					//lower half broken
					.withPool(LootPool.lootPool().add(EntryGroup.list(
									LootItem.lootTableItem(GourmetItems.FIDDLEHEAD)
											.apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 5.0F)))
											.when(fullyGrown)
											.otherwise(LootItem.lootTableItem(GourmetItems.FIDDLEHEAD_SPORES)),

									LootItem.lootTableItem(GourmetItems.FIDDLEHEAD_SPORES)
											.when(fullyGrown)
											.apply(ApplyBonusCount.addBonusBinomialDistributionCount(this.registries.holderOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))))
							.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FiddleheadCropBlock.VARIANT, FiddleheadCropBlock.Variant.LOWER)))
							.when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(block)
									.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FiddleheadCropBlock.VARIANT, FiddleheadCropBlock.Variant.UPPER))
							), new BlockPos(0, 1, 0)))));
		});

		this.add(TFBlocks.FIDDLEHEAD.get(), block -> this.applyExplosionDecay(block, LootTable.lootTable()
				.withPool(LootPool.lootPool().add(LootItem.lootTableItem(block).when(HAS_SHEARS)))
				.withPool(LootPool.lootPool().add(LootItem.lootTableItem(GourmetItems.FIDDLEHEAD_SPORES).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
						.when(CanItemPerformAbility.canItemPerformAbility(KnifeItem.KNIFE_HARVEST)))));
	}

	protected void dropNamedContainer(Block block) {
		this.add(block, this::createNameableBlockEntityTable);
	}

	protected LootTable.Builder mushroomColony(Block block, ItemLike mushroom) {
		return this.applyExplosionDecay(block, LootTable.lootTable().withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1.0F))
				.add(AlternativesEntry.alternatives(
						LootItem.lootTableItem(mushroom)
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
										.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MushroomColonyBlock.COLONY_AGE, 0))),
						LootItem.lootTableItem(mushroom)
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F)))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
										.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MushroomColonyBlock.COLONY_AGE, 1))),
						LootItem.lootTableItem(mushroom)
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F)))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
										.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MushroomColonyBlock.COLONY_AGE, 2))),
						//fully grown should only drop mushrooms if not harvested by shears
						LootItem.lootTableItem(mushroom)
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(5.0F)))
								.when(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST).invert())
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
										.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MushroomColonyBlock.COLONY_AGE, 3))),
						//if broken with shears at max age, drop self
						LootItem.lootTableItem(block)
								.when(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
										.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MushroomColonyBlock.COLONY_AGE, 3)))))));
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return Stream.concat(Stream.of(TFBlocks.FIDDLEHEAD.get()), GourmetBlocks.BLOCKS.getEntries().stream().map(Supplier::get)).collect(Collectors.toList());
	}
}
