package dev.tauri.rsjukeboxes.packet.packets;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.blockentity.AbstractTieredJukeboxBE;
import dev.tauri.rsjukeboxes.packet.PositionedPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class JukeboxActionPacketToServer extends PositionedPacket {

    public JukeboxActionPacketToServer() {
        super();
    }

    @Override
    public Identifier getId() {
        return new Identifier(RSJukeboxes.MOD_ID, "jukebox_action_packet_to_server");
    }

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

    public JukeboxActionPacketToServer(PacketByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(PacketByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(action.ordinal());
    }

    @Override
    public void fromBytes(PacketByteBuf buf) {
        super.fromBytes(buf);
        action = JukeboxAction.values()[buf.readInt()];
    }

    @Override
    public void handle(PlayerEntity player, PacketSender responseSender) {
        if (!(player instanceof ServerPlayerEntity sp)) return;
        var level = sp.getServerWorld();
        if (level == null) return;
        level.getServer().execute(() -> {
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
