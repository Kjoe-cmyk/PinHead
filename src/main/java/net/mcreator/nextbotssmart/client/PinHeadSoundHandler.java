/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package net.mcreator.nextbotssmart.client;

import net.mcreator.nextbotssmart.NextbotsSmartMod;
import net.mcreator.nextbotssmart.entity.PinHeadEntity;
import net.mcreator.nextbotssmart.init.NextbotsSmartModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(value=Dist.CLIENT)
@Mod.EventBusSubscriber(modid="nextbots_smart", value={Dist.CLIENT})
public class PinHeadSoundHandler {
    private static boolean isAmbientSoundPlaying = false;
    private static boolean isChasingSoundPlaying = false;
    private static boolean isWatchingSoundPlaying = false;
    private static SoundInstance currentAmbientSound = null;
    private static SoundInstance currentChasingSound = null;
    private static SoundInstance currentWatchingSound = null;
    private static PinHeadEntity.BehaviorState lastKnownState = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (Minecraft.m_91087_().f_91073_ != null && Minecraft.m_91087_().f_91074_ != null && Minecraft.m_91087_().f_91073_.m_46467_() % 10L == 0L) {
            SimpleSoundInstance soundInstance;
            LocalPlayer player = Minecraft.m_91087_().f_91074_;
            boolean foundStalkingPinHead = false;
            boolean foundChasingPinHead = false;
            boolean foundWatchingPinHead = false;
            double closestDistance = Double.MAX_VALUE;
            PinHeadEntity.BehaviorState currentState = null;
            for (Entity entity : Minecraft.m_91087_().f_91073_.m_104735_()) {
                if (!(entity instanceof PinHeadEntity)) continue;
                PinHeadEntity pinHead = (PinHeadEntity)entity;
                currentState = pinHead.getBehaviorState();
                double distance = player.m_20270_((Entity)pinHead);
                if (distance < closestDistance) {
                    closestDistance = distance;
                }
                if (currentState == PinHeadEntity.BehaviorState.STALKING || currentState == PinHeadEntity.BehaviorState.HIDING) {
                    foundStalkingPinHead = true;
                } else if (currentState == PinHeadEntity.BehaviorState.CHASING) {
                    foundChasingPinHead = true;
                } else if (currentState == PinHeadEntity.BehaviorState.WATCHING) {
                    foundWatchingPinHead = true;
                }
            }
            if (currentState != null && lastKnownState != currentState) {
                PinHeadSoundHandler.stopAmbientSound();
                PinHeadSoundHandler.stopChasingSound();
                PinHeadSoundHandler.stopWatchingSound();
                NextbotsSmartMod.LOGGER.info("Behavior state changed from " + String.valueOf((Object)lastKnownState) + " to " + String.valueOf(currentState) + ", stopping sounds");
                lastKnownState = currentState;
            }
            if (foundStalkingPinHead && !isAmbientSoundPlaying) {
                float volume = 0.3f;
                volume = closestDistance < 8.0 ? 1.0f : (closestDistance < 16.0 ? 0.8f : (closestDistance < 32.0 ? 0.5f : 0.3f));
                soundInstance = new SimpleSoundInstance(((SoundEvent)NextbotsSmartModSounds.AMBIENTSPOOKY.get()).m_11660_(), SoundSource.HOSTILE, volume, 1.0f, RandomSource.m_216327_(), true, 0, SoundInstance.Attenuation.LINEAR, player.m_20185_(), player.m_20186_(), player.m_20189_(), false);
                Minecraft.m_91087_().m_91106_().m_120367_((SoundInstance)soundInstance);
                currentAmbientSound = soundInstance;
                isAmbientSoundPlaying = true;
                NextbotsSmartMod.LOGGER.info("Started playing stalking sound at distance " + closestDistance + " with volume " + volume);
            } else if (!foundStalkingPinHead && isAmbientSoundPlaying) {
                PinHeadSoundHandler.stopAmbientSound();
                NextbotsSmartMod.LOGGER.info("No stalking Pin Head found, stopping ambient sound");
            }
            if (foundChasingPinHead && !isChasingSoundPlaying) {
                float volume = 1.5f;
                if (closestDistance > 48.0) {
                    volume = 1.0f;
                } else if (closestDistance > 24.0) {
                    volume = 1.2f;
                }
                soundInstance = new SimpleSoundInstance(((SoundEvent)NextbotsSmartModSounds.CHASING.get()).m_11660_(), SoundSource.HOSTILE, volume, 1.0f, RandomSource.m_216327_(), true, 0, SoundInstance.Attenuation.LINEAR, player.m_20185_(), player.m_20186_(), player.m_20189_(), false);
                Minecraft.m_91087_().m_91106_().m_120367_((SoundInstance)soundInstance);
                currentChasingSound = soundInstance;
                isChasingSoundPlaying = true;
                NextbotsSmartMod.LOGGER.info("Started playing chasing sound at distance " + closestDistance + " with volume " + volume);
            } else if (!foundChasingPinHead && isChasingSoundPlaying) {
                PinHeadSoundHandler.stopChasingSound();
                NextbotsSmartMod.LOGGER.info("No chasing Pin Head found, stopping chasing sound");
            }
            if (foundWatchingPinHead && !isWatchingSoundPlaying) {
                float volume = 0.4f;
                volume = closestDistance < 12.0 ? 0.8f : (closestDistance < 24.0 ? 0.6f : 0.4f);
                soundInstance = new SimpleSoundInstance(((SoundEvent)NextbotsSmartModSounds.WATCHING.get()).m_11660_(), SoundSource.HOSTILE, volume, 0.8f, RandomSource.m_216327_(), true, 0, SoundInstance.Attenuation.LINEAR, player.m_20185_(), player.m_20186_(), player.m_20189_(), false);
                Minecraft.m_91087_().m_91106_().m_120367_((SoundInstance)soundInstance);
                currentWatchingSound = soundInstance;
                isWatchingSoundPlaying = true;
                NextbotsSmartMod.LOGGER.info("Started playing watching sound at distance " + closestDistance + " with volume " + volume);
            } else if (!foundWatchingPinHead && isWatchingSoundPlaying) {
                PinHeadSoundHandler.stopWatchingSound();
                NextbotsSmartMod.LOGGER.info("No watching Pin Head found, stopping watching sound");
            }
        }
    }

    private static void stopAmbientSound() {
        if (isAmbientSoundPlaying && currentAmbientSound != null) {
            Minecraft.m_91087_().m_91106_().m_120399_(currentAmbientSound);
            isAmbientSoundPlaying = false;
            currentAmbientSound = null;
            NextbotsSmartMod.LOGGER.info("Stopped ambient sound");
        }
    }

    private static void stopChasingSound() {
        if (isChasingSoundPlaying && currentChasingSound != null) {
            Minecraft.m_91087_().m_91106_().m_120399_(currentChasingSound);
            isChasingSoundPlaying = false;
            currentChasingSound = null;
            NextbotsSmartMod.LOGGER.info("Stopped chasing sound");
        }
    }

    private static void stopWatchingSound() {
        if (isWatchingSoundPlaying && currentWatchingSound != null) {
            Minecraft.m_91087_().m_91106_().m_120399_(currentWatchingSound);
            isWatchingSoundPlaying = false;
            currentWatchingSound = null;
            NextbotsSmartMod.LOGGER.info("Stopped watching sound");
        }
    }
}

