package org.moon.fefafly.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableInt;
import org.moon.fefafly.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.enchantment.EnchantmentHelper.getIdFromNbt;
import static net.minecraft.enchantment.EnchantmentHelper.getLevelFromNbt;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Unique private static Iterable<ItemStack> equipment;
    @Unique private static DamageSource source;

    @Inject(method = "getProtectionAmount", at = @At("HEAD"))
    private static void getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        EnchantmentHelperMixin.equipment = equipment;
        EnchantmentHelperMixin.source = source;
    }

    @Redirect(method = "getProtectionAmount",
            at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/mutable/MutableInt;intValue()I"))
    private static int redirectSomeMethod(MutableInt mutableInt) {
        return mutableInt.intValue() + modifyProtectionAmount();
    }

    private static int modifyProtectionAmount() {

        MutableInt modifiedAmount = new MutableInt();

        // If the damage taken was from flying into a wall...
        if (source.equals(DamageSource.FLY_INTO_WALL) || source.equals(DamageSource.FALL)) {
            equipment.forEach((itemStack -> {
                if (!itemStack.isEmpty()) {
                    // If the item is an enchanted helmet
                    EnchantmentHelper.get(itemStack).forEach((enchantment, level) -> {
                        if (enchantment instanceof ProtectionEnchantment protectionEnchantment) {
                            // If the enchantment is feather falling
                            if (protectionEnchantment.protectionType.equals(ProtectionEnchantment.Type.FALL) && Utils.isArmorType(itemStack, EquipmentSlot.HEAD)) {
                                if (source.equals(DamageSource.FLY_INTO_WALL)) {
                                    modifiedAmount.add(level * 3);
                                } else if (source.equals(DamageSource.FALL)) {
                                    modifiedAmount.subtract(level * 3);
                                }
                            }
                        }
                    });
                }
            }));
        }
        return modifiedAmount.intValue();
    }

}
