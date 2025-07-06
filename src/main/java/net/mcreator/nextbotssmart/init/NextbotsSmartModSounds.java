/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package net.mcreator.nextbotssmart.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class NextbotsSmartModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.SOUND_EVENTS, (String)"nextbots_smart");
    public static final RegistryObject<SoundEvent> AMBIENTSPOOKY = REGISTRY.register("ambientspooky", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("nextbots_smart", "ambientspooky")));
    public static final RegistryObject<SoundEvent> JUMPSCARE = REGISTRY.register("jumpscare", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("nextbots_smart", "jumpscare")));
    public static final RegistryObject<SoundEvent> CHASING = REGISTRY.register("chasing", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("nextbots_smart", "chasing")));
    public static final RegistryObject<SoundEvent> FOOTSTEPS = REGISTRY.register("footsteps", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("nextbots_smart", "footsteps")));
    public static final RegistryObject<SoundEvent> BREATHING = REGISTRY.register("breathing", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("nextbots_smart", "breathing")));
    public static final RegistryObject<SoundEvent> WATCHING = REGISTRY.register("watching", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("nextbots_smart", "watching")));
    public static final RegistryObject<SoundEvent> VANISH = REGISTRY.register("vanish", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("nextbots_smart", "vanish")));
}

