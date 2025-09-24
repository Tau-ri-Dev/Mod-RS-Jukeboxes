package dev.tauri.rsjukeboxes.integration.cctweaked;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CCTweakedHelper {

    public static List<Object> getCorrectlyOrderedTableValues(Map<?, ?> table) {
        return table.entrySet().stream().sorted(Comparator.comparingInt(l -> ((int) Double.parseDouble(String.valueOf(l.getKey()))))).map(e -> (Object) e.getValue()).toList();
    }
}
