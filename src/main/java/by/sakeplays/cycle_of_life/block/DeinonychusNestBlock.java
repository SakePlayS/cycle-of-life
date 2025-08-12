package by.sakeplays.cycle_of_life.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DeinonychusNestBlock extends Block implements EntityBlock {
    public DeinonychusNestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DeinonychusNestBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.DEINONYCHUS_NEST_BLOCK_ENTITY.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof DeinonychusNestBlockEntity animBe) {
                    if (!lvl.isClientSide) {
                        animBe.serverTick(lvl, pos, st);
                    }
                }
            };
        }
        return null;
    }
}
