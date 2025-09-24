package dev.tauri.rsjukeboxes.integration.cctweaked.methods;

import dan200.computercraft.api.lua.LuaFunction;
import dev.tauri.rsjukeboxes.blockentity.AbstractTieredJukeboxBE;
import dev.tauri.rsjukeboxes.integration.cctweaked.CCDevice;
import net.minecraft.world.level.block.entity.BlockEntity;

public class JukeboxCCMethods extends AbstractCCMethods<AbstractTieredJukeboxBE> {
    public JukeboxCCMethods(BlockEntity deviceTile) {
        super((AbstractTieredJukeboxBE) deviceTile, CCDevice.JUKEBOX);
    }

    @LuaFunction(mainThread = true)
    public final Object[] play() {
        if (deviceTile.isPlaying()) return new Object[]{false, "already_playing", "Jukebox is already playing music"};
        deviceTile.startPlaying();
        return new Object[]{true, "playing", "Jukebox started playing some music"};
    }

    @LuaFunction(mainThread = true)
    public final Object[] stop() {
        if (!deviceTile.isPlaying()) return new Object[]{false, "not_playing", "Jukebox is not playing music"};
        deviceTile.stopPlayingAndDoNotSkip();
        return new Object[]{true, "stopping", "Jukebox stopped playing music"};
    }
}
