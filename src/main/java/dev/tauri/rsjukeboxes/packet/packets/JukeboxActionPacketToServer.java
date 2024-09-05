package dev.tauri.rsjukeboxes.packet.packets;

import dev.tauri.rsjukeboxes.blockentity.AbstractTieredJukeboxBE;
import dev.tauri.rsjukeboxes.packet.PositionedPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class JukeboxActionPacketToServer extends PositionedPacket {
    public enum JukeboxAction {
        NONE,
        PLAY,
        STOP,
        NEXT,
        PREVIOUS
    }

    JukeboxAction action;

    public JukeboxActionPacketToServer(BlockPos pos, JukeboxAction action) {
        super(pos);
        this.action = action;
    }

    public JukeboxActionPacketToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(action.ordinal());
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        action = JukeboxAction.values()[buf.readInt()];
    }

    @Override
    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.setPacketHandled(true);
        ServerPlayer player = ctx.getSender();
        if (player != null) {
            ServerLevel level = player.serverLevel();
            ctx.enqueueWork(() -> {
                var entity = level.getBlockEntity(pos);
                if (!(entity instanceof AbstractTieredJukeboxBE jukebox)) return;
                switch (action) {
                    case PLAY:
                        if (jukebox.isPlaying()) break;
                        if (!jukebox.hasPlayableItem()) break;
                        jukebox.startPlaying();
                        break;
                    case STOP:
                        if (!jukebox.isPlaying()) break;
                        jukebox.stopPlayingAndDoNotSkip();
                        break;
                    case NEXT:
                        jukebox.selectNextTrack();
                        if (!jukebox.hasPlayableItem()) break;
                        jukebox.startPlaying();
                        break;
                    case PREVIOUS:
                        jukebox.selectPreviousTrack();
                        if (!jukebox.hasPlayableItem()) break;
                        jukebox.startPlaying();
                        break;
                    default:
                        break;
                }
            });
        }
    }
}
