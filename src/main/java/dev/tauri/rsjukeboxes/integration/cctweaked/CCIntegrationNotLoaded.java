package dev.tauri.rsjukeboxes.integration.cctweaked;

import dev.tauri.rsjukeboxes.integration.ComputerDeviceProvider;
import dev.tauri.rsjukeboxes.integration.cctweaked.methods.ICCDevice;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

@SuppressWarnings("unused")
public class CCIntegrationNotLoaded implements CCIntegrationWrapper {
    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public boolean checkCaps(Capability<?> caps) {
        return false;
    }

    @Override
    public Optional<Capability<?>> getCaps() {
        return Optional.empty();
    }

    @Override
    public <T> LazyOptional<ICCDevice> createDevice(Capability<T> cap, ComputerDeviceProvider tile, String deviceType) {
        return LazyOptional.empty();
    }
}
