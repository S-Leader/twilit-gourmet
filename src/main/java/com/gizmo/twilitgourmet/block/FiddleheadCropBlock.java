package com.gizmo.twilitgourmet.block;

import com.gizmo.twilitgourmet.init.GourmetBlocks;
import com.gizmo.twilitgourmet.init.GourmetItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.CommonHooks;
import vectorwing.farmersdelight.common.registry.ModBlocks;

import javax.annotation.Nullable;
import java.util.Locale;

public class FiddleheadCropBlock extends CropBlock {

	public static final EnumProperty<Variant> VARIANT = EnumProperty.create("variant", Variant.class);
	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

	public FiddleheadCropBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected IntegerProperty getAgeProperty() {
		return AGE;
	}

	@Override
	public int getMaxAge() {
		return 3;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return GourmetItems.FIDDLEHEAD_SPORES;
	}

	@Override
	public BlockState updateShape(
			BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos
	) {
		if (state.getValue(VARIANT) != Variant.SINGLE) {
			//function like a DoublePlantBlock if not a single plant
			Variant variant = state.getValue(VARIANT);
			if (facing.getAxis() != Direction.Axis.Y
					|| variant == Variant.LOWER != (facing == Direction.UP)
					|| facingState.is(this) && facingState.getValue(VARIANT) != variant) {
				return variant == Variant.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
						? Blocks.AIR.defaultBlockState()
						: super.updateShape(state, facing, facingState, level, currentPos, facingPos);
			} else {
				return Blocks.AIR.defaultBlockState();
			}
		} else {
			return state.canSurvive(level, currentPos) ? state : Blocks.AIR.defaultBlockState();
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (isNotTopPlant(state)) {
			return super.canSurvive(state, level, pos);
		} else {
			BlockState blockstate = level.getBlockState(pos.below());
			if (state.getBlock() != this)
				return super.canSurvive(state, level, pos); //This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
			return blockstate.is(this) && blockstate.getValue(VARIANT) == Variant.LOWER;
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE, VARIANT);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		return false;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return state.getValue(VARIANT) != Variant.LOWER;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		if (level.getRawBrightness(pos, 0) >= 9) {
			float f = getGrowthSpeed(state, level, pos);
			if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
				this.grow(level, state, pos, 1);
				CommonHooks.fireCropGrowPost(level, pos, state);
			}
		}
	}

	private void grow(ServerLevel level, BlockState state, BlockPos pos, int ageIncrement) {
		int i = Math.min(state.getValue(AGE) + ageIncrement, 3);
		if (this.canGrow(level, pos, state)) {
			BlockState blockstate = state.setValue(AGE, i);
			//TODO tag for double plant growth
			if (this.isMaxAge(state) && level.getBlockState(pos.below()).is(ModBlocks.RICH_SOIL_FARMLAND.get())) {
				level.setBlock(pos, blockstate.setValue(VARIANT, Variant.LOWER), 2);
				level.setBlock(pos.above(), state.setValue(AGE, 0).setValue(VARIANT, Variant.UPPER), 3);
			} else {
				level.setBlock(pos, blockstate, 2);
			}
		}
	}

	private static boolean canGrowInto(LevelReader level, BlockPos pos) {
		BlockState blockstate = level.getBlockState(pos);
		return blockstate.isAir() || blockstate.is(GourmetBlocks.FIDDLEHEAD);
	}


	private static boolean isNotTopPlant(BlockState state) {
		return state.is(GourmetBlocks.FIDDLEHEAD) && state.getValue(VARIANT) != Variant.UPPER;
	}

	private boolean canGrow(LevelReader reader, BlockPos pos, BlockState state) {
		//single variant can grow again if its max age  to turn it into a double block
		if (state.getValue(VARIANT) == Variant.SINGLE) {
			return hasSufficientLight(reader, pos) && (!this.isMaxAge(state) || canGrowInto(reader, pos.above()));
		}
		//otherwise, only the top can grow if its not max aged
		return state.getValue(VARIANT) == Variant.UPPER && !this.isMaxAge(state) && hasSufficientLight(reader, pos);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
		if (state.getValue(VARIANT) == Variant.LOWER) {
			return this.canGrow(level, pos.above(), level.getBlockState(pos.above()));
		}
		return this.canGrow(level, pos, state);
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
		if (state.getValue(VARIANT) == Variant.LOWER) {
			this.grow(level, level.getBlockState(pos.above()), pos.above(), 1);
		} else {
			this.grow(level, state, pos, 1);
		}
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide()) {
			if (player.isCreative() || EnchantmentHelper.has(player.getMainHandItem(), EnchantmentEffectComponents.ARMOR_EFFECTIVENESS)) {
				preventDropFromBottomPart(level, pos, state, player);
			} else {
				dropResources(state, level, pos, null, player, player.getMainHandItem());
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	/**
	 * Called after a player has successfully harvested this block. This method will only be called if the player has used the correct tool and drops should be spawned.
	 */
	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
		super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), te, stack);
	}

	protected static void preventDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
		Variant variant = state.getValue(VARIANT);
		if (variant == Variant.UPPER) {
			BlockPos blockpos = pos.below();
			BlockState blockstate = level.getBlockState(blockpos);
			if (blockstate.is(state.getBlock()) && blockstate.getValue(VARIANT) == Variant.LOWER) {
				BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
				level.setBlock(blockpos, blockstate1, 35);
				level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
			}
		}
	}

	public enum Variant implements StringRepresentable {
		SINGLE,
		UPPER,
		LOWER;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
