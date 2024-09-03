package dev.tauri.rsjukeboxes.packet.packets;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.packet.PositionedPacket;
import dev.tauri.rsjukeboxes.packet.RSJPacketHandler;
import dev.tauri.rsjukeboxes.state.State;
import dev.tauri.rsjukeboxes.state.StateProviderInterface;
import dev.tauri.rsjukeboxes.state.StateTypeEnum;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.NotImplementedException;

public class StateUpdateRequestToServer extends PositionedPacket {
    public StateUpdateRequestToServer() {
        super();
    }

    @Override
    public Identifier getId() {
        return new Identifier(RSJukeboxes.MOD_ID, "state_update_request_to_server");
    }

    StateTypeEnum stateType;

    public StateUpdateRequestToServer(BlockPos pos, StateTypeEnum stateType) {
        super(pos);
        this.stateType = stateType;
    }

    public StateUpdateRequestToServer(PacketByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(PacketByteBuf buf) {
        super.toBytes(buf);

        buf.writeInt(stateType.id);
    }

    @Override
    public void fromBytes(PacketByteBuf buf) {
        super.fromBytes(buf);
        stateType = StateTypeEnum.byId(buf.readInt());
    }

    @Override
    public void handle(PlayerEntity player, PacketSender responseSender) {
        if (!(player instanceof ServerPlayerEntity sp)) return;
        var level = sp.getServerWorld();
        level.getServer().execute(() -> {
            StateProviderInterface te = (StateProviderInterface) level.getBlockEntity(pos);

            if (te != null) {
                try {
                    State state = te.getState(stateType);

                    if (state != null)
                        RSJPacketHandler.sendTo(new StateUpdatePacketToClient(pos, stateType, state), sp);
                    else
                        throw new NotImplementedException("State not implemented on " + te.getClass().getCanonicalName() + " : " + stateType.toString());
                } catch (Exception e) {
                    RSJukeboxes.logger.error("Error while handling packet!", e);
                }
            }
        });
    }
}
