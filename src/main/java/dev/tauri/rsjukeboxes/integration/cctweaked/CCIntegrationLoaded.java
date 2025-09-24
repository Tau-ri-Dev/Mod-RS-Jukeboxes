package dev.tauri.rsjukeboxes.integration.cctweaked;

import dan200.computercraft.api.peripheral.IPeripheral;
import dev.tauri.rsjukeboxes.integration.ComputerDeviceProvider;
import dev.tauri.rsjukeboxes.integration.cctweaked.methods.AbstractCCMethods;
import dev.tauri.rsjukeboxes.integration.cctweaked.methods.ICCDevice;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

@SuppressWarnings("unused")
public class CCIntegrationLoaded implements CCIntegrationWrapper {
    private static final Capability<IPeripheral> CAP_PERIPHERAL = CapabilityManager.get(new CapabilityToken<>() {
    });


    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public boolean checkCaps(Capability<?> caps) {
        return caps == CAP_PERIPHERAL;
    }

    @Override
    public Optional<Capability<?>> getCaps() {
        return Optional.of(CAP_PERIPHERAL);
    }

    @Override
    public <T> LazyOptional<ICCDevice> createDevice(Capability<T> cap, ComputerDeviceProvider provider, String deviceType) {
        return LazyOptional.of(() -> (AbstractCCMethods<?>) CCDevice.valueOf(deviceType.toUpperCase()).constructor.construct((BlockEntity) provider)).cast();
    }
}
