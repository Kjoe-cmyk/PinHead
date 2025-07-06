/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package net.mcreator.nextbotssmart.client;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.mcreator.nextbotssmart.client.JumpscareHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(value=Dist.CLIENT)
public class JumpscareOverlay
implements IGuiOverlay {
    private static final Random RANDOM = new Random();

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int width, int height) {
        if (JumpscareHandler.isJumpscareActive()) {
            ResourceLocation texture = new ResourceLocation(JumpscareHandler.getJumpscareTexture());
            int timer = JumpscareHandler.getJumpscareTimer();
            float shakeIntensity = Math.min(10.0f, 20.0f * ((float)timer / 60.0f));
            int shakeX = timer > 0 ? (int)((RANDOM.nextFloat() - 0.5f) * shakeIntensity * 2.0f) : 0;
            int shakeY = timer > 0 ? (int)((RANDOM.nextFloat() - 0.5f) * shakeIntensity * 2.0f) : 0;
            int imageWidth = width * 3 / 4;
            int imageHeight = height * 3 / 4;
            int x = (width - imageWidth) / 2 + shakeX;
            int y = (height - imageHeight) / 2 + shakeY;
            RenderSystem.disableDepthTest();
            RenderSystem.setShader(GameRenderer::m_172817_);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)texture);
            guiGraphics.m_280163_(texture, x, y, 0.0f, 0.0f, imageWidth, imageHeight, imageWidth, imageHeight);
            RenderSystem.enableDepthTest();
        }
    }
}

