package dev.tauri.rsjukeboxes.renderer;

import dev.tauri.rsjukeboxes.state.State;
import io.netty.buffer.ByteBuf;

public class GenericRSJukeboxRendererState extends State {
    public boolean playing = false;
    public long playingStarted;
    public long playingStopped;
    public boolean discInserted = false;
    public int discItemId = -1;
    public int selectedSlot = 0;
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(playing);
        buf.writeBoolean(discInserted);
        buf.writeInt(discItemId);
        buf.writeLong(playingStarted);
        buf.writeLong(playingStopped);
        buf.writeInt(selectedSlot);
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        playing = buf.readBoolean();
        discInserted = buf.readBoolean();
        discItemId = buf.readInt();
        playingStarted = buf.readLong();
        playingStopped = buf.readLong();
        selectedSlot = buf.readInt();
    }
}
