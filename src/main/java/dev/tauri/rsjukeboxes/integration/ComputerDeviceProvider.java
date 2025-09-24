package dev.tauri.rsjukeboxes.integration;

public interface ComputerDeviceProvider {
    String getDeviceType();

    ComputerDeviceHolder getDeviceHolder();

    default void sendSignal(String eventName, Object... objects) {
        getDeviceHolder().sendSignal(eventName, objects);
    }
}
