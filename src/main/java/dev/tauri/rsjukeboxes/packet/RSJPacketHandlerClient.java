package dev.tauri.rsjukeboxes.packet;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.packet.packets.StateUpdatePacketToClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class RSJPacketHandlerClient {
    public static void sendToServer(RSJPacket packet) {
        ClientPlayNetworking.send(packet);
    }

    public static void init() {
        registerPacket(new StateUpdatePacketToClient());
    }

    public static void registerPacket(RSJPacket packet) {
        try {
            ClientPlayNetworking.registerGlobalReceiver(packet.getType(), (p, player, responseSender) -> ((RSJPacket) p).handle(player, responseSender));
        } catch (Exception e) {
            RSJukeboxes.logger.error("Could not register packet " + packet.getId() + ": ", e);
        }
    }
}
