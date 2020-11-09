package com.fibocom.factorytest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoContent {
    public static final List<InfoContent.InfoItem> ITEMS = new ArrayList<>();
    public static final Map<String, InfoContent.InfoItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        addItem(createMainItem(1, "IMEI", "123456"));
    }

    private static void addItem(InfoContent.InfoItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static InfoContent.InfoItem createMainItem(int position, String content, String details) {
        return new InfoContent.InfoItem(String.valueOf(position), content, details);
    }

    public static class InfoItem {
        public final String id;
        public final String content;
        public final String details;

        public InfoItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
