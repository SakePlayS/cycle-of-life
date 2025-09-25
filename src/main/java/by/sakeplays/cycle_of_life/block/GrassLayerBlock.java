package by.sakeplays.cycle_of_life.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrassLayerBlock extends Block {
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);
    private static final VoxelShape[] SHAPES = new VoxelShape[9];

    static {
        for (int i = 0; i <= 8; ++i) {
            SHAPES[i] = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D * i, 16.0D);
        }
    }

    public GrassLayerBlock(Properties properties) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)
                .strength(0.1F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(LAYERS)];
    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.is(this)) {
            int layers = state.getValue(LAYERS);
            return state.setValue(LAYERS, Math.min(8, layers + 1));
        }
        return this.defaultBlockState();
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && state.getValue(LAYERS) < 8
                || super.canBeReplaced(state, context);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }
}
