package dev.tauri.rsjukeboxes.packet.packets;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.packet.PositionedPacket;
import dev.tauri.rsjukeboxes.state.State;
import dev.tauri.rsjukeboxes.state.StateProviderInterface;
import dev.tauri.rsjukeboxes.state.StateTypeEnum;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.apache.commons.lang3.NotImplementedException;

public class StateUpdatePacketToClient extends PositionedPacket {
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

    public StateUpdatePacketToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(stateType.id);
        state.toBytes(buf);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        stateType = StateTypeEnum.byId(buf.readInt());
        stateBuf = buf.copy();
    }

    @Override
    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.setPacketHandled(true);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        ClientLevel level = player.clientLevel;
        ctx.enqueueWork(() -> {
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
