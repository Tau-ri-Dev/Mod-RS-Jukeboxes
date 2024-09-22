package dev.tauri.rsjukeboxes.compatibility;

import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class VinURLCompat {
    public static final Identifier PLAY_PACKET_ID = new Identifier("vinurl", "play_sound");
    public static final Identifier CUSTOM_RECORD = new Identifier("vinurl", "custom_record");

    public static boolean isCustomDisc(@NotNull Item i){
        return Registries.ITEM.getId(i).equals(CUSTOM_RECORD);
    }

    public static void playDisc(AbstractRSJukeboxBE jukeboxBE) {
        var recordStack = jukeboxBE.getPlayingItem();
        var isRecord = (recordStack.getItem() instanceof MusicDiscItem && isCustomDisc(recordStack.getItem()));
        if (jukeboxBE.getWorld() != null && !jukeboxBE.getWorld().isClient() && isRecord) {
            String musicUrl = recordStack.getOrCreateNbt().getString("music_url");

            if (musicUrl != null && !musicUrl.isEmpty()) {
                var bufInfo = PacketByteBufs.create();
                bufInfo.writeBlockPos(jukeboxBE.getPos());
                bufInfo.writeString(musicUrl);

                jukeboxBE.getWorld().getPlayers().forEach(
                        playerEntity -> ServerPlayNetworking.send((ServerPlayerEntity) playerEntity, PLAY_PACKET_ID, bufInfo)
                );
            }
        }
    }

    public static void stopDisc(AbstractRSJukeboxBE jukeboxBE) {
        if (jukeboxBE.getWorld() == null) return;
        var bufInfo = PacketByteBufs.create();
        bufInfo.writeBlockPos(jukeboxBE.getPos());
        bufInfo.writeString("");

        jukeboxBE.getWorld().getPlayers().forEach(
                playerEntity -> ServerPlayNetworking.send((ServerPlayerEntity) playerEntity, PLAY_PACKET_ID, bufInfo)
        );
    }
}
