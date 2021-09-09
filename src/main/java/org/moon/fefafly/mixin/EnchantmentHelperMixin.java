package org.moon.fefafly.mixin;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.*;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Users beware: This mod isn't just a simple mixin to make feather falling work on helmets, oh no.
// Minecraft's enchantment system is so BUSTED, so basically I had to make it really hacky to work.
// *Hopefully* shouldn't cause many mod compatibility issues...
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Unique private static int isAcceptableItemFix$power;
    @Unique private static ArrayList isAcceptableItemFix$list;
    @Unique private static Enchantment isAcceptableItemFix$enchantment;
    @Unique private static ItemStack isAcceptableItemFix$itemStack;

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;"))
    private static <E> ArrayList<E> stealLocalVariable() {
        isAcceptableItemFix$list = Lists.newArrayList();
        return isAcceptableItemFix$list;
    }

    // Steals the enchantment iterators return value
    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private static <E>E stealLocalVariable(Iterator<Enchantment> iterator) {
        Enchantment next = iterator.next();
        isAcceptableItemFix$enchantment = next;
        return (E) next;
    }

    // Steals the ItemStack and power arguments
    @Inject(method = "getPossibleEntries", at = @At(value = "HEAD"))
    private static void getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        isAcceptableItemFix$itemStack = stack;
        isAcceptableItemFix$power = power;
    }

    // "Hacks" into isAcceptableItem, and makes it run *per enchantment* instead of *per enchantment TYPE*. Also injects my condition into it~
    // @TODO Turn this into it's own api for other mods
    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean isAcceptableItemFix(EnchantmentTarget enchantmentTarget, Item item) {

        if (Utils.isArmorType(item, EquipmentSlot.HEAD)) {
            if (isAcceptableItemFix$enchantment instanceof ProtectionEnchantment protectionEnchantment) {
                if (protectionEnchantment.protectionType == ProtectionEnchantment.Type.FALL) {
                    for(int i = isAcceptableItemFix$enchantment.getMaxLevel(); i > isAcceptableItemFix$enchantment.getMinLevel() - 1; --i) {
                        if (isAcceptableItemFix$power >= isAcceptableItemFix$enchantment.getMinPower(i) && isAcceptableItemFix$power <= isAcceptableItemFix$enchantment.getMaxPower(i)) {
                            System.out.println("Rolled!");
                            isAcceptableItemFix$list.add(new EnchantmentLevelEntry(isAcceptableItemFix$enchantment, i));
                            return true;
                        }
                    }
                }
            }
        }

        return isAcceptableItemFix$enchantment.isAcceptableItem(isAcceptableItemFix$itemStack);
    }


    @Unique private static Iterable<ItemStack> getProtectionAmount$equipment;
    @Unique private static DamageSource getProtectionAmount$source;

    @Inject(method = "getProtectionAmount", at = @At("HEAD"))
    private static void getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        getProtectionAmount$equipment = equipment;
        getProtectionAmount$source = source;
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
