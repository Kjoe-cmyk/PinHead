/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package net.mcreator.nextbotssmart.client;

import net.mcreator.nextbotssmart.NextbotsSmartMod;
import net.mcreator.nextbotssmart.entity.PinHeadEntitySimplified;
import net.mcreator.nextbotssmart.init.NextbotsSmartModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(value=Dist.CLIENT)
@Mod.EventBusSubscriber(modid="nextbots_smart", value={Dist.CLIENT})
public class JumpscareHandler {
    private static final int JUMPSCARE_DURATION = 60;
    private static boolean isJumpscareActive = false;
    private static int jumpscareTimer = 0;
    private static String jumpscareTexture = "";

    public static void activateJumpscare(String texturePath) {
        isJumpscareActive = true;
        jumpscareTimer = 60;
        jumpscareTexture = texturePath;
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(NextbotsSmartModSounds.JUMPSCARE.get().getLocation(), SoundSource.HOSTILE, 2.0f, 1.0f, RandomSource.create(), false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true));
            NextbotsSmartMod.LOGGER.info("Playing jumpscare sound on client");
        }
    }

    public static boolean isJumpscareActive() {
        return isJumpscareActive;
    }

    public static String getJumpscareTexture() {
        return jumpscareTexture;
    }

    public static int getJumpscareTimer() {
        return jumpscareTimer;
    }

    public static void updateJumpscare() {
        if (isJumpscareActive && --jumpscareTimer <= 0) {
            isJumpscareActive = false;
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        Player player;
        if (Minecraft.getInstance().level == null) {
            return;
        }
        if (event.getEntity() instanceof Player && event.getEntity().level().isClientSide && (player = (Player)event.getEntity()) == Minecraft.getInstance().player) {
            Entity killer = event.getSource().getEntity();
            NextbotsSmartMod.LOGGER.info("Player died. Killer entity: " + (killer != null ? killer.getClass().getName() : "null"));
            if (killer instanceof PinHeadEntitySimplified) {
                JumpscareHandler.activateJumpscare("nextbots_smart:textures/entities/pinhead.png");
                NextbotsSmartMod.LOGGER.info("Activating Pin Head jumpscare!");
            }
        }
    }

    public static void clientTick() {
        JumpscareHandler.updateJumpscare();
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null || Minecraft.getInstance().level.getGameTime() % 100L == 0L) {
            // empty if block
        }
    }
}

