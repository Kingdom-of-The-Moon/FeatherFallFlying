package org.moon.fefafly.mixin;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Vanishable;
import org.moon.fefafly.FeFaFlyMod;
import org.moon.fefafly.Utils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(EnchantmentTarget.class)
public class EnchantmentTargetMixin {

    @SuppressWarnings("InvokerTarget")
    @Invoker("<init>")
    private static EnchantmentTarget newEntry() {
        throw new AssertionError();
    }

    //                return Utils.isArmorType(item, EquipmentSlot.FEET) || Utils.isArmorType(item, EquipmentSlot.HEAD);
    //  private final static synthetic [Lnet/minecraft/enchantment/EnchantmentTarget; field_9077

    @SuppressWarnings("ShadowTarget")
    @Shadow
    @Mutable
    private static @Final EnchantmentTarget[] field_9077;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<clinit>", at = @At(value = "FIELD",
            opcode = Opcodes.PUTSTATIC,
            target = "Lnet/minecraft/enchantment/EnchantmentTarget;field_28350:[Lnet/minecraft/enchantment/EnchantmentTarget;",
            shift = At.Shift.AFTER))
    private static void addCustomVariant(CallbackInfo ci) {
        ArrayList<EnchantmentTarget> valueEntries = new ArrayList<>(Arrays.asList(field_9077));

        //EnchantmentTarget armorFeetHead = newEntry() {
        //
        //};



        FeFaFlyMod.ARMOR_FEET_HEAD = newEntry();
    }
}
