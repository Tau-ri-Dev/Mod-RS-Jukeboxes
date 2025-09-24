package dev.tauri.rsjukeboxes.integration;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.integration.cctweaked.CCIntegrationWrapper;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public enum Integrations {
    CCT("ComputerCraft", List.of("computercraft"));

    public final String name;
    public final List<String> modNames;
    private Boolean isLoaded = null;
    private final List<Loader> onLoad = new ArrayList<>();
    private final List<Loader> onNotLoaded = new ArrayList<>();

    Integrations(String name, List<String> modNames) {
        this.name = name;
        this.modNames = modNames;
    }

    public boolean isLoaded() {
        return isLoaded != null && isLoaded;
    }

    public Integrations addOnLoad(Loader task) {
        if (isLoaded()) {
            try {
                task.run();
            } catch (Exception ignored) {
            }
            return this;
        }
        onLoad.add(task);
        return this;
    }

    public Integrations addOnNotLoaded(Loader task) {
        if (isLoaded != null && !isLoaded) {
            try {
                task.run();
            } catch (Exception ignored) {
            }
            return this;
        }
        onNotLoaded.add(task);
        return this;
    }

    public interface Loader {
        void run() throws Exception;
    }

    static {
        // CC
        CCT.addOnLoad(() -> RSJukeboxes.ccWrapper = (CCIntegrationWrapper) Class.forName(RSJukeboxes.CC_WRAPPER_LOADED).getConstructor().newInstance())
                .addOnNotLoaded(() -> RSJukeboxes.ccWrapper = (CCIntegrationWrapper) Class.forName(RSJukeboxes.CC_WRAPPER_NOT_LOADED).getConstructor().newInstance());
    }

    public static void tryLoad() {
        for (var i : values()) {
            try {
                if (i.modNames.stream().anyMatch((name) -> ModList.get().isLoaded(name))) {
                    RSJukeboxes.logger.info("{} found and connection is enabled... Connecting...", i.name);
                    i.isLoaded = true;
                    for (var t : i.onLoad)
                        t.run();
                    RSJukeboxes.logger.info("Successfully connected into {}!", i.name);
                } else {
                    i.isLoaded = false;
                    for (var t : i.onNotLoaded)
                        t.run();
                }
            } catch (Exception e) {
                RSJukeboxes.logger.error("Exception loading {} wrapper", i.name, e);
            }
        }
    }
}
