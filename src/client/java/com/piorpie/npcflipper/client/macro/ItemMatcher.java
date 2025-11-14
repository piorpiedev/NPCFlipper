package com.piorpie.npcflipper.client.macro;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ItemMatcher {
    public static boolean matches(ItemStack stack, Item itemType, String itemName, String stringInLore) {
        if (stack == null || stack.isEmpty()) return false;
        if (stack.getItem() != itemType) return false;

        // Check name
        Text name = stack.getName();
        if (name != null && !name.getString().contains(itemName)) {
            return false;
        }

        // Check lore
        boolean containsLore = false;
        var lore = stack.get(DataComponentTypes.LORE);
        if (lore == null) return false;
        for (Text line : lore.lines()) {
            if (line.getString().contains(stringInLore)) {
                containsLore = true;
            }
        }
        return containsLore;
    }
}
