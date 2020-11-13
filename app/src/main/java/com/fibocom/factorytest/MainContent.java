package com.fibocom.factorytest;

import java.util.ArrayList;
import java.util.List;

public class MainContent {
    public static final List<MainContent.MainItem> ITEMS = new ArrayList<>();

    public static void addMainItem(MainContent.MainItem item) {
        ITEMS.add(item);
    }

    public static MainContent.MainItem createMainItem(String content) {
        return createMainItem(content, "");
    }

    public static MainContent.MainItem createMainItem(String content, String details) {
        return new MainContent.MainItem(content, details);
    }

    public static class MainItem {
        public String content;
        public String details;

        public MainItem(String content, String details) {
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
