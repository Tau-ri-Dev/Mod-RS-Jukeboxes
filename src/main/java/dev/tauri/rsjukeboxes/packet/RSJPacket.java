package dev.tauri.rsjukeboxes.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public abstract class RSJPacket {
    public RSJPacket() {
    }

    public abstract void toBytes(FriendlyByteBuf buf);

    public abstract void fromBytes(FriendlyByteBuf buf);

    public abstract void handle(CustomPayloadEvent.Context ctx);

    public RSJPacket(FriendlyByteBuf buf) {
        fromBytes(buf);
    }
}
