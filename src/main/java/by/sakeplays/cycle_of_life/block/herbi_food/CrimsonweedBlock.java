package by.sakeplays.cycle_of_life.block.herbi_food;

import by.sakeplays.cycle_of_life.block.DeinonychusNestBlockEntity;
import by.sakeplays.cycle_of_life.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CrimsonweedBlock extends Block implements EntityBlock {

    private static final VoxelShape BASE = Block.box(0, 0, 0, 16 ,16, 16);


    public CrimsonweedBlock(Properties properties) {
        super(properties);
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BASE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CrimsonweedBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.CRIMSONWEED_BLOCK_ENTITY.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof CrimsonweedBlockEntity animBe) {
                    if (!lvl.isClientSide) {
                        animBe.serverTick(lvl, pos, st);
                    }
                }
            };
        }
        return null;
    }
}
