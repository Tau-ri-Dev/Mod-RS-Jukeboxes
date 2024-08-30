package dev.tauri.rsjukeboxes.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class RSJPacket {
    public RSJPacket() {
    }

    public abstract void toBytes(FriendlyByteBuf buf);

    public abstract void fromBytes(FriendlyByteBuf buf);

    public abstract void handle(NetworkEvent.Context ctx);

    public boolean handleSupplier(Supplier<NetworkEvent.Context> contextSupplier) {
        handle(contextSupplier.get());
        return true;
    }

    public RSJPacket(FriendlyByteBuf buf) {
        fromBytes(buf);
    }
}
