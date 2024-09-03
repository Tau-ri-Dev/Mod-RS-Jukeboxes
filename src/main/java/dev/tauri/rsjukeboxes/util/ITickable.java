package dev.tauri.rsjukeboxes.util;


import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;

public interface ITickable {
    void tick();

    static <T extends BlockEntity> BlockEntityTicker<T> getTickerHelper() {
        return (level0, pos0, state0, be0) -> {
            if (be0.getWorld() == null)
                return;
            ((ITickable) be0).tick();
        };
    }
}
