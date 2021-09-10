package org.moon.fefafly;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import org.moon.enchantmenttweaker.lib.ArmorEnchantmentModification;

import java.util.Optional;

public class FeatherFallFlyingEnchantmentModification implements ArmorEnchantmentModification {

    @Override
    public boolean isApplicableTo(ItemStack stack, Optional<Integer> level) {
        return isHelmet(stack);
    }

    @Override
    public int getDamageReduction(ItemStack stack, DamageSource source, int level) {
        if (isHelmet(stack)) {
            if (source.equals(DamageSource.FLY_INTO_WALL)) {
                return level * 3;
            } else if (source.equals(DamageSource.FALL)) {
                return level * -3;
            }
        }
        return 0;
    }

    private static boolean isHelmet(ItemStack stack) {
        return  stack.getItem() instanceof ArmorItem ? ((ArmorItem)stack.getItem()).getSlotType() == EquipmentSlot.HEAD : false;
    }

}
