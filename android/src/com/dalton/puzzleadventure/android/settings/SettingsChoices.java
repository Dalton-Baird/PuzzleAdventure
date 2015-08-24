package com.dalton.puzzleadventure.android.settings;

import com.dalton.puzzleadventure.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
public class SettingsChoices
{

    /**
     * An array of items.
     */
    public static List<SettingItem> ITEMS = new ArrayList<SettingItem>();

    /**
     * A map of items, by ID.
     */
    public static Map<String, SettingItem> ITEM_MAP = new HashMap<String, SettingItem>();

    static
    {
        // Add 3 sample items.
        addItem(new SettingItem("1", "Game Settings"));
        addItem(new SettingItem("2", "Audio Settings"));
        addItem(new SettingItem("3", "Dev Settings"));
    }

    private static void addItem(SettingItem item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * An item representing a piece of content.
     */
    public static class SettingItem
    {
        public String id;
        public String content;

        public SettingItem(String id, String content)
        {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString()
        {
            return content;
        }
    }
}
