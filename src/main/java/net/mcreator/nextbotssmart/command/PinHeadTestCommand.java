package net.mcreator.nextbotssmart.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.mcreator.nextbotssmart.entity.PinHeadEntity;
import net.mcreator.nextbotssmart.NextbotsSmartMod;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="nextbots_smart")
public class PinHeadTestCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pinhead_test")
            .requires(source -> source.hasPermission(2)) // Requires OP level 2
            .then(Commands.literal("enable")
                .executes(context -> enableTestMode(context)))
            .then(Commands.literal("disable")
                .executes(context -> disableTestMode(context)))
            .then(Commands.literal("debug")
                .then(Commands.literal("on")
                    .executes(context -> enableDebugMode(context)))
                .then(Commands.literal("off")
                    .executes(context -> disableDebugMode(context))))
            .then(Commands.literal("spawn")
                .executes(context -> spawnPinHead(context)))
            .then(Commands.literal("force")
                .then(Commands.argument("behavior", StringArgumentType.string())
                    .executes(context -> forceBehavior(context))))
            .then(Commands.literal("trigger")
                .then(Commands.literal("psychological")
                    .executes(context -> triggerPsychological(context)))
                .then(Commands.literal("environmental")
                    .executes(context -> triggerEnvironmental(context)))
                .then(Commands.literal("paranoid")
                    .executes(context -> triggerParanoid(context))))
            .then(Commands.literal("reset")
                .executes(context -> resetTimers(context)))
            .then(Commands.literal("help")
                .executes(context -> showHelp(context))));
    }
    
    private static int enableTestMode(CommandContext<CommandSourceStack> context) {
        PinHeadEntity.setTestMode(true);
        context.getSource().sendSuccess(() -> Component.literal("Pin Head Test Mode ENABLED"), true);
        return 1;
    }
    
    private static int disableTestMode(CommandContext<CommandSourceStack> context) {
        PinHeadEntity.setTestMode(false);
        context.getSource().sendSuccess(() -> Component.literal("Pin Head Test Mode DISABLED"), true);
        return 1;
    }
    
    private static int enableDebugMode(CommandContext<CommandSourceStack> context) {
        PinHeadEntity.setDebugMode(true);
        context.getSource().sendSuccess(() -> Component.literal("Pin Head Debug Mode ENABLED"), true);
        return 1;
    }
    
    private static int disableDebugMode(CommandContext<CommandSourceStack> context) {
        PinHeadEntity.setDebugMode(false);
        context.getSource().sendSuccess(() -> Component.literal("Pin Head Debug Mode DISABLED"), true);
        return 1;
    }
    
    private static int spawnPinHead(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PinHeadEntity.testSpawnNearPlayer(player);
        context.getSource().sendSuccess(() -> Component.literal("Spawned Pin Head near you for testing"), true);
        return 1;
    }
    
    private static int forceBehavior(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String behaviorName = StringArgumentType.getString(context, "behavior").toUpperCase();
        
        try {
            PinHeadEntity.BehaviorState state = PinHeadEntity.BehaviorState.valueOf(behaviorName);
            
            // Find nearest Pin Head entity
            PinHeadEntity nearestPinHead = findNearestPinHead(player);
            if (nearestPinHead != null) {
                nearestPinHead.testForceBehaviorState(state);
                context.getSource().sendSuccess(() -> Component.literal("Forced nearest Pin Head to " + behaviorName + " behavior"), true);
            } else {
                context.getSource().sendFailure(Component.literal("No Pin Head entities found nearby"));
            }
        } catch (IllegalArgumentException e) {
            context.getSource().sendFailure(Component.literal("Invalid behavior state. Valid states: STALKING, CHASING, HIDING, WATCHING, VANISHING, ENVIRONMENTAL_MANIPULATION"));
        }
        
        return 1;
    }
    
    private static int triggerPsychological(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PinHeadEntity nearestPinHead = findNearestPinHead(player);
        
        if (nearestPinHead != null) {
            nearestPinHead.testPsychologicalEffects(player);
            context.getSource().sendSuccess(() -> Component.literal("Triggered psychological effects"), true);
        } else {
            context.getSource().sendFailure(Component.literal("No Pin Head entities found nearby"));
        }
        
        return 1;
    }
    
    private static int triggerEnvironmental(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PinHeadEntity nearestPinHead = findNearestPinHead(player);
        
        if (nearestPinHead != null) {
            nearestPinHead.testEnvironmentalManipulation(player);
            context.getSource().sendSuccess(() -> Component.literal("Triggered environmental manipulation"), true);
        } else {
            context.getSource().sendFailure(Component.literal("No Pin Head entities found nearby"));
        }
        
        return 1;
    }
    
    private static int triggerParanoid(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PinHeadEntity nearestPinHead = findNearestPinHead(player);
        
        if (nearestPinHead != null) {
            nearestPinHead.testParanoidEffects(player);
            context.getSource().sendSuccess(() -> Component.literal("Applied paranoid effects"), true);
        } else {
            context.getSource().sendFailure(Component.literal("No Pin Head entities found nearby"));
        }
        
        return 1;
    }
    
    private static int resetTimers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PinHeadEntity nearestPinHead = findNearestPinHead(player);
        
        if (nearestPinHead != null) {
            nearestPinHead.testResetTimers();
            context.getSource().sendSuccess(() -> Component.literal("Reset all timers and cooldowns"), true);
        } else {
            context.getSource().sendFailure(Component.literal("No Pin Head entities found nearby"));
        }
        
        return 1;
    }
    
    private static int showHelp(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("=== PIN HEAD TEST COMMANDS ==="), false);
        context.getSource().sendSuccess(() -> Component.literal("/pinhead_test enable - Enable test mode"), false);
        context.getSource().sendSuccess(() -> Component.literal("/pinhead_test disable - Disable test mode"), false);
        context.getSource().sendSuccess(() -> Component.literal("/pinhead_test debug on/off - Toggle debug logging"), false);
        context.getSource().sendSuccess(() -> Component.literal("/pinhead_test spawn - Spawn Pin Head near you"), false);
        context.getSource().sendSuccess(() -> Component.literal("/pinhead_test force <behavior> - Force behavior state"), false);
        context.getSource().sendSuccess(() -> Component.literal("  Valid behaviors: STALKING, CHASING, HIDING, WATCHING, VANISHING, ENVIRONMENTAL_MANIPULATION"), false);
        context.getSource().sendSuccess(() -> Component.literal("/pinhead_test trigger psychological - Trigger psychological effects"), false);
        context.getSource().sendSuccess(() -> Component.literal("/pinhead_test trigger environmental - Trigger environmental manipulation"), false);
        context.getSource().sendSuccess(() -> Component.literal("/pinhead_test trigger paranoid - Apply paranoid effects"), false);
        context.getSource().sendSuccess(() -> Component.literal("/pinhead_test reset - Reset all timers and cooldowns"), false);
        return 1;
    }
    
    private static PinHeadEntity findNearestPinHead(ServerPlayer player) {
        PinHeadEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (Entity entity : player.serverLevel().getAllEntities()) {
            if (entity instanceof PinHeadEntity) {
                double distance = player.distanceTo(entity);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = (PinHeadEntity) entity;
                }
            }
        }
        
        return nearest;
    }
}
