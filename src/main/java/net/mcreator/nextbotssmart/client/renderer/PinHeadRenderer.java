/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.resources.ResourceLocation
 */
package net.mcreator.nextbotssmart.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.nextbotssmart.client.model.ModelEntityNextBot;
import net.mcreator.nextbotssmart.entity.PinHeadEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PinHeadRenderer
extends MobRenderer<PinHeadEntity, ModelEntityNextBot<PinHeadEntity>> {
    public PinHeadRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelEntityNextBot(context.m_174023_(ModelEntityNextBot.LAYER_LOCATION)), 0.5f);
    }

    public ResourceLocation getTextureLocation(PinHeadEntity entity) {
        return new ResourceLocation("nextbots_smart:textures/entities/pinhead.png");
    }

    protected void scale(PinHeadEntity entity, PoseStack poseStack, float partialTicks) {
        poseStack.m_85841_(1.2f, 1.2f, 1.2f);
    }
}

