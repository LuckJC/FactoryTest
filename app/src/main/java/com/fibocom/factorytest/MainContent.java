package com.fibocom.factorytest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainContent {
    public static final List<MainContent.MainItem> ITEMS = new ArrayList<>();
    public static final List<MainContent.MainItem> INFO_ITEMS = new ArrayList<>();
    public static final Map<String, MainContent.MainItem> ITEM_MAP = new HashMap<>();
    public static final Map<String, MainContent.MainItem> INFO_ITEMS_MAP = new HashMap<>();

    static {
        addMainItem(createMainItem(1, "状态信息"));
        addMainItem(createMainItem(2, "移动网络"));
        addMainItem(createMainItem(3, "短信"));
        addMainItem(createMainItem(4, "GPS"));

        //addInfoItem(createInfoItem(1, "IMEI", "12345678"));
    }

    public static void addMainItem(MainContent.MainItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void addInfoItem(MainContent.MainItem item) {
        INFO_ITEMS.add(item);
        INFO_ITEMS_MAP.put(item.id, item);
    }

    public static MainContent.MainItem createMainItem(int position, String content) {
        return new MainContent.MainItem(String.valueOf(position), content, makeDetails(position));
    }

    public static MainContent.MainItem createInfoItem(int position, String content, String details) {
        return new MainContent.MainItem(String.valueOf(position), content, details);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public static class MainItem {
        public final String id;
        public final String content;
        public final String details;

        public MainItem(String id, String content, String details) {
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
