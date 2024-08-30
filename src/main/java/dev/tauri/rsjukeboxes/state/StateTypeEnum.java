package dev.tauri.rsjukeboxes.state;

import java.util.HashMap;
import java.util.Map;

public enum StateTypeEnum {
    JUKEBOX_UPDATE(0);

    public final int id;

    StateTypeEnum(int id) {
        this.id = id;
    }

    private static final Map<Integer, StateTypeEnum> ID_MAP = new HashMap<>();

    static {
        for (StateTypeEnum stateType : values())
            ID_MAP.put(stateType.id, stateType);
    }

    public static StateTypeEnum byId(int id) {
        return ID_MAP.get(id);
    }
}
