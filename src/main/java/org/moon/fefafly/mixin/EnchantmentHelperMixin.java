package org.moon.fefafly.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;
import org.moon.fefafly.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Unique private static Enchantment isAcceptableItemFix$enchantment;

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private static <E>E stealLocalVariable(Iterator<Enchantment> iterator) {
        Enchantment next = iterator.next();
        EnchantmentHelperMixin.isAcceptableItemFix$enchantment = next;
        return (E) next;
    }

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean isAcceptableItemFix(EnchantmentTarget enchantmentTarget, Item item) {
        boolean isFeatherHelmet = false;
        if (Utils.isArmorType(item, EquipmentSlot.HEAD)) {

        }

        return isFeatherHelmet || isAcceptableItemFix$enchantment.isAcceptableItem(item.getDefaultStack());
    }


    @Unique private static Iterable<ItemStack> getProtectionAmount$equipment;
    @Unique private static DamageSource getProtectionAmount$source;

    @Inject(method = "getProtectionAmount", at = @At("HEAD"))
    private static void getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        EnchantmentHelperMixin.getProtectionAmount$equipment = equipment;
        EnchantmentHelperMixin.getProtectionAmount$source = source;
    }

    @Redirect(method = "getProtectionAmount",
            at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/mutable/MutableInt;intValue()I"))
    private static int redirectSomeMethod(MutableInt mutableInt) {
        return mutableInt.intValue() + modifyProtectionAmount();
    }

    private static int modifyProtectionAmount() {

        MutableInt modifiedAmount = new MutableInt();

        // If the damage taken was from flying into a wall...
        if (getProtectionAmount$source.equals(DamageSource.FLY_INTO_WALL) || getProtectionAmount$source.equals(DamageSource.FALL)) {
            getProtectionAmount$equipment.forEach((itemStack -> {
                if (!itemStack.isEmpty()) {
                    // If the item is an enchanted helmet
                    EnchantmentHelper.get(itemStack).forEach((enchantment, level) -> {
                        if (enchantment instanceof ProtectionEnchantment protectionEnchantment) {
                            // If the enchantment is feather falling
                            if (protectionEnchantment.protectionType.equals(ProtectionEnchantment.Type.FALL) && Utils.isArmorType(itemStack, EquipmentSlot.HEAD)) {
                                if (getProtectionAmount$source.equals(DamageSource.FLY_INTO_WALL)) {
                                    modifiedAmount.add(level * 3);
                                } else if (getProtectionAmount$source.equals(DamageSource.FALL)) {
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
