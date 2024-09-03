package dev.tauri.rsjukeboxes.packet;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.packet.packets.JukeboxActionPacketToServer;
import dev.tauri.rsjukeboxes.packet.packets.StateUpdateRequestToServer;
import dev.tauri.rsjukeboxes.util.TargetPoint;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

public class RSJPacketHandler {
    private RSJPacketHandler() {

    }

    public static void sendToClient(FabricPacket packet, TargetPoint point) {
        for (var player : point.dim().getOtherEntities(null, new Box(
                point.x() - point.radius(), point.y() - point.radius(), point.z() - point.radius(),
                point.x() + point.radius(), point.y() + point.radius(), point.z() + point.radius()
        ), (entity) -> entity instanceof ServerPlayerEntity)) {
            if (!(player instanceof ServerPlayerEntity sp)) continue;
            ServerPlayNetworking.send(sp, packet);
        }
    }

    public static void sendTo(FabricPacket packet, ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, packet);
    }

    public static void init() {
        registerPacket(new StateUpdateRequestToServer());
        registerPacket(new JukeboxActionPacketToServer());
    }

    public static void registerPacket(RSJPacket packet) {
        try {
            ServerPlayNetworking.registerGlobalReceiver(packet.getType(), (p, player, responseSender) -> ((RSJPacket) p).handle(player, responseSender));
        } catch (Exception e) {
            RSJukeboxes.logger.error("Could not register packet " + packet.getId() + ": ", e);
        }
    }
}
