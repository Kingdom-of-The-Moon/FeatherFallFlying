package org.moon.fefafly.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import org.moon.fefafly.FeFaFlyMod;
import org.moon.fefafly.access.EnchantmentAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProtectionEnchantment.class)
public abstract class ProtectionEnchantmentMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Enchantment.Rarity weight, ProtectionEnchantment.Type protectionType, EquipmentSlot[] slotTypes, CallbackInfo ci) {
        if (protectionType.equals(ProtectionEnchantment.Type.FALL)) {
            ((EnchantmentAccess)this).setTargetType(FeFaFlyMod.ARMOR_FEET_HEAD);
        }
    }

}
