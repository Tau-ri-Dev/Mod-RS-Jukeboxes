package dev.tauri.rsjukeboxes.util;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.item.ITabbedItem;
import dev.tauri.rsjukeboxes.item.RSJBlockItem;
import dev.tauri.rsjukeboxes.registry.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static dev.tauri.rsjukeboxes.registry.TabRegistry.REGISTER;

public class TabHelper {
    public static final Map<String, List<Supplier<? extends ItemLike>>> TAB_ITEMS = new HashMap<>();

    private static RegistryObject<CreativeModeTab> lastTab = null;

    @ParametersAreNonnullByDefault
    public static RegistryObject<CreativeModeTab> createCreativeTab(String name, Supplier<RegistryObject<? extends ItemLike>> iconObject) {
        final var lastTabForThisTab = lastTab;
        lastTab = REGISTER.register(name, () -> {
                    var tab = CreativeModeTab.builder()
                            .icon(() -> {
                                if (iconObject.get() == null) {
                                    RSJukeboxes.logger.error("Tab " + name + " has null icon!");
                                    return new ItemStack(Blocks.AIR);
                                }
                                return new ItemStack(iconObject.get().get());
                            })
                            .title(Component.translatable("itemGroup." + RSJukeboxes.MOD_ID + "_" + name))
                            .displayItems((parameters, output) -> {
                                var list = TAB_ITEMS.get(name);
                                if (list == null) {
                                    list = new ArrayList<>();
                                    for (var itemObject : ItemRegistry.REGISTER.getEntries().stream().toList()) {
                                        var item = itemObject.get();
                                        if (item instanceof ITabbedItem tabbedItem) {
                                            if (tabbedItem.getTab() != null) {
                                                if (tabbedItem.getTab().getId().getPath().equalsIgnoreCase(name)) {
                                                    list.add(itemObject);
                                                }
                                            }
                                        }
                                    }
                                    TAB_ITEMS.put(name, list);
                                }
                                for (var itemObject : list) {
                                    var item = itemObject.get();
                                    output.accept(new ItemStack(item));
                                    if(item instanceof RSJBlockItem itemBlock){
                                        itemBlock.addAdditional(output);
                                    }
                                }
                            });
                    if (lastTabForThisTab != null)
                        tab.withTabsBefore(lastTabForThisTab.getId());
                    return tab.build();
                }
        );
        return lastTab;
    }
}
