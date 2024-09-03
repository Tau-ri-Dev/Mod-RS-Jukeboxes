package dev.tauri.rsjukeboxes.util;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.item.ITabbedItem;
import dev.tauri.rsjukeboxes.item.RSJBlockItem;
import dev.tauri.rsjukeboxes.registry.ItemRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class TabHelper {
    public static final Map<String, List<Supplier<ItemStack>>> TAB_ITEMS = new HashMap<>();
    private static final Map<String, ItemGroup> TABS = new HashMap<>();

    public static Supplier<ItemGroup> createCreativeTab(String name, Supplier<ItemStack> iconObject) {
        ItemGroup tab = Registry.register(Registries.ITEM_GROUP, new Identifier(RSJukeboxes.MOD_ID, name),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup." + RSJukeboxes.MOD_ID + "_" + name))
                        .icon(iconObject)
                        .entries(((displayContext, entries) -> {
                            var list = TAB_ITEMS.get(name);
                            if (list == null) {
                                list = new ArrayList<>();
                                for (var item : ItemRegistry.REGISTERED_ITEMS) {
                                    if (item.getItem() instanceof ITabbedItem tabbedItem) {
                                        if (tabbedItem.getTab() != null) {
                                            if (tabbedItem.getTab().get().equals(TABS.get(name))) {
                                                list.add(() -> item);
                                            }
                                        }
                                    }
                                }
                                TAB_ITEMS.put(name, list);
                            }
                            for (var itemObject : list) {
                                var item = itemObject.get();
                                entries.add(item);
                                if (item.getItem() instanceof RSJBlockItem itemBlock) {
                                    itemBlock.addAdditional(entries);
                                }
                            }
                        }))
                        .build());
        TABS.put(name, tab);
        return () -> tab;
    }
}
