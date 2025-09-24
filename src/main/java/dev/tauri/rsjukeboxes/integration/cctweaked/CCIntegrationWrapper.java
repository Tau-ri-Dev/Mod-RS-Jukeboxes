package dev.tauri.rsjukeboxes.integration.cctweaked;

import dev.tauri.rsjukeboxes.integration.ComputerDeviceProvider;
import dev.tauri.rsjukeboxes.integration.cctweaked.methods.ICCDevice;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

public interface CCIntegrationWrapper {
    boolean isLoaded();

    boolean checkCaps(Capability<?> caps);

    Optional<Capability<?>> getCaps();

    <T> LazyOptional<ICCDevice> createDevice(Capability<T> cap, ComputerDeviceProvider tile, String deviceType);
}
