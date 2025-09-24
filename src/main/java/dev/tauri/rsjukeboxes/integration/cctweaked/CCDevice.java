package dev.tauri.rsjukeboxes.integration.cctweaked;

import dev.tauri.rsjukeboxes.integration.cctweaked.methods.JukeboxCCMethods;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;

public class CCDevice {
    private static final HashMap<String, CCDevice> REGISTRY = new HashMap<>();


    public static final CCDevice JUKEBOX = new CCDevice("JUKEBOX", "jukebox", JukeboxCCMethods::new);


    public final String name;
    public final IConstructable constructor;
    public final String deviceName;

    public CCDevice(String name, String deviceName, IConstructable c) {
        this.name = name;
        this.deviceName = deviceName;
        this.constructor = c;
        REGISTRY.put(name, this);
    }

    public static CCDevice valueOf(String name) {
        return REGISTRY.get(name);
    }

    public interface IConstructable {
        Object construct(BlockEntity tile);
    }
}
