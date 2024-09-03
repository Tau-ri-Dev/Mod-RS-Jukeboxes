package dev.tauri.rsjukeboxes.packet;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public abstract class RSJPacket implements FabricPacket {
    public RSJPacket() {
    }

    public abstract Identifier getId();

    public abstract void toBytes(PacketByteBuf buf);

    public abstract void fromBytes(PacketByteBuf buf);

    public abstract void handle(PlayerEntity player, PacketSender responseSender);

    public RSJPacket(PacketByteBuf buf) {
        fromBytes(buf);
    }

    public final void write(PacketByteBuf buf) {
        toBytes(buf);
    }

    public final PacketType<FabricPacket> getType() {
        return PacketType.create(getId(), (buf) -> {
            try {
                return getClass().getConstructor(PacketByteBuf.class).newInstance(buf);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
