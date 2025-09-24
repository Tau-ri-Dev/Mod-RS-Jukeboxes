package dev.tauri.rsjukeboxes.integration;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.integration.cctweaked.methods.ICCDevice;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class ComputerDeviceHolder {
    private LazyOptional<ICCDevice> ccDevice = LazyOptional.empty();

    public final ComputerDeviceProvider provider; // usually stargate block entity

    public ComputerDeviceHolder(ComputerDeviceProvider provider) {
        this.provider = provider;
    }

    public <T> LazyOptional<T> getOrCreateDeviceBasedOnCap(Capability<T> cap) {
        var cc = getOrCreateCCDevice(cap);
        if (cc.isPresent()) return cc;
        return LazyOptional.empty();
    }

    public <T> LazyOptional<T> getOrCreateCCDevice(Capability<T> cap) {
        if (!RSJukeboxes.ccWrapper.isLoaded()) return LazyOptional.empty();
        if (RSJukeboxes.ccWrapper.checkCaps(cap)) {
            if (ccDevice.isPresent()) return ccDevice.cast();
            ccDevice = RSJukeboxes.ccWrapper.createDevice(cap, provider, provider.getDeviceType());
            return ccDevice.cast();
        }
        return LazyOptional.empty();
    }


    public void sendSignal(String eventName, Object... objects) {
        sendSignalCC(eventName, objects);
    }

    public void connectToWirelessNetwork() {
        connectToWirelessNetworkCC();
    }

    public void disconnectFromWirelessNetwork() {
        disconnectFromWirelessNetworkCC();
    }


    // WIRED SIGNALS
    public void sendSignalCC(String eventName, Object... objects) {
        var caps = RSJukeboxes.ccWrapper.getCaps();
        if (caps.isEmpty()) return;
        var opt = getOrCreateCCDevice(caps.get());
        if (!opt.isPresent()) return;
        var peripheral = opt.resolve().orElseThrow();
        if (!(peripheral instanceof ICCDevice device)) return;
        device.sendSignal(eventName, objects);
    }

    // WIRELESS NETWORK CONNECTION
    public void connectToWirelessNetworkCC() {
        var caps = RSJukeboxes.ccWrapper.getCaps();
        if (caps.isEmpty()) return;
        var opt = getOrCreateCCDevice(caps.get());
        if (!opt.isPresent()) return;
        var peripheral = opt.resolve().orElseThrow();
        if (!(peripheral instanceof ICCDevice device)) return;
        device.connectToWirelessNetwork();
    }

    public void disconnectFromWirelessNetworkCC() {
        var caps = RSJukeboxes.ccWrapper.getCaps();
        if (caps.isEmpty()) return;
        var opt = getOrCreateCCDevice(caps.get());
        if (!opt.isPresent()) return;
        var peripheral = opt.resolve().orElseThrow();
        if (!(peripheral instanceof ICCDevice device)) return;
        device.disconnectFromWirelessNetwork();
    }
}
