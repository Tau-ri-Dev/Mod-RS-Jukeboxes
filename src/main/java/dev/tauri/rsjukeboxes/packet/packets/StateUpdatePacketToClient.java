package dev.tauri.rsjukeboxes.packet.packets;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.packet.PositionedPacket;
import dev.tauri.rsjukeboxes.state.State;
import dev.tauri.rsjukeboxes.state.StateProviderInterface;
import dev.tauri.rsjukeboxes.state.StateTypeEnum;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.NotImplementedException;

public class StateUpdatePacketToClient extends PositionedPacket {

    public StateUpdatePacketToClient() {
        super();
    }

    private StateTypeEnum stateType;
    private State state;

    private ByteBuf stateBuf;

    public StateUpdatePacketToClient(BlockPos pos, StateTypeEnum stateType, State state) {
        super(pos);

        this.stateType = stateType;
        if (state == null) {
            throw new NullPointerException("State was null! (State type: " + stateType.toString() + "; Pos: " + pos.toString() + ")");
        }

        this.state = state;
    }

    public StateUpdatePacketToClient(PacketByteBuf buf) {
        super(buf);
    }

    @Override
    public Identifier getId() {
        return new Identifier(RSJukeboxes.MOD_ID, "state_update_packet_to_client");
    }

    @Override
    public void toBytes(PacketByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(stateType.id);
        state.toBytes(buf);
    }

    @Override
    public void fromBytes(PacketByteBuf buf) {
        super.fromBytes(buf);
        stateType = StateTypeEnum.byId(buf.readInt());
        stateBuf = buf.copy();
    }

    @Override
    public void handle(PlayerEntity player, PacketSender responseSender) {
        if (!(player instanceof ClientPlayerEntity client)) return;
        var level = client.getWorld();
        if (level == null) return;
        MinecraftClient.getInstance().execute(() -> {
            StateProviderInterface te = (StateProviderInterface) level.getBlockEntity(pos);
            try {
                if (te == null)
                    return;

                State state = te.createState(stateType);

                if (state != null) {
                    state.fromBytes(stateBuf);

                    te.setState(stateType, state);
                } else {
                    throw new NotImplementedException("State not implemented on " + te.getClass().getCanonicalName());
                }
            } catch (Exception e) {
                RSJukeboxes.logger.error("Error while handling packet!", e);
            }
        });
    }
}
