package dev.tauri.rsjukeboxes.registry;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {

    public static final List<ItemStack> REGISTERED_ITEMS = new ArrayList<>();

    @SuppressWarnings("all")
    public static Item registerItem(String name, Item item) {
        var r = Registry.register(Registries.ITEM, new Identifier(RSJukeboxes.MOD_ID, name), item);
        REGISTERED_ITEMS.add(new ItemStack(r));
        return r;
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SEARCH).register((entries -> {
            entries.addAll(REGISTERED_ITEMS);
        }));
    }
}
