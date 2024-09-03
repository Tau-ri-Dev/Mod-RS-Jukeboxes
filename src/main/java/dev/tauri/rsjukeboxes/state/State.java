package dev.tauri.rsjukeboxes.state;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;

@SuppressWarnings("unused")
public abstract class State {
    public abstract void toBytes(ByteBuf buf);

    public abstract void fromBytes(ByteBuf buf);

    public NbtCompound serializeNBT() {
        NbtCompound compound = new NbtCompound();

        ByteBuf buf = Unpooled.buffer();
        toBytes(buf);

        byte[] dst = new byte[buf.readableBytes()];
        buf.readBytes(dst);

        compound.putByteArray("byteArray", dst);

        return compound;
    }

    public void deserializeNBT(NbtCompound compound) {
        if (compound == null)
            return;

        byte[] dst = compound.getByteArray("byteArray");

        if (dst.length > 0) {
            ByteBuf buf = Unpooled.copiedBuffer(dst);
            fromBytes(buf);
        }
    }
}
