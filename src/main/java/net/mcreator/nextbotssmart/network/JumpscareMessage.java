/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package net.mcreator.nextbotssmart.network;

import java.util.function.Supplier;
import net.mcreator.nextbotssmart.client.JumpscareHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class JumpscareMessage {
    public JumpscareMessage() {
    }

    public JumpscareMessage(FriendlyByteBuf buffer) {
    }

    public void toBytes(FriendlyByteBuf buffer) {
    }

    public void handler(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> {
            if (Minecraft.m_91087_().f_91074_ != null) {
                JumpscareHandler.activateJumpscare("nextbots_smart:textures/entities/pinhead.png");
            }
        }));
        context.setPacketHandled(true);
    }
}

