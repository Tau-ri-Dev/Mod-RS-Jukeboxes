package dev.tauri.rsjukeboxes.integration.cctweaked.methods;

import dan200.computercraft.api.lua.LuaFunction;
import dev.tauri.rsjukeboxes.blockentity.AbstractTieredJukeboxBE;
import dev.tauri.rsjukeboxes.integration.cctweaked.CCDevice;
import dev.tauri.rsjukeboxes.util.PlaySlotSelectResult;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.entity.BlockEntity;

public class JukeboxCCMethods extends AbstractCCMethods<AbstractTieredJukeboxBE> {
    public JukeboxCCMethods(BlockEntity deviceTile) {
        super((AbstractTieredJukeboxBE) deviceTile, CCDevice.JUKEBOX);
    }

    @LuaFunction(mainThread = true)
    public final Object[] play() {
        if (deviceTile.isPlaying()) return new Object[]{false, "already_playing", "Jukebox is already playing music"};
        if (!deviceTile.hasPlayableItem()) {
            return new Object[]{false, "missing_disc", "Jukebox is missing a music disc"};
        }
        deviceTile.startPlaying();
        return new Object[]{true, "playing", "Jukebox started playing some music"};
    }

    @LuaFunction(mainThread = true)
    public final Object[] stop() {
        if (!deviceTile.isPlaying()) return new Object[]{false, "not_playing", "Jukebox is not playing music"};
        deviceTile.stopPlayingAndDoNotSkip();
        return new Object[]{true, "stopping", "Jukebox stopped playing music"};
    }

    @LuaFunction(mainThread = true)
    public final Object[] next() {
        if (!deviceTile.hasPlayableItem()) {
            return new Object[]{false, "missing_disc", "Jukebox is missing a music disc"};
        }
        deviceTile.selectNextTrack();
        return new Object[]{true, "playing_next", "Playing next disc"};
    }

    @LuaFunction(mainThread = true)
    public final Object[] prev() {
        if (!deviceTile.hasPlayableItem()) {
            return new Object[]{false, "missing_disc", "Jukebox is missing a music disc"};
        }
        deviceTile.selectPreviousTrack();
        return new Object[]{true, "playing_previous", "Playing previous disc"};
    }

    @LuaFunction(mainThread = true)
    public final int getInventorySize() {
        return deviceTile.getContainerSize();
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlaying() {
        return deviceTile.isPlaying();
    }

    @LuaFunction(mainThread = true)
    public final long getPlayingStarted() {
        return deviceTile.playingStarted;
    }

    @LuaFunction(mainThread = true)
    public final double getProgress() {
        if (deviceTile.getLevel() == null) return 0;
        if (deviceTile.getPlayingItem() == null || !(deviceTile.getPlayingItem().getItem() instanceof RecordItem record))
            return 0;
        var progress = (double) (deviceTile.getLevel().getGameTime() - deviceTile.playingStarted);
        var length = (double) record.getLengthInTicks();
        if (length != 0) {
            var progressBarWidth = (progress / length);
            if (progressBarWidth > 1) progressBarWidth = 1;
            if (progressBarWidth < 0) progressBarWidth = 0;
            return progressBarWidth;
        }
        return 0;
    }

    @LuaFunction(mainThread = true)
    public final int getLength() {
        if (deviceTile.getLevel() == null) return 0;
        if (deviceTile.getPlayingItem() == null || !(deviceTile.getPlayingItem().getItem() instanceof RecordItem record))
            return 0;
        return record.getLengthInTicks();
    }

    @LuaFunction(mainThread = true)
    public final Object[] goTo(int slot) {
        var result = deviceTile.setSlotToPlay(slot);
        if (result == PlaySlotSelectResult.OUT_OF_BOUNDS)
            return new Object[]{false, "out_of_bounds", "Slot is out of bounds the inventory"};
        if (result == PlaySlotSelectResult.NO_DISC)
            return new Object[]{false, "missing_disc", "Jukebox is missing a music disc at this slot"};
        return new Object[]{true, "slot_set", "Active slot set to " + slot};
    }
}
