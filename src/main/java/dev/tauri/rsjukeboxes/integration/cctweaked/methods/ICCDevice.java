package dev.tauri.rsjukeboxes.integration.cctweaked.methods;

public interface ICCDevice {
    void sendSignal(String eventName, Object... objects);

    void connectToWirelessNetwork();

    void disconnectFromWirelessNetwork();
}
