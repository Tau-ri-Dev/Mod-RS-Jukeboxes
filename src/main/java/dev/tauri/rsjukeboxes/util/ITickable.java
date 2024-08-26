package dev.tauri.rsjukeboxes.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface ITickable {
    void tick();

    static <T extends BlockEntity> BlockEntityTicker<T> getTickerHelper(){
        return (level0, pos0, state0, be0) -> {
            if(be0.getLevel() == null)
                return;
            ((ITickable) be0).tick();
        };
    }
}
