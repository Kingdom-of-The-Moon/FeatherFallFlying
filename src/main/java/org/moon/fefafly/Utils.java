package org.moon.fefafly;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Utils {

    public static boolean isArmorType(ItemStack itemStack, EquipmentSlot slot) {
        return isArmorType(itemStack.getItem(), slot);
    }

    public static boolean isArmorType(Item item, EquipmentSlot slot) {
        return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == slot;
    }

}
