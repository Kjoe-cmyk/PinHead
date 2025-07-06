/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.fml.util.thread.SidedThreadGroups
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.simple.SimpleChannel
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.mcreator.nextbotssmart;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mcreator.nextbotssmart.init.NextbotsSmartModEntities;
import net.mcreator.nextbotssmart.init.NextbotsSmartModItems;
import net.mcreator.nextbotssmart.init.NextbotsSmartModSounds;
import net.mcreator.nextbotssmart.init.NextbotsSmartModTabs;
import net.mcreator.nextbotssmart.network.JumpscareMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value="nextbots_smart")
public class NextbotsSmartMod {
    public static final Logger LOGGER = LogManager.getLogger(NextbotsSmartMod.class);
    public static final String MODID = "nextbots_smart";
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("nextbots_smart", "nextbots_smart"), () -> "1", "1"::equals, "1"::equals);
    private static int messageID = 0;
    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<AbstractMap.SimpleEntry<Runnable, Integer>>();

    public NextbotsSmartMod() {
        MinecraftForge.EVENT_BUS.register((Object)this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        NextbotsSmartModSounds.REGISTRY.register(bus);
        NextbotsSmartModItems.REGISTRY.register(bus);
        NextbotsSmartModEntities.REGISTRY.register(bus);
        NextbotsSmartModTabs.REGISTRY.register(bus);
        NextbotsSmartMod.addNetworkMessage(JumpscareMessage.class, JumpscareMessage::toBytes, JumpscareMessage::new, JumpscareMessage::handler);
        LOGGER.info("Initializing NextbotsSmartMod - sounds and behaviors");
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        ++messageID;
    }

    public static void queueServerWork(int tick, Runnable action) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
            workQueue.add(new AbstractMap.SimpleEntry<Runnable, Integer>(action, tick));
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ArrayList actions = new ArrayList();
            workQueue.forEach(work -> {
                work.setValue((Integer)work.getValue() - 1);
                if ((Integer)work.getValue() == 0) {
                    actions.add(work);
                }
            });
            actions.forEach(e -> ((Runnable)e.getKey()).run());
            workQueue.removeAll(actions);
        }
    }
}

