package com.sm1les.meowmur;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class MeowmurMod implements ModInitializer {
	public static final String MOD_ID = "meowmur";
	public static Item MEOWMUR;

	@Override
	public void onInitialize() {
		Identifier id = Identifier.of(MOD_ID, "meowmur");
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
		MEOWMUR = Registry.register(
				Registries.ITEM,
				key,
				new MeowmurItem(new Item.Settings().registryKey(key).maxCount(1))
		);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> entries.add(MEOWMUR));
	}
}
