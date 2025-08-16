package com.raymondhooper.sugarcookie;

import net.fabricmc.api.ModInitializer;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.util.Identifier;

/**
 * Entry point for the Sugar Cookie mod. This mod adds a new edible item
 * that grants the player a Jump Boost effect for 60 seconds when consumed.
 *
 * The sugar cookie can be crafted by combining a standard Minecraft cookie
 * with a piece of sugar in a shapeless recipe.
 */
public class SugarCookieMod implements ModInitializer {
    public static final String MOD_ID = "sugarcookie";

    /** Defines the sugar cookie item and its food properties. */
    public static Item SUGAR_COOKIE;

    @Override
    public void onInitialize() {
        // Create the registry key for the item
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "sugar_cookie"));
        
        // Create and register the sugar cookie item
        SUGAR_COOKIE = Registry.register(
            Registries.ITEM,
            itemKey,
            new Item(new Item.Settings()
                .registryKey(itemKey)  // This is required for 1.21.2+
                .food(new FoodComponent.Builder()
                    .nutrition(2)                 // Restores 1 hunger shank (2 points)
                    .saturationModifier(0.4f)     // Match vanilla cookie saturation (0.4)
                    .alwaysEdible()               // Allows eating even when full
                    .build()
                )
            ) {
                @Override
                public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
                    ItemStack result = super.finishUsing(stack, world, user);
                    
                    if (user instanceof PlayerEntity player && !world.isClient) {
                        // Apply Jump Boost III for 60 seconds (1200 ticks)
                        player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.JUMP_BOOST,
                            1200, // 60 seconds in ticks
                            2     // Level III (0-based, so 2 = level III)
                        ));
                    }
                    
                    return result;
                }
            }
        );

        // Place the sugar cookie after the vanilla cookie in the Food & Drink item group.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries ->
                entries.addAfter(Items.COOKIE, SUGAR_COOKIE)
        );
    }
}