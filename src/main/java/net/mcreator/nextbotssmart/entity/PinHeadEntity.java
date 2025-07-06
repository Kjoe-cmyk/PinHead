/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.SpawnPlacements
 *  net.minecraft.world.entity.SpawnPlacements$Type
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.MeleeAttackGoal
 *  net.minecraft.world.entity.ai.goal.OpenDoorGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.RandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.ai.navigation.GroundPathNavigation
 *  net.minecraft.world.entity.ai.navigation.PathNavigation
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.pathfinder.BlockPathTypes
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 *  net.minecraftforge.registries.ForgeRegistries
 */
package net.mcreator.nextbotssmart.entity;

import javax.annotation.Nullable;
import net.mcreator.nextbotssmart.NextbotsSmartMod;
import net.mcreator.nextbotssmart.init.NextbotsSmartModEntities;
import net.mcreator.nextbotssmart.init.NextbotsSmartModSounds;
import net.mcreator.nextbotssmart.network.JumpscareMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

public class PinHeadEntity
extends Monster {
    private BehaviorState currentState = BehaviorState.STALKING;
    private int behaviorTimer = 0;
    private BlockPos hideTarget = null;
    private boolean hasBeenSeen = false;
    private int coinFlipCooldown = 0;
    private boolean initialPositionSet = false;

    // Horror-focused fields
    private int watchingTimer = 0;
    private BlockPos watchingPosition = null;
    private int environmentalCooldown = 0;
    private boolean hasManipulatedEnvironment = false;
    private int vanishingTimer = 0;
    private int presenceTimer = 0;
    private boolean isWatchingPlayer = false;
    private int psychologicalCooldown = 0;
    private boolean hasLeftSign = false;
    private int paranoidEffectTimer = 0;

    // Debug and testing fields
    private static boolean DEBUG_MODE = true;
    private static boolean TEST_MODE = false;
    private int debugTimer = 0;

    public BehaviorState getBehaviorState() {
        return this.currentState;
    }

    public PinHeadEntity(PlayMessages.SpawnEntity packet, Level world) {
        this((EntityType<PinHeadEntity>)((EntityType)NextbotsSmartModEntities.PIN_HEAD.get()), world);
    }

    public PinHeadEntity(EntityType<PinHeadEntity> type, Level world) {
        super(type, world);
        this.m_274367_(1.0f);
        this.f_21364_ = 0;
        this.m_21557_(false);
        this.m_21441_(BlockPathTypes.DOOR_OPEN, 0.0f);
        this.m_21441_(BlockPathTypes.DOOR_WOOD_CLOSED, 0.0f);
        this.m_21441_(BlockPathTypes.FENCE, -1.0f);
        PathNavigation pathNavigation = this.m_21573_();
        if (pathNavigation instanceof GroundPathNavigation) {
            GroundPathNavigation navigation = (GroundPathNavigation)pathNavigation;
            navigation.m_26477_(true);
            navigation.m_7008_(true);
            navigation.m_148214_(true);
        }
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }

    protected void m_8099_() {
        super.m_8099_();
        this.f_21345_.m_25352_(1, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.5, true){

            protected double m_6639_(LivingEntity entity) {
                return (double)(this.f_25540_.m_20205_() * this.f_25540_.m_20205_() + entity.m_20205_()) + 1.0;
            }
        });
        this.f_21345_.m_25352_(2, (Goal)new RandomStrollGoal((PathfinderMob)this, 1.2));
        this.f_21345_.m_25352_(1, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 64.0f){

            public boolean m_8036_() {
                this.f_25513_ = PinHeadEntity.this.m_9236_().m_45930_((Entity)PinHeadEntity.this, (double)this.f_25514_);
                return this.f_25513_ != null;
            }
        });
        this.f_21346_.m_25352_(1, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, true));
        this.f_21346_.m_25352_(2, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.f_21345_.m_25352_(3, (Goal)new OpenDoorGoal((Mob)this, true){

            public boolean m_8036_() {
                return PinHeadEntity.this.m_21573_().m_26572_();
            }
        });
        this.f_21345_.m_25352_(5, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21345_.m_25352_(6, (Goal)new FloatGoal((Mob)this));
    }

    public MobType m_6336_() {
        return MobType.f_21640_;
    }

    public SoundEvent m_7975_(DamageSource ds) {
        return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
    }

    public SoundEvent m_5592_() {
        return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
    }

    public boolean m_6779_(LivingEntity target) {
        if (target instanceof Player) {
            Player player = (Player)target;
            return !player.m_5833_();
        }
        return super.m_6779_(target);
    }

    public void m_6710_(@Nullable LivingEntity target) {
        Player nearestPlayer;
        if (target == null && this.m_9236_() != null && (nearestPlayer = this.m_9236_().m_45930_((Entity)this, 64.0)) != null && !nearestPlayer.m_7500_() && !nearestPlayer.m_5833_()) {
            target = nearestPlayer;
        }
        super.m_6710_(target);
        if (target instanceof Player) {
            this.m_21051_(Attributes.f_22279_).m_22100_(0.45);
        } else {
            this.m_21051_(Attributes.f_22279_).m_22100_(0.4);
        }
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.coinFlipCooldown > 0) {
            --this.coinFlipCooldown;
        }
        Player nearestPlayer = null;
        if (this.m_9236_() != null) {
            nearestPlayer = this.m_9236_().m_45930_((Entity)this, 64.0);
        }
        if (!this.initialPositionSet && this.m_9236_() != null) {
            this.initialPositionSet = true;
            BlockPos randomPos = this.findRandomPosition(64);
            if (randomPos != null) {
                this.m_6021_((double)randomPos.m_123341_() + 0.5, randomPos.m_123342_(), (double)randomPos.m_123343_() + 0.5);
                NextbotsSmartMod.LOGGER.info("Pin Head teleported to initial position: " + String.valueOf(randomPos));
            }
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
        }
        BehaviorState previousState = this.currentState;
        if (this.currentState == BehaviorState.CHASING && this.behaviorTimer >= 400) {
            this.currentState = BehaviorState.HIDING;
            this.behaviorTimer = 0;
            this.hideTarget = this.findHidingSpot(nearestPlayer);
            NextbotsSmartMod.LOGGER.info("Pin Head finished chasing and is now hiding");
        } else if (this.currentState == BehaviorState.HIDING && this.behaviorTimer >= 200) {
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
            this.hideTarget = null;
            NextbotsSmartMod.LOGGER.info("Pin Head finished hiding and is now stalking");
        }
        if (previousState != this.currentState) {
            NextbotsSmartMod.LOGGER.info("Behavior state changed from " + String.valueOf((Object)previousState) + " to " + String.valueOf((Object)this.currentState));
            this.stopSounds();
        }
        switch (this.currentState) {
            case STALKING: {
                this.handleStalkingBehavior(nearestPlayer);
                break;
            }
            case CHASING: {
                this.handleChasingBehavior(nearestPlayer);
                break;
            }
            case HIDING: {
                this.handleHidingBehavior(nearestPlayer);
                break;
            }
            case WATCHING: {
                this.handleWatchingBehavior(nearestPlayer);
                break;
            }
            case VANISHING: {
                this.handleVanishingBehavior(nearestPlayer);
                break;
            }
            case ENVIRONMENTAL_MANIPULATION: {
                this.handleEnvironmentalManipulation(nearestPlayer);
                break;
            }
        }
        ++this.behaviorTimer;
        ++this.debugTimer;

        // Debug logging every 5 seconds
        if (DEBUG_MODE && this.debugTimer % 100 == 0) {
            this.logDebugInfo(nearestPlayer);
        }

        // Decrement cooldowns
        if (this.coinFlipCooldown > 0) {
            --this.coinFlipCooldown;
        }
        if (this.environmentalCooldown > 0) {
            --this.environmentalCooldown;
        }
        if (this.psychologicalCooldown > 0) {
            --this.psychologicalCooldown;
        }
    }

    public boolean m_142582_(Entity entity) {
        return super.m_142582_(entity);
    }

    public boolean m_7327_(Entity entity) {
        boolean result = super.m_7327_(entity);
        if (result && entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 10, 1, false, false));
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer)entity;
                if (livingEntity.m_21223_() <= 0.0f) {
                    NextbotsSmartMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), (Object)new JumpscareMessage());
                    NextbotsSmartMod.LOGGER.info("Sent jumpscare packet to player killed by Pin Head");
                    this.playJumpscareSound((Player)player);
                }
            }
        }
        return result;
    }

    public void m_8107_() {
        Player nearestPlayer;
        super.m_8107_();
        if (this.m_5448_() == null && this.m_9236_() != null && this.f_19797_ % 5 == 0 && (nearestPlayer = this.m_9236_().m_45930_((Entity)this, 64.0)) != null && !nearestPlayer.m_5833_()) {
            this.m_6710_((LivingEntity)nearestPlayer);
        }
    }

    public static void init() {
        SpawnPlacements.m_21754_((EntityType)((EntityType)NextbotsSmartModEntities.PIN_HEAD.get()), (SpawnPlacements.Type)SpawnPlacements.Type.ON_GROUND, (Heightmap.Types)Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> world.m_46791_() != Difficulty.PEACEFUL && Monster.m_219009_((ServerLevelAccessor)world, (BlockPos)pos, (RandomSource)random) && Mob.m_217057_((EntityType)entityType, (LevelAccessor)world, (MobSpawnType)reason, (BlockPos)pos, (RandomSource)random));
    }

    private BlockPos findRandomPosition(int radius) {
        if (this.m_9236_() == null) {
            return null;
        }
        for (int attempts = 10; attempts > 0; --attempts) {
            int x = this.m_20183_().m_123341_() + (this.f_19796_.m_188503_(radius * 2) - radius);
            int z = this.m_20183_().m_123343_() + (this.f_19796_.m_188503_(radius * 2) - radius);
            int y = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            if (!this.m_9236_().m_8055_(pos.m_7495_()).m_60815_() || !this.m_9236_().m_8055_(pos).m_60795_()) continue;
            return pos;
        }
        return this.m_20183_();
    }

    private void handleStalkingBehavior(Player player) {
        if (player == null) {
            BlockPos randomPos;
            if (this.behaviorTimer % 100 == 0 && !this.m_21573_().m_26572_() && (randomPos = this.findRandomPosition(16)) != null) {
                this.m_21573_().m_26519_((double)randomPos.m_123341_(), (double)randomPos.m_123342_(), (double)randomPos.m_123343_(), 0.3);
            }
            return;
        }
        this.m_21051_(Attributes.f_22279_).m_22100_(0.25);
        boolean playerCanSeeMe = player.m_142582_((Entity)this);
        double distance = this.m_20270_((Entity)player);
        if (playerCanSeeMe && distance <= 7.0) {
            this.currentState = BehaviorState.CHASING;
            NextbotsSmartMod.LOGGER.info("Pin Head spotted at close range! Immediately chasing!");
            this.behaviorTimer = 0;
            return;
        }
        if (!this.m_21573_().m_26572_() || this.behaviorTimer % 40 == 0) {
            if (distance > 20.0) {
                this.findPathWithCover(player, 20.0);
            } else if (distance > 10.0) {
                this.findFlankingPosition(player, 10.0);
            } else {
                this.findAmbushPosition(player, 5.0);
            }
        }

        // Play distant footsteps when stalking and moving
        if (this.m_21573_().m_26572_() && !playerCanSeeMe && distance > 8.0 && distance < 25.0 && this.behaviorTimer % 80 == 0 && !this.m_9236_().m_5776_()) {
            this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), (SoundEvent)NextbotsSmartModSounds.FOOTSTEPS.get(), SoundSource.HOSTILE, 0.4f, 0.9f);
        }
        if (playerCanSeeMe && !this.hasBeenSeen && this.coinFlipCooldown <= 0) {
            this.hasBeenSeen = true;
            this.coinFlipCooldown = 200;
            int choice = this.f_19796_.m_188503_(100);
            if (choice < 30) {
                this.currentState = BehaviorState.HIDING;
                this.hideTarget = this.findHidingSpot(player);
                if (DEBUG_MODE) NextbotsSmartMod.LOGGER.info("Pin Head decided to hide at: " + String.valueOf(this.hideTarget) + " (Choice: " + choice + ", Distance: " + String.format("%.1f", distance) + ")");
            } else if (choice < 50) {
                this.currentState = BehaviorState.VANISHING;
                this.vanishingTimer = 0;
                if (DEBUG_MODE) NextbotsSmartMod.LOGGER.info("Pin Head decided to vanish! (Choice: " + choice + ", Distance: " + String.format("%.1f", distance) + ")");
            } else if (choice < 70) {
                this.currentState = BehaviorState.WATCHING;
                this.watchingTimer = 0;
                this.watchingPosition = this.findWatchingPosition(player);
                if (DEBUG_MODE) NextbotsSmartMod.LOGGER.info("Pin Head decided to watch from: " + String.valueOf(this.watchingPosition) + " (Choice: " + choice + ", Distance: " + String.format("%.1f", distance) + ")");
            } else {
                this.currentState = BehaviorState.CHASING;
                if (DEBUG_MODE) NextbotsSmartMod.LOGGER.info("Pin Head decided to chase the player! (Choice: " + choice + ", Distance: " + String.format("%.1f", distance) + ")");
            }
            this.behaviorTimer = 0;
        }

        // Random chance to manipulate environment when stalking
        if (!playerCanSeeMe && this.behaviorTimer % 200 == 0 && this.environmentalCooldown <= 0 && this.f_19796_.m_188503_(100) < 15) {
            this.currentState = BehaviorState.ENVIRONMENTAL_MANIPULATION;
            this.environmentalCooldown = 600; // 30 second cooldown
            this.behaviorTimer = 0;
            if (DEBUG_MODE) NextbotsSmartMod.LOGGER.info("Pin Head decided to manipulate the environment! (Distance: " + String.format("%.1f", distance) + ")");
        }

        // Psychological horror effects when stalking
        if (!playerCanSeeMe && this.behaviorTimer % 300 == 0 && this.psychologicalCooldown <= 0 && this.f_19796_.m_188503_(100) < 25) {
            this.createPsychologicalEffects(player);
            this.psychologicalCooldown = 800; // 40 second cooldown
            if (DEBUG_MODE) NextbotsSmartMod.LOGGER.info("Pin Head triggered psychological effects! (Distance: " + String.format("%.1f", distance) + ")");
        }

        // Apply paranoid effects to player when being stalked for a long time
        if (distance < 30.0 && !playerCanSeeMe) {
            ++this.paranoidEffectTimer;
            if (this.paranoidEffectTimer > 600 && this.paranoidEffectTimer % 100 == 0) { // After 30 seconds of stalking
                this.applyParanoidEffects(player);
                if (DEBUG_MODE) NextbotsSmartMod.LOGGER.info("Pin Head applied paranoid effects! (Stalking time: " + (this.paranoidEffectTimer / 20) + "s, Distance: " + String.format("%.1f", distance) + ")");
            }
        } else {
            if (this.paranoidEffectTimer > 0 && DEBUG_MODE) {
                NextbotsSmartMod.LOGGER.info("Pin Head stopped stalking, resetting paranoid timer (was at " + (this.paranoidEffectTimer / 20) + "s)");
            }
            this.paranoidEffectTimer = 0;
        }
        if (!playerCanSeeMe && this.hasBeenSeen) {
            this.hasBeenSeen = false;
        }
    }

    private void handleChasingBehavior(Player player) {
        if (player == null || this.behaviorTimer > 400) {
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
            this.hasBeenSeen = false;
            return;
        }
        this.m_21051_(Attributes.f_22279_).m_22100_(0.5);
        if (this.behaviorTimer % 10 == 0 || !this.m_21573_().m_26572_()) {
            this.m_21573_().m_5624_((Entity)player, 1.5);
        }
        this.m_6710_((LivingEntity)player);
    }

    private void handleHidingBehavior(Player player) {
        if (this.hideTarget == null && player != null) {
            this.hideTarget = this.findHidingSpot(player);
            NextbotsSmartMod.LOGGER.info("Pin Head found new hiding spot at: " + String.valueOf(this.hideTarget));
        }
        if (this.hideTarget == null) {
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
            this.hasBeenSeen = false;
            return;
        }
        this.m_21051_(Attributes.f_22279_).m_22100_(0.45);
        if (!this.m_21573_().m_26572_() || this.behaviorTimer % 20 == 0) {
            BlockPos newHideTarget;
            if (player != null && player.m_142582_((Entity)this) && this.behaviorTimer > 40 && (newHideTarget = this.findBetterHidingSpot(player)) != null) {
                this.hideTarget = newHideTarget;
                NextbotsSmartMod.LOGGER.info("Pin Head found better hiding spot at: " + String.valueOf(this.hideTarget));
            }
            this.m_21573_().m_26519_((double)this.hideTarget.m_123341_(), (double)this.hideTarget.m_123342_(), (double)this.hideTarget.m_123343_(), 1.2);
        }
        if (this.m_20275_(this.hideTarget.m_123341_(), this.hideTarget.m_123342_(), this.hideTarget.m_123343_()) < 4.0 && player != null && !player.m_142582_((Entity)this) && this.behaviorTimer % 40 == 0 && this.f_19796_.m_188503_(100) < 20) {
            this.hideTarget = this.findHidingSpot(player);
            NextbotsSmartMod.LOGGER.info("Pin Head moving to new hiding spot at: " + String.valueOf(this.hideTarget));
        }
    }

    private BlockPos findHidingSpot(Player player) {
        double dz;
        if (this.m_9236_() == null || player == null) {
            return this.m_20183_();
        }
        for (int attempt = 0; attempt < 5; ++attempt) {
            double dz2;
            double dx;
            double angle = this.f_19796_.m_188500_() * Math.PI * 2.0;
            if (attempt == 0) {
                dx = this.m_20185_() - player.m_20185_();
                dz2 = this.m_20189_() - player.m_20189_();
                angle = Math.atan2(dz2, dx);
            }
            dx = Math.cos(angle);
            dz2 = Math.sin(angle);
            int hideDistance = 24 + this.f_19796_.m_188503_(17);
            int x = (int)(player.m_20185_() + (dx *= (double)hideDistance));
            int z = (int)(player.m_20189_() + (dz2 *= (double)hideDistance));
            int y = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos hidePos = new BlockPos(x, y, z);
            boolean hasLineOfSight = true;
            double distSq = player.m_20275_((double)x, (double)y, (double)z);
            if (distSq > 900.0) {
                hasLineOfSight = false;
            }
            if (hasLineOfSight) continue;
            return hidePos;
        }
        double dx = this.m_20185_() - player.m_20185_();
        double length = Math.sqrt(dx * dx + (dz = this.m_20189_() - player.m_20189_()) * dz);
        if (length > 0.0) {
            dx /= length;
            dz /= length;
        }
        int hideDistance = 32;
        int x = (int)(player.m_20185_() + (dx *= (double)hideDistance));
        int z = (int)(player.m_20189_() + (dz *= (double)hideDistance));
        int y = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        return new BlockPos(x, y, z);
    }

    private void findPathWithCover(Player player, double maxDistance) {
        double dz;
        if (this.m_9236_() == null || player == null) {
            return;
        }
        for (int attempt = 0; attempt < 5; ++attempt) {
            double dz2;
            double angle = this.f_19796_.m_188500_() * Math.PI / 2.0 - 0.7853981633974483;
            double dx = player.m_20185_() - this.m_20185_();
            double length = Math.sqrt(dx * dx + (dz2 = player.m_20189_() - this.m_20189_()) * dz2);
            if (length > 0.0) {
                dx /= length;
                dz2 /= length;
            }
            double newDx = dx * Math.cos(angle) - dz2 * Math.sin(angle);
            double newDz = dx * Math.sin(angle) + dz2 * Math.cos(angle);
            double distance = Math.min(length * 0.7, maxDistance);
            double targetX = this.m_20185_() + (newDx *= distance);
            double targetZ = this.m_20189_() + (newDz *= distance);
            int targetY = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int)targetX, (int)targetZ);
            if (!this.hasNearbyObstacles(targetX, targetY, targetZ, 3.0)) continue;
            this.m_21573_().m_26519_(targetX, (double)targetY, targetZ, 0.4);
            NextbotsSmartMod.LOGGER.info("Pin Head found path with cover toward player");
            return;
        }
        double distance = Math.min((double)this.m_20270_((Entity)player) * 0.6, maxDistance);
        double dx = player.m_20185_() - this.m_20185_();
        double length = Math.sqrt(dx * dx + (dz = player.m_20189_() - this.m_20189_()) * dz);
        if (length > 0.0) {
            dx = dx / length * distance;
            dz = dz / length * distance;
        }
        double targetX = this.m_20185_() + dx;
        double targetZ = this.m_20189_() + dz;
        int targetY = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int)targetX, (int)targetZ);
        this.m_21573_().m_26519_(targetX, (double)targetY, targetZ, 0.3);
    }

    private void findFlankingPosition(Player player, double maxDistance) {
        double dz;
        if (this.m_9236_() == null || player == null) {
            return;
        }
        double angle = (double)(this.f_19796_.m_188499_() ? 1 : -1) * 1.5707963267948966;
        double dx = player.m_20185_() - this.m_20185_();
        double length = Math.sqrt(dx * dx + (dz = player.m_20189_() - this.m_20189_()) * dz);
        if (length > 0.0) {
            dx /= length;
            dz /= length;
        }
        double newDx = dx * Math.cos(angle) - dz * Math.sin(angle);
        double newDz = dx * Math.sin(angle) + dz * Math.cos(angle);
        double distance = Math.min(length * 0.8, maxDistance);
        double targetX = player.m_20185_() + (newDx *= distance);
        double targetZ = player.m_20189_() + (newDz *= distance);
        int targetY = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int)targetX, (int)targetZ);
        this.m_21573_().m_26519_(targetX, (double)targetY, targetZ, 0.4);
        NextbotsSmartMod.LOGGER.info("Pin Head moving to flanking position");
    }

    private void findAmbushPosition(Player player, double maxDistance) {
        double fallbackDz;
        if (this.m_9236_() == null || player == null) {
            return;
        }
        for (int attempt = 0; attempt < 3; ++attempt) {
            double angle = this.f_19796_.m_188500_() * Math.PI * 2.0;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            double distance = 3.0 + this.f_19796_.m_188500_() * (maxDistance - 3.0);
            double targetX = player.m_20185_() + (dx *= distance);
            double targetZ = player.m_20189_() + (dz *= distance);
            int targetY = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int)targetX, (int)targetZ);
            if (!this.hasNearbyObstacles(targetX, targetY, targetZ, 2.0)) continue;
            double oldX = this.m_20185_();
            double oldY = this.m_20186_();
            double oldZ = this.m_20189_();
            this.m_6034_(targetX, targetY + 1, targetZ);
            boolean canSee = player.m_142582_((Entity)this);
            this.m_6034_(oldX, oldY, oldZ);
            if (canSee) continue;
            this.m_21573_().m_26519_(targetX, (double)targetY, targetZ, 0.5);
            NextbotsSmartMod.LOGGER.info("Pin Head found ambush position");
            return;
        }
        double fallbackDistance = Math.min(3.0, (double)this.m_20270_((Entity)player) * 0.5);
        double fallbackDx = player.m_20185_() - this.m_20185_();
        double fallbackLength = Math.sqrt(fallbackDx * fallbackDx + (fallbackDz = player.m_20189_() - this.m_20189_()) * fallbackDz);
        if (fallbackLength > 0.0) {
            fallbackDx = fallbackDx / fallbackLength * fallbackDistance;
            fallbackDz = fallbackDz / fallbackLength * fallbackDistance;
        }
        double fallbackX = this.m_20185_() + fallbackDx;
        double fallbackZ = this.m_20189_() + fallbackDz;
        int fallbackY = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int)fallbackX, (int)fallbackZ);
        this.m_21573_().m_26519_(fallbackX, (double)fallbackY, fallbackZ, 0.5);
        NextbotsSmartMod.LOGGER.info("Pin Head moving closer to player for ambush");
    }

    private boolean hasNearbyObstacles(double x, double y, double z, double radius) {
        if (this.m_9236_() == null) {
            return false;
        }
        for (int offsetX = -1; offsetX <= 1; ++offsetX) {
            for (int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
                if (offsetX == 0 && offsetZ == 0) continue;
                double checkX = x + (double)offsetX * radius;
                double checkZ = z + (double)offsetZ * radius;
                BlockPos checkPos = new BlockPos((int)checkX, (int)y, (int)checkZ);
                if (!this.m_9236_().m_8055_(checkPos).m_60815_()) continue;
                return true;
            }
        }
        return false;
    }

    private BlockPos findBetterHidingSpot(Player player) {
        if (this.m_9236_() == null || player == null) {
            return this.m_20183_();
        }
        for (int attempt = 0; attempt < 8; ++attempt) {
            double angle = this.f_19796_.m_188500_() * Math.PI * 2.0;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            int hideDistance = 10 + this.f_19796_.m_188503_(15);
            int x = (int)(this.m_20185_() + (dx *= (double)hideDistance));
            int z = (int)(this.m_20189_() + (dz *= (double)hideDistance));
            int y = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos hidePos = new BlockPos(x, y, z);
            boolean hasLineOfSight = true;
            double distSq = player.m_20275_((double)x, (double)y, (double)z);
            if (distSq > 400.0) {
                hasLineOfSight = false;
            }
            if (hasLineOfSight) continue;
            return hidePos;
        }
        return this.findHidingSpot(player);
    }

    private void stopSounds() {
        NextbotsSmartMod.LOGGER.info("Stopping all sounds - server side notification");
    }

    private void playJumpscareSound(Player player) {
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            if (this.m_9236_() != null && !this.m_9236_().m_5776_()) {
                this.m_9236_().m_6263_((Player)serverPlayer, serverPlayer.m_20185_(), serverPlayer.m_20186_(), serverPlayer.m_20189_(), (SoundEvent)NextbotsSmartModSounds.JUMPSCARE.get(), SoundSource.HOSTILE, 2.0f, 1.0f);
                this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), (SoundEvent)NextbotsSmartModSounds.JUMPSCARE.get(), SoundSource.HOSTILE, 1.5f, 1.0f);
                NextbotsSmartMod.LOGGER.info("Playing jumpscare sound for player: " + serverPlayer.m_7755_().getString());
            }
        }
    }

    private void handleWatchingBehavior(Player player) {
        if (player == null || this.watchingTimer > 300) {
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
            this.watchingPosition = null;
            this.isWatchingPlayer = false;
            return;
        }

        if (this.watchingPosition == null) {
            this.watchingPosition = this.findWatchingPosition(player);
        }

        if (this.watchingPosition != null) {
            // Move to watching position
            if (this.m_20275_(this.watchingPosition.m_123341_(), this.watchingPosition.m_123342_(), this.watchingPosition.m_123343_()) > 3.0) {
                this.m_21573_().m_26519_((double)this.watchingPosition.m_123341_(), (double)this.watchingPosition.m_123342_(), (double)this.watchingPosition.m_123343_(), 0.3);
            } else {
                // At watching position, look at player
                this.isWatchingPlayer = true;
                this.m_21573_().m_7471_();
                this.m_5616_((Entity)player);

                // Play subtle watching sounds occasionally
                if (this.watchingTimer % 60 == 0 && !this.m_9236_().m_5776_()) {
                    this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), (SoundEvent)NextbotsSmartModSounds.BREATHING.get(), SoundSource.HOSTILE, 0.3f, 0.8f);
                }

                // If player looks at us while watching, chance to vanish or chase
                if (player.m_142582_((Entity)this) && this.f_19796_.m_188503_(100) < 30) {
                    if (this.f_19796_.m_188503_(100) < 60) {
                        this.currentState = BehaviorState.VANISHING;
                        this.vanishingTimer = 0;
                        NextbotsSmartMod.LOGGER.info("Pin Head vanishing after being spotted watching!");
                    } else {
                        this.currentState = BehaviorState.CHASING;
                        NextbotsSmartMod.LOGGER.info("Pin Head chasing after being spotted watching!");
                    }
                    this.behaviorTimer = 0;
                }
            }
        }

        ++this.watchingTimer;
    }

    private void handleVanishingBehavior(Player player) {
        if (this.vanishingTimer > 60) {
            // Teleport to a new stalking position far from player
            if (player != null) {
                BlockPos newPos = this.findDistantPosition(player, 30, 50);
                if (newPos != null) {
                    this.m_6034_((double)newPos.m_123341_(), (double)newPos.m_123342_(), (double)newPos.m_123343_());
                    NextbotsSmartMod.LOGGER.info("Pin Head vanished and teleported to: " + String.valueOf(newPos));
                }
            }
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
            this.vanishingTimer = 0;
            this.hasBeenSeen = false;
            this.m_20359_(false); // Make visible again
            return;
        }

        // Make entity invisible/transparent during vanishing
        this.m_20359_(true);

        // Play vanish sound at the start
        if (this.vanishingTimer == 1 && !this.m_9236_().m_5776_()) {
            this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), (SoundEvent)NextbotsSmartModSounds.VANISH.get(), SoundSource.HOSTILE, 0.8f, 1.0f);
        }

        ++this.vanishingTimer;
    }

    private void handleEnvironmentalManipulation(Player player) {
        if (this.behaviorTimer > 100 || player == null) {
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
            this.hasManipulatedEnvironment = false;
            return;
        }

        if (!this.hasManipulatedEnvironment && this.behaviorTimer > 20) {
            // Try to manipulate environment near player
            this.manipulateEnvironmentNearPlayer(player);
            this.hasManipulatedEnvironment = true;
        }
    }

    private BlockPos findWatchingPosition(Player player) {
        if (player == null || this.m_9236_() == null) {
            return this.m_20183_();
        }

        for (int attempt = 0; attempt < 8; ++attempt) {
            double angle = this.f_19796_.m_188500_() * Math.PI * 2.0;
            double distance = 15 + this.f_19796_.m_188503_(10); // 15-25 blocks away
            double dx = Math.cos(angle) * distance;
            double dz = Math.sin(angle) * distance;

            int x = (int)(player.m_20185_() + dx);
            int z = (int)(player.m_20189_() + dz);
            int y = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

            BlockPos watchPos = new BlockPos(x, y, z);

            // Check if position has line of sight to player
            if (this.hasLineOfSight(watchPos, player.m_20183_())) {
                return watchPos;
            }
        }

        return this.m_20183_();
    }

    private BlockPos findDistantPosition(Player player, int minDistance, int maxDistance) {
        if (player == null || this.m_9236_() == null) {
            return this.m_20183_();
        }

        for (int attempt = 0; attempt < 10; ++attempt) {
            double angle = this.f_19796_.m_188500_() * Math.PI * 2.0;
            double distance = minDistance + this.f_19796_.m_188503_(maxDistance - minDistance);
            double dx = Math.cos(angle) * distance;
            double dz = Math.sin(angle) * distance;

            int x = (int)(player.m_20185_() + dx);
            int z = (int)(player.m_20189_() + dz);
            int y = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

            BlockPos newPos = new BlockPos(x, y, z);

            // Make sure position is safe and not in player's line of sight
            if (!player.m_142582_((Entity)this) && this.m_9236_().m_8055_(newPos).m_60795_()) {
                return newPos;
            }
        }

        return this.m_20183_();
    }

    private void manipulateEnvironmentNearPlayer(Player player) {
        if (player == null || this.m_9236_() == null || this.m_9236_().m_5776_()) {
            return;
        }

        // Create atmospheric effects around the player
        int effectType = this.f_19796_.m_188503_(4);

        switch (effectType) {
            case 0: // Spawn particles around player
                this.createSpookyParticles(player);
                break;
            case 1: // Play distant sounds
                this.playDistantSounds(player);
                break;
            case 2: // Create temporary darkness
                this.createTemporaryDarkness(player);
                break;
            case 3: // Manipulate nearby blocks
                this.manipulateNearbyBlocks(player);
                break;
        }

        NextbotsSmartMod.LOGGER.info("Pin Head manipulated environment with effect type: " + effectType);
    }

    private void createSpookyParticles(Player player) {
        if (this.m_9236_().m_5776_()) return;

        for (int i = 0; i < 10; ++i) {
            double offsetX = (this.f_19796_.m_188500_() - 0.5) * 20.0;
            double offsetZ = (this.f_19796_.m_188500_() - 0.5) * 20.0;
            double offsetY = this.f_19796_.m_188500_() * 3.0;

            double x = player.m_20185_() + offsetX;
            double y = player.m_20186_() + offsetY;
            double z = player.m_20189_() + offsetZ;

            // Add smoke particles for spooky atmosphere
            this.m_9236_().m_7106_(net.minecraft.core.particles.ParticleTypes.f_123794_, x, y, z, 0.0, 0.1, 0.0);
        }
    }

    private void playDistantSounds(Player player) {
        if (this.m_9236_().m_5776_()) return;

        // Play sounds from random directions around the player
        double angle = this.f_19796_.m_188500_() * Math.PI * 2.0;
        double distance = 15 + this.f_19796_.m_188500_() * 10; // 15-25 blocks away

        double x = player.m_20185_() + Math.cos(angle) * distance;
        double y = player.m_20186_();
        double z = player.m_20189_() + Math.sin(angle) * distance;

        this.m_9236_().m_6263_(null, x, y, z, (SoundEvent)NextbotsSmartModSounds.AMBIENTSPOOKY.get(), SoundSource.HOSTILE, 0.5f, 0.8f);
    }

    private void createTemporaryDarkness(Player player) {
        // This would require more complex implementation with light manipulation
        // For now, just create dark particles around the player
        if (this.m_9236_().m_5776_()) return;

        for (int i = 0; i < 15; ++i) {
            double offsetX = (this.f_19796_.m_188500_() - 0.5) * 10.0;
            double offsetZ = (this.f_19796_.m_188500_() - 0.5) * 10.0;
            double offsetY = this.f_19796_.m_188500_() * 2.0;

            double x = player.m_20185_() + offsetX;
            double y = player.m_20186_() + offsetY;
            double z = player.m_20189_() + offsetZ;

            // Add dark particles
            this.m_9236_().m_7106_(net.minecraft.core.particles.ParticleTypes.f_123803_, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    private void manipulateNearbyBlocks(Player player) {
        if (this.m_9236_().m_5776_()) return;

        // Find blocks near player to manipulate
        for (int attempt = 0; attempt < 3; ++attempt) {
            int offsetX = this.f_19796_.m_188503_(21) - 10; // -10 to +10
            int offsetZ = this.f_19796_.m_188503_(21) - 10;
            int offsetY = this.f_19796_.m_188503_(5) - 2; // -2 to +2

            BlockPos targetPos = player.m_20183_().m_7918_(offsetX, offsetY, offsetZ);

            // Only manipulate if far enough from player and not air
            if (player.m_20275_((double)targetPos.m_123341_(), (double)targetPos.m_123342_(), (double)targetPos.m_123343_()) > 16.0
                && !this.m_9236_().m_8055_(targetPos).m_60795_()) {

                // Create break particles to simulate block manipulation
                this.m_9236_().m_7106_(net.minecraft.core.particles.ParticleTypes.f_123794_,
                    (double)targetPos.m_123341_() + 0.5,
                    (double)targetPos.m_123342_() + 0.5,
                    (double)targetPos.m_123343_() + 0.5,
                    0.0, 0.1, 0.0);

                // Play block break sound
                this.m_9236_().m_6263_(null,
                    (double)targetPos.m_123341_(),
                    (double)targetPos.m_123342_(),
                    (double)targetPos.m_123343_(),
                    net.minecraft.sounds.SoundEvents.f_12083_,
                    SoundSource.BLOCKS, 0.3f, 0.8f);
                break;
            }
        }
    }

    private void createPsychologicalEffects(Player player) {
        if (player == null || this.m_9236_() == null || this.m_9236_().m_5776_()) {
            return;
        }

        int effectType = this.f_19796_.m_188503_(5);

        switch (effectType) {
            case 0: // Leave signs of presence behind player
                this.leavePresenceSigns(player);
                break;
            case 1: // Create false movement sounds
                this.createFalseMovementSounds(player);
                break;
            case 2: // Manipulate player's peripheral vision
                this.createPeripheralEffects(player);
                break;
            case 3: // Create temporary structures
                this.createTemporaryStructures(player);
                break;
            case 4: // Make player feel watched
                this.createWatchingEffects(player);
                break;
        }

        if (DEBUG_MODE) NextbotsSmartMod.LOGGER.info("Pin Head created psychological effect type: " + effectType);
    }

    private void applyParanoidEffects(Player player) {
        if (player == null || this.m_9236_().m_5776_()) {
            return;
        }

        // Apply subtle negative effects to make player paranoid
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;

            // Occasionally apply brief blindness or slowness
            int effectChoice = this.f_19796_.m_188503_(3);
            switch (effectChoice) {
                case 0: // Brief darkness
                    serverPlayer.m_7292_(new MobEffectInstance(MobEffects.f_19603_, 40, 0, true, false));
                    break;
                case 1: // Brief slowness
                    serverPlayer.m_7292_(new MobEffectInstance(MobEffects.f_19596_, 60, 0, true, false));
                    break;
                case 2: // Brief nausea
                    serverPlayer.m_7292_(new MobEffectInstance(MobEffects.f_19595_, 80, 0, true, false));
                    break;
            }

            if (DEBUG_MODE) NextbotsSmartMod.LOGGER.info("Applied paranoid effect " + effectChoice + " to player");
        }
    }

    private void leavePresenceSigns(Player player) {
        // Leave subtle signs that the entity was near the player's previous location
        BlockPos playerPrevPos = new BlockPos((int)player.m_20185_(), (int)player.m_20186_(), (int)player.m_20189_());

        // Look for a position behind the player
        double angle = Math.toRadians(player.m_146908_() + 180); // Behind player
        int distance = 5 + this.f_19796_.m_188503_(5); // 5-10 blocks behind

        int x = (int)(player.m_20185_() + Math.cos(angle) * distance);
        int z = (int)(player.m_20189_() + Math.sin(angle) * distance);
        int y = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

        BlockPos signPos = new BlockPos(x, y, z);

        // Create particles to simulate disturbed ground
        for (int i = 0; i < 5; ++i) {
            this.m_9236_().m_7106_(net.minecraft.core.particles.ParticleTypes.f_123794_,
                (double)signPos.m_123341_() + this.f_19796_.m_188500_(),
                (double)signPos.m_123342_() + 0.1,
                (double)signPos.m_123343_() + this.f_19796_.m_188500_(),
                0.0, 0.05, 0.0);
        }
    }

    private void createFalseMovementSounds(Player player) {
        // Play footstep sounds from random directions around the player
        for (int i = 0; i < 2; ++i) {
            double angle = this.f_19796_.m_188500_() * Math.PI * 2.0;
            double distance = 8 + this.f_19796_.m_188500_() * 12; // 8-20 blocks away

            double x = player.m_20185_() + Math.cos(angle) * distance;
            double y = player.m_20186_();
            double z = player.m_20189_() + Math.sin(angle) * distance;

            this.m_9236_().m_6263_(null, x, y, z, net.minecraft.sounds.SoundEvents.f_12083_, SoundSource.BLOCKS, 0.3f, 0.8f);
        }
    }

    private void createPeripheralEffects(Player player) {
        // Create brief particle effects at the edge of player's vision
        double playerYaw = Math.toRadians(player.m_146908_());

        // Create effects 90 degrees to the left and right
        for (int side = -1; side <= 1; side += 2) {
            double angle = playerYaw + (Math.PI / 2) * side;
            double distance = 12 + this.f_19796_.m_188500_() * 8;

            double x = player.m_20185_() + Math.cos(angle) * distance;
            double y = player.m_20186_() + 1;
            double z = player.m_20189_() + Math.sin(angle) * distance;

            // Create dark particles
            for (int i = 0; i < 8; ++i) {
                this.m_9236_().m_7106_(net.minecraft.core.particles.ParticleTypes.f_123803_,
                    x + (this.f_19796_.m_188500_() - 0.5) * 2,
                    y + this.f_19796_.m_188500_() * 2,
                    z + (this.f_19796_.m_188500_() - 0.5) * 2,
                    0.0, 0.0, 0.0);
            }
        }
    }

    private void createTemporaryStructures(Player player) {
        // This would create temporary blocks that appear and disappear
        // For now, just create particle effects that simulate structures
        BlockPos structurePos = player.m_20183_().m_7918_(
            this.f_19796_.m_188503_(21) - 10,
            0,
            this.f_19796_.m_188503_(21) - 10
        );

        // Create particles in a structure-like pattern
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                for (int z = 0; z < 3; ++z) {
                    if (this.f_19796_.m_188503_(100) < 30) { // 30% chance for each position
                        this.m_9236_().m_7106_(net.minecraft.core.particles.ParticleTypes.f_123794_,
                            (double)structurePos.m_123341_() + x,
                            (double)structurePos.m_123342_() + y,
                            (double)structurePos.m_123343_() + z,
                            0.0, 0.1, 0.0);
                    }
                }
            }
        }
    }

    private void createWatchingEffects(Player player) {
        // Create subtle effects that make the player feel watched
        // Play very quiet breathing sounds from behind
        double angle = Math.toRadians(player.m_146908_() + 180 + (this.f_19796_.m_188500_() - 0.5) * 60);
        double distance = 15 + this.f_19796_.m_188500_() * 10;

        double x = player.m_20185_() + Math.cos(angle) * distance;
        double y = player.m_20186_();
        double z = player.m_20189_() + Math.sin(angle) * distance;

        this.m_9236_().m_6263_(null, x, y, z, (SoundEvent)NextbotsSmartModSounds.BREATHING.get(), SoundSource.HOSTILE, 0.2f, 0.6f);

        // Create very faint particles
        for (int i = 0; i < 3; ++i) {
            this.m_9236_().m_7106_(net.minecraft.core.particles.ParticleTypes.f_123803_,
                x + (this.f_19796_.m_188500_() - 0.5) * 2,
                y + this.f_19796_.m_188500_() * 2,
                z + (this.f_19796_.m_188500_() - 0.5) * 2,
                0.0, 0.0, 0.0);
        }
    }

    // ========== DEBUG AND TESTING METHODS ==========

    private void logDebugInfo(Player player) {
        if (player == null) return;

        double distance = this.m_20270_((Entity)player);
        boolean playerCanSeeMe = this.canPlayerSeeEntity(player, this);

        NextbotsSmartMod.LOGGER.info("=== PIN HEAD DEBUG INFO ===");
        NextbotsSmartMod.LOGGER.info("Current State: " + this.currentState);
        NextbotsSmartMod.LOGGER.info("Distance to Player: " + String.format("%.2f", distance));
        NextbotsSmartMod.LOGGER.info("Player Can See Me: " + playerCanSeeMe);
        NextbotsSmartMod.LOGGER.info("Behavior Timer: " + this.behaviorTimer);
        NextbotsSmartMod.LOGGER.info("Watching Timer: " + this.watchingTimer);
        NextbotsSmartMod.LOGGER.info("Vanishing Timer: " + this.vanishingTimer);
        NextbotsSmartMod.LOGGER.info("Paranoid Effect Timer: " + this.paranoidEffectTimer);
        NextbotsSmartMod.LOGGER.info("Environmental Cooldown: " + this.environmentalCooldown);
        NextbotsSmartMod.LOGGER.info("Psychological Cooldown: " + this.psychologicalCooldown);
        NextbotsSmartMod.LOGGER.info("Has Been Seen: " + this.hasBeenSeen);
        NextbotsSmartMod.LOGGER.info("Is Watching Player: " + this.isWatchingPlayer);
        NextbotsSmartMod.LOGGER.info("Position: " + String.format("%.1f, %.1f, %.1f", this.m_20185_(), this.m_20186_(), this.m_20189_()));
        if (this.watchingPosition != null) {
            NextbotsSmartMod.LOGGER.info("Watching Position: " + this.watchingPosition);
        }
        NextbotsSmartMod.LOGGER.info("========================");
    }

    // Test function to force specific behavior states
    public void testForceBehaviorState(BehaviorState state) {
        if (!TEST_MODE) {
            NextbotsSmartMod.LOGGER.info("Test mode not enabled! Use /pinhead_test enable first");
            return;
        }

        this.currentState = state;
        this.behaviorTimer = 0;

        switch (state) {
            case WATCHING:
                this.watchingTimer = 0;
                this.watchingPosition = null;
                break;
            case VANISHING:
                this.vanishingTimer = 0;
                break;
            case ENVIRONMENTAL_MANIPULATION:
                this.environmentalCooldown = 0;
                break;
        }

        NextbotsSmartMod.LOGGER.info("TEST: Forced Pin Head to " + state + " behavior state");
    }

    // Test function to trigger psychological effects
    public void testPsychologicalEffects(Player player) {
        if (!TEST_MODE) {
            NextbotsSmartMod.LOGGER.info("Test mode not enabled! Use /pinhead_test enable first");
            return;
        }

        if (player != null) {
            this.createPsychologicalEffects(player);
            NextbotsSmartMod.LOGGER.info("TEST: Triggered psychological effects on " + player.m_7755_().getString());
        }
    }

    // Test function to trigger environmental manipulation
    public void testEnvironmentalManipulation(Player player) {
        if (!TEST_MODE) {
            NextbotsSmartMod.LOGGER.info("Test mode not enabled! Use /pinhead_test enable first");
            return;
        }

        if (player != null) {
            this.manipulateEnvironmentNearPlayer(player);
            NextbotsSmartMod.LOGGER.info("TEST: Triggered environmental manipulation near " + player.m_7755_().getString());
        }
    }

    // Test function to apply paranoid effects
    public void testParanoidEffects(Player player) {
        if (!TEST_MODE) {
            NextbotsSmartMod.LOGGER.info("Test mode not enabled! Use /pinhead_test enable first");
            return;
        }

        if (player != null) {
            this.applyParanoidEffects(player);
            NextbotsSmartMod.LOGGER.info("TEST: Applied paranoid effects to " + player.m_7755_().getString());
        }
    }

    // Test function to reset all timers and cooldowns
    public void testResetTimers() {
        if (!TEST_MODE) {
            NextbotsSmartMod.LOGGER.info("Test mode not enabled! Use /pinhead_test enable first");
            return;
        }

        this.behaviorTimer = 0;
        this.watchingTimer = 0;
        this.vanishingTimer = 0;
        this.paranoidEffectTimer = 0;
        this.environmentalCooldown = 0;
        this.psychologicalCooldown = 0;
        this.coinFlipCooldown = 0;
        this.hasBeenSeen = false;
        this.isWatchingPlayer = false;
        this.hasManipulatedEnvironment = false;
        this.hasLeftSign = false;

        NextbotsSmartMod.LOGGER.info("TEST: Reset all timers and cooldowns");
    }

    // Static methods for enabling/disabling debug and test modes
    public static void setDebugMode(boolean enabled) {
        DEBUG_MODE = enabled;
        NextbotsSmartMod.LOGGER.info("Pin Head Debug Mode: " + (enabled ? "ENABLED" : "DISABLED"));
    }

    public static void setTestMode(boolean enabled) {
        TEST_MODE = enabled;
        NextbotsSmartMod.LOGGER.info("Pin Head Test Mode: " + (enabled ? "ENABLED" : "DISABLED"));
    }

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }

    public static boolean isTestMode() {
        return TEST_MODE;
    }

    // Test function to spawn Pin Head near player
    public static void testSpawnNearPlayer(Player player) {
        if (!TEST_MODE) {
            NextbotsSmartMod.LOGGER.info("Test mode not enabled! Use /pinhead_test enable first");
            return;
        }

        if (player != null && !player.m_9236_().m_5776_()) {
            // Find a position behind the player
            double angle = Math.toRadians(player.m_146908_() + 180 + (player.m_217043_().m_188500_() - 0.5) * 60);
            double distance = 15 + player.m_217043_().m_188500_() * 10;

            double x = player.m_20185_() + Math.cos(angle) * distance;
            double z = player.m_20189_() + Math.sin(angle) * distance;
            double y = player.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z);

            PinHeadEntity pinHead = new PinHeadEntity(NextbotsSmartModEntities.PIN_HEAD.get(), player.m_9236_());
            pinHead.m_6034_(x, y, z);
            pinHead.setTestMode(true);

            player.m_9236_().m_7967_((Entity)pinHead);
            NextbotsSmartMod.LOGGER.info("TEST: Spawned Pin Head near " + player.m_7755_().getString() + " at " + String.format("%.1f, %.1f, %.1f", x, y, z));
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.m_21552_();
        builder = builder.m_22268_(Attributes.f_22279_, 0.4);
        builder = builder.m_22268_(Attributes.f_22276_, 40.0);
        builder = builder.m_22268_(Attributes.f_22284_, 2.0);
        builder = builder.m_22268_(Attributes.f_22281_, 40.0);
        builder = builder.m_22268_(Attributes.f_22277_, 64.0);
        builder = builder.m_22268_(Attributes.f_22278_, 0.5);
        return builder;
    }

    public static enum BehaviorState {
        STALKING,
        CHASING,
        HIDING,
        WATCHING,
        VANISHING,
        ENVIRONMENTAL_MANIPULATION;

    }
}

