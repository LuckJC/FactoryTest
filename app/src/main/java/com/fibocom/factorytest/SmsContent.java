package com.fibocom.factorytest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SmsContent {

    public static final List<SmsItem> ITEMS = new ArrayList<>();
    public static final Map<String, SmsItem> ITEM_MAP = new HashMap<>();

    public static void addItem(SmsItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static SmsItem createDummyItem(int position, String phoneNumber, String content, String date) {
        return new SmsItem(String.valueOf(position), phoneNumber, content, date);
    }

    public static class SmsItem {
        public final String id;
        public final String phoneNumber;
        public final String content;
        public final String date;

        public SmsItem(String id, String phoneNumber, String content, String date) {
            this.id = id;
            this.phoneNumber = phoneNumber;
            this.content = content;
            this.date = date;
        }

        @Override
        public String toString() {
            return phoneNumber + "[" + content + "]";
        }
    }
}