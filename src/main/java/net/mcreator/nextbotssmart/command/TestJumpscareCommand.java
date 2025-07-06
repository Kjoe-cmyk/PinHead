/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.network.PacketDistributor
 */
package net.mcreator.nextbotssmart.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.mcreator.nextbotssmart.NextbotsSmartMod;
import net.mcreator.nextbotssmart.network.JumpscareMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid="nextbots_smart")
public class TestJumpscareCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher dispatcher = event.getDispatcher();
        LiteralArgumentBuilder command = (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"testjumpscare").requires(source -> source.m_6761_(2))).executes(context -> {
            ServerPlayer player = ((CommandSourceStack)context.getSource()).m_81375_();
            NextbotsSmartMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), (Object)new JumpscareMessage());
            ((CommandSourceStack)context.getSource()).m_288197_(() -> Component.m_237113_((String)"Jumpscare test triggered!"), false);
            return 1;
        });
        dispatcher.register(command);
    }
}

