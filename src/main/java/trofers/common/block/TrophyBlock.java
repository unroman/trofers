package trofers.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import trofers.common.init.ModBlockEntityTypes;
import trofers.common.block.entity.TrophyBlockEntity;
import trofers.common.block.entity.TrophyScreen;
import trofers.common.trophy.Trophy;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TrophyBlock extends Block {

    private final int size;

    public TrophyBlock(Properties properties, int size) {
        super(properties);
        this.size = size;
        registerDefaultState(
                defaultBlockState()
                        .setValue(BlockStateProperties.WATERLOGGED, false)
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        );
    }

    public abstract int getHeight();

    public int getSize() {
        return size;
    }

    @Override
    public String getDescriptionId() {
        return "block.trofers.trophy";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        Trophy trophy = Trophy.getTrophy(stack);
        if (trophy != null) {
            tooltip.addAll(trophy.tooltip());
        }
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (level.getBlockEntity(pos) instanceof TrophyBlockEntity
                && placer instanceof PlayerEntity
                && !level.isClientSide()
                && ((PlayerEntity) placer).isCreative()) {
            // noinspection ConstantConditions
            ((TrophyBlockEntity) blockEntity).removeCooldown();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.WATERLOGGED);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        boolean isWaterlogged = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite())
                .setValue(BlockStateProperties.WATERLOGGED, isWaterlogged);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos pos, BlockPos facingPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModBlockEntityTypes.TROPHY.get().create();
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack result = super.getPickBlock(state, target, world, pos, player);
        if (world.getBlockEntity(pos) instanceof TrophyBlockEntity) {
            TrophyBlockEntity blockEntity = ((TrophyBlockEntity) world.getBlockEntity(pos));
            // noinspection ConstantConditions
            if (blockEntity.getTrophyID() != null) {
                result.getOrCreateTagElement("BlockEntityTag").putString("Trophy", blockEntity.getTrophyID().toString());
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
        if (player.isCreative()) {
            if (level.isClientSide()) {
                TrophyScreen.open(state.getBlock().asItem(), pos);
            }
            return ActionResultType.sidedSuccess(level.isClientSide());
        }
        if (level.getBlockEntity(pos) instanceof TrophyBlockEntity) {
            // noinspection ConstantConditions
            if (((TrophyBlockEntity) level.getBlockEntity(pos)).applyEffect(player, hand)) {
                return ActionResultType.sidedSuccess(level.isClientSide());
            }
        }
        return ActionResultType.PASS;
    }
}
