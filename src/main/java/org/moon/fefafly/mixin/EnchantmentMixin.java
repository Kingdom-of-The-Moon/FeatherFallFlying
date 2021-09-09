package org.moon.fefafly.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.moon.fefafly.Utils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Shadow @Mutable @Final public EnchantmentTarget type;

    // Force feather falling to work on helmets.
    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    public void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.type.equals(EnchantmentTarget.ARMOR_FEET)) {
            if (Utils.isArmorType(stack, EquipmentSlot.HEAD)) {
                if (((Enchantment)(Object)this) instanceof ProtectionEnchantment protectionEnchantment) {
                    if (protectionEnchantment.protectionType.equals(ProtectionEnchantment.Type.FALL)) {
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
}
