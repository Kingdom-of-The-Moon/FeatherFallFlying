package org.moon.fefafly;

import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantments;
import org.moon.enchantmenttweaker.EnchantmentModifications;

public class FeFaFlyMod implements ModInitializer {
	@Override
	public void onInitialize() {
		EnchantmentModifications.add(Enchantments.FEATHER_FALLING, new FeatherFallFlyingEnchantmentModification());
	}

}
