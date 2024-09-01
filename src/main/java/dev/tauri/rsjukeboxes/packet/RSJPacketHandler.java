package dev.tauri.rsjukeboxes.packet;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.packet.packets.JukeboxActionPacketToServer;
import dev.tauri.rsjukeboxes.packet.packets.StateUpdatePacketToClient;
import dev.tauri.rsjukeboxes.packet.packets.StateUpdateRequestToServer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.Objects;
import java.util.function.Function;

public class RSJPacketHandler {
    private RSJPacketHandler() {

    }

    public static void sendToServer(Object packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void sendToClient(Object packet, PacketDistributor.TargetPoint point) {
        INSTANCE.send(packet, PacketDistributor.NEAR.with(point));
    }

    public static void sendTo(Object packet, ServerPlayer player) {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(player));
    }

    public static final int NETWORK_VERSION = 1;

    private static final SimpleChannel INSTANCE = ChannelBuilder.named(new ResourceLocation(RSJukeboxes.MOD_ID, "main"))
            .clientAcceptedVersions((status, version) -> Objects.equals(version, NETWORK_VERSION))
            .serverAcceptedVersions((status, version) -> Objects.equals(version, NETWORK_VERSION))
            .networkProtocolVersion(NETWORK_VERSION)
            .simpleChannel();

    public static void init() {
        int index = -1;
        // to server
        registerPacket(StateUpdateRequestToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, StateUpdateRequestToServer::new);
        registerPacket(JukeboxActionPacketToServer.class, ++index, NetworkDirection.PLAY_TO_SERVER, JukeboxActionPacketToServer::new);

        // to client
        registerPacket(StateUpdatePacketToClient.class, ++index, NetworkDirection.PLAY_TO_CLIENT, StateUpdatePacketToClient::new);
    }

    public static <MSG extends RSJPacket> void registerPacket(Class<MSG> clazz, int id, NetworkDirection direction, Function<FriendlyByteBuf, MSG> decoder) {
        try {
            INSTANCE.messageBuilder(clazz, id, direction)
                    .encoder(RSJPacket::toBytes)
                    .decoder(decoder)
                    .consumerNetworkThread(RSJPacket::handle)
                    .add();
        } catch (Exception e) {
            RSJukeboxes.logger.error("Could not register packet " + id + ": ", e);
        }
    }
}
