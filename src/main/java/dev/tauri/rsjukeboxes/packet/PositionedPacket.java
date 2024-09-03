package dev.tauri.rsjukeboxes.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public abstract class PositionedPacket extends RSJPacket {
    public PositionedPacket() {
    }

    protected BlockPos pos;

    public PositionedPacket(BlockPos pos) {
        this.pos = pos;
    }

    public PositionedPacket(PacketByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public void fromBytes(PacketByteBuf buf) {
        pos = buf.readBlockPos();
    }
}
