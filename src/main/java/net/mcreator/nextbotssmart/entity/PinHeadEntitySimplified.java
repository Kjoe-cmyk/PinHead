package net.mcreator.nextbotssmart.entity;

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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.mcreator.nextbotssmart.init.NextbotsSmartModEntities;
import net.mcreator.nextbotssmart.init.NextbotsSmartModSounds;
import net.mcreator.nextbotssmart.network.JumpscareMessage;
import net.mcreator.nextbotssmart.NextbotsSmartMod;
import net.mcreator.nextbotssmart.util.PerformanceMonitor;

import javax.annotation.Nullable;

/**
 * Advanced PinHead Entity with full horror behaviors and performance monitoring
 * Restored from original with all mapping issues fixed
 */
public class PinHeadEntitySimplified extends Monster {

    // Behavior state system
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

    public enum BehaviorState {
        STALKING,
        CHASING,
        HIDING,
        WATCHING,
        VANISHING,
        ENVIRONMENTAL_MANIPULATION
    }
    
    public PinHeadEntitySimplified(EntityType<? extends PinHeadEntitySimplified> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.0f);
        this.xpReward = 0;
        this.setPersistenceRequired();
        this.setPathfindingMalus(BlockPathTypes.DOOR_OPEN, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DOOR_WOOD_CLOSED, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0f);

        PathNavigation pathNavigation = this.getNavigation();
        if (pathNavigation instanceof GroundPathNavigation) {
            GroundPathNavigation navigation = (GroundPathNavigation)pathNavigation;
            navigation.setCanOpenDoors(true);
            navigation.setCanFloat(true);
            navigation.setCanPassDoors(true);
        }
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.4);
        builder = builder.add(Attributes.MAX_HEALTH, 30.0);
        builder = builder.add(Attributes.ARMOR, 0.0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 6.0);
        builder = builder.add(Attributes.FOLLOW_RANGE, 64.0);
        return builder;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5, true){
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return (double)(this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()) + 1.0;
            }
        });
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.2));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 64.0f){
            @Override
            public boolean canUse() {
                this.lookAt = PinHeadEntitySimplified.this.level().getNearestPlayer(PinHeadEntitySimplified.this, (double)this.lookDistance);
                return this.lookAt != null;
            }
        });
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(3, new OpenDoorGoal(this, true){
            @Override
            public boolean canUse() {
                return PinHeadEntitySimplified.this.getNavigation().isDone();
            }
        });
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
    }

    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player) {
            Player player = (Player)target;
            return !player.isCreative();
        }
        return super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        Player nearestPlayer;
        if (target == null && this.level() != null && (nearestPlayer = this.level().getNearestPlayer(this, 64.0)) != null && !nearestPlayer.isSpectator() && !nearestPlayer.isCreative()) {
            target = nearestPlayer;
        }
        super.setTarget(target);
        if (target instanceof Player) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.45);
        } else {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4);
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Performance monitoring integration
        PerformanceMonitor.trackEntityTick();

        if (this.coinFlipCooldown > 0) {
            --this.coinFlipCooldown;
        }

        Player nearestPlayer = null;
        if (this.level() != null) {
            nearestPlayer = this.level().getNearestPlayer(this, 64.0);
            PerformanceMonitor.trackPlayerSearch();
        }

        // Initial position setup
        if (!this.initialPositionSet && this.level() != null) {
            this.initialPositionSet = true;
            BlockPos randomPos = this.findRandomPosition(64);
            if (randomPos != null) {
                this.setPos((double)randomPos.getX() + 0.5, randomPos.getY(), (double)randomPos.getZ() + 0.5);
                NextbotsSmartMod.LOGGER.info("Pin Head teleported to initial position: " + String.valueOf(randomPos));
            }
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
        }

        BehaviorState previousState = this.currentState;

        // Main behavior state machine
        switch (this.currentState) {
            case STALKING:
                this.handleStalkingBehavior(nearestPlayer);
                break;
            case CHASING:
                this.handleChasingBehavior(nearestPlayer);
                break;
            case HIDING:
                this.handleHidingBehavior(nearestPlayer);
                break;
            case WATCHING:
                this.handleWatchingBehavior(nearestPlayer);
                break;
            case VANISHING:
                this.handleVanishingBehavior(nearestPlayer);
                break;
            case ENVIRONMENTAL_MANIPULATION:
                this.handleEnvironmentalManipulation(nearestPlayer);
                break;
        }

        // Debug logging
        if (DEBUG_MODE && this.debugTimer++ % 100 == 0) {
            NextbotsSmartMod.LOGGER.info("Pin Head State: " + this.currentState + ", Timer: " + this.behaviorTimer + ", Player Distance: " + (nearestPlayer != null ? this.distanceTo(nearestPlayer) : "No player"));
        }

        ++this.behaviorTimer;
    }

    private void handleStalkingBehavior(Player player) {
        if (player == null) {
            BlockPos randomPos;
            if (this.behaviorTimer % 100 == 0 && !this.getNavigation().isDone() && (randomPos = this.findRandomPosition(16)) != null) {
                this.getNavigation().moveTo((double)randomPos.getX(), (double)randomPos.getY(), (double)randomPos.getZ(), 0.3);
            }
            return;
        }

        // Performance optimization based on distance
        double distance = this.distanceTo(player);
        if (PerformanceMonitor.shouldOptimizeForDistance(distance)) {
            // Reduce update frequency for distant entities
            if (this.behaviorTimer % PerformanceMonitor.getOptimizedTickInterval(distance) != 0) {
                return;
            }
        }

        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
        boolean playerCanSeeMe = player.hasLineOfSight(this);

        if (playerCanSeeMe && distance <= 7.0) {
            this.currentState = BehaviorState.CHASING;
            NextbotsSmartMod.LOGGER.info("Pin Head spotted at close range! Immediately chasing!");
            this.behaviorTimer = 0;
            return;
        }

        // Coin flip behavior for state transitions
        if (this.coinFlipCooldown <= 0) {
            this.coinFlipCooldown = 200;
            int choice = this.random.nextInt(100);
            if (choice < 30) {
                this.currentState = BehaviorState.HIDING;
                this.behaviorTimer = 0;
                NextbotsSmartMod.LOGGER.info("Pin Head switching to HIDING behavior");
            } else if (choice < 50) {
                this.currentState = BehaviorState.WATCHING;
                this.behaviorTimer = 0;
                NextbotsSmartMod.LOGGER.info("Pin Head switching to WATCHING behavior");
            } else if (choice < 65 && distance > 20.0) {
                this.currentState = BehaviorState.VANISHING;
                this.behaviorTimer = 0;
                NextbotsSmartMod.LOGGER.info("Pin Head switching to VANISHING behavior");
            }
        }

        // Stalking movement logic
        if (!this.getNavigation().isDone() || this.behaviorTimer % 40 == 0) {
            this.moveTowardsPlayerStealthily(player, 12.0, 20.0);
        }

        // Footstep sounds when close but not seen
        if (this.getNavigation().isDone() && !playerCanSeeMe && distance > 8.0 && distance < 25.0 && this.behaviorTimer % 80 == 0 && !this.level().isDay()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), NextbotsSmartModSounds.FOOTSTEPS.get(), SoundSource.HOSTILE, 0.4f, 0.9f);
        }

        // Environmental effects
        if (!playerCanSeeMe && this.behaviorTimer % 200 == 0 && this.environmentalCooldown <= 0 && this.random.nextInt(100) < 15) {
            this.triggerEnvironmentalEffect(player);
            this.environmentalCooldown = 400;
        }

        // Psychological effects
        if (!playerCanSeeMe && this.behaviorTimer % 300 == 0 && this.psychologicalCooldown <= 0 && this.random.nextInt(100) < 25) {
            this.applyPsychologicalEffect(player);
            this.psychologicalCooldown = 600;
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

        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5);
        if (this.behaviorTimer % 10 == 0 || !this.getNavigation().isDone()) {
            this.getNavigation().moveTo(player, 1.5);
        }
        this.setTarget(player);
    }

    private void handleHidingBehavior(Player player) {
        if (this.hideTarget == null && player != null) {
            this.hideTarget = this.findHidingSpot(player);
            NextbotsSmartMod.LOGGER.info("Pin Head found new hiding spot at: " + String.valueOf(this.hideTarget));
        }
        if (this.hideTarget == null) {
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
            return;
        }

        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.45);
        if (!this.getNavigation().isDone() || this.behaviorTimer % 20 == 0) {
            BlockPos newHideTarget;
            if (player != null && player.hasLineOfSight(this) && this.behaviorTimer > 40 && (newHideTarget = this.findBetterHidingSpot(player)) != null) {
                this.hideTarget = newHideTarget;
                NextbotsSmartMod.LOGGER.info("Pin Head found better hiding spot at: " + String.valueOf(this.hideTarget));
            }
            this.getNavigation().moveTo((double)this.hideTarget.getX(), (double)this.hideTarget.getY(), (double)this.hideTarget.getZ(), 1.2);
        }

        if (this.distanceToSqr(this.hideTarget.getX(), this.hideTarget.getY(), this.hideTarget.getZ()) < 4.0 && player != null && !player.hasLineOfSight(this) && this.behaviorTimer % 40 == 0 && this.random.nextInt(100) < 20) {
            this.hideTarget = this.findHidingSpot(player);
            NextbotsSmartMod.LOGGER.info("Pin Head moving to new hiding spot at: " + String.valueOf(this.hideTarget));
        }
    }

    private void handleWatchingBehavior(Player player) {
        if (player == null || this.watchingTimer > 300) {
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
            this.watchingPosition = null;
            this.isWatchingPlayer = false;
            this.watchingTimer = 0;
            return;
        }

        if (this.watchingPosition == null) {
            this.watchingPosition = this.findWatchingPosition(player);
            if (this.watchingPosition == null) {
                this.currentState = BehaviorState.STALKING;
                this.behaviorTimer = 0;
                return;
            }
        }

        // Move to watching position
        if (this.distanceToSqr(this.watchingPosition.getX(), this.watchingPosition.getY(), this.watchingPosition.getZ()) > 4.0) {
            this.getNavigation().moveTo((double)this.watchingPosition.getX(), (double)this.watchingPosition.getY(), (double)this.watchingPosition.getZ(), 0.4);
        } else {
            this.isWatchingPlayer = true;
            this.getNavigation().stop();

            // Look at player
            this.getLookControl().setLookAt(player, 30.0f, 30.0f);

            // Apply psychological effects
            if (this.watchingTimer % 60 == 0 && !player.hasLineOfSight(this)) {
                this.applyPsychologicalEffect(player);
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
                    this.setPos(newPos.getX() + 0.5, newPos.getY(), newPos.getZ() + 0.5);
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), NextbotsSmartModSounds.VANISH.get(), SoundSource.HOSTILE, 0.8f, 1.0f);
                    NextbotsSmartMod.LOGGER.info("Pin Head vanished and teleported to: " + newPos);
                }
            }
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
            this.vanishingTimer = 0;
            return;
        }

        // Become invisible/untargetable during vanishing
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
        this.getNavigation().stop();

        ++this.vanishingTimer;
    }

    private void handleEnvironmentalManipulation(Player player) {
        if (player == null || this.behaviorTimer > 200) {
            this.currentState = BehaviorState.STALKING;
            this.behaviorTimer = 0;
            this.hasManipulatedEnvironment = false;
            return;
        }

        if (!this.hasManipulatedEnvironment) {
            this.triggerEnvironmentalEffect(player);
            this.hasManipulatedEnvironment = true;
        }
    }

    private BlockPos findHidingSpot(Player player) {
        if (this.level() == null || player == null) {
            return this.blockPosition();
        }

        for (int attempt = 0; attempt < 10; ++attempt) {
            double dx;
            double dz2;
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            if (attempt == 0) {
                dx = this.getX() - player.getX();
                dz2 = this.getZ() - player.getZ();
                angle = Math.atan2(dz2, dx);
            }
            dx = Math.cos(angle);
            dz2 = Math.sin(angle);
            int hideDistance = 24 + this.random.nextInt(17);
            int x = (int)(player.getX() + (dx *= (double)hideDistance));
            int z = (int)(player.getZ() + (dz2 *= (double)hideDistance));
            int y = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).getY();

            BlockPos hidePos = new BlockPos(x, y, z);
            double distSq = player.distanceToSqr((double)x, (double)y, (double)z);

            if (distSq >= 400.0 && distSq <= 1600.0 && this.level().isEmptyBlock(hidePos) && this.level().isEmptyBlock(hidePos.above())) {
                return hidePos;
            }
        }
        return this.blockPosition();
    }

    private BlockPos findBetterHidingSpot(Player player) {
        if (this.level() == null || player == null) {
            return null;
        }

        for (int attempt = 0; attempt < 5; ++attempt) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            int hideDistance = 20 + this.random.nextInt(15);
            int x = (int)(player.getX() + dx * hideDistance);
            int z = (int)(player.getZ() + dz * hideDistance);
            int y = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).getY();

            BlockPos hidePos = new BlockPos(x, y, z);
            double distSq = player.distanceToSqr((double)x, (double)y, (double)z);

            if (distSq >= 300.0 && distSq <= 1200.0 && this.level().isEmptyBlock(hidePos) && this.level().isEmptyBlock(hidePos.above())) {
                return hidePos;
            }
        }
        return null;
    }

    private BlockPos findWatchingPosition(Player player) {
        if (this.level() == null || player == null) {
            return null;
        }

        for (int attempt = 0; attempt < 8; ++attempt) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            int watchDistance = 15 + this.random.nextInt(10);
            int x = (int)(player.getX() + dx * watchDistance);
            int z = (int)(player.getZ() + dz * watchDistance);
            int y = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).getY();

            BlockPos watchPos = new BlockPos(x, y, z);
            double distSq = player.distanceToSqr((double)x, (double)y, (double)z);

            if (distSq >= 200.0 && distSq <= 900.0 && this.level().isEmptyBlock(watchPos) && this.level().isEmptyBlock(watchPos.above())) {
                return watchPos;
            }
        }
        return null;
    }

    private BlockPos findDistantPosition(Player player, int minDistance, int maxDistance) {
        if (this.level() == null || player == null) {
            return null;
        }

        for (int attempt = 0; attempt < 10; ++attempt) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            int distance = minDistance + this.random.nextInt(maxDistance - minDistance);
            int x = (int)(player.getX() + dx * distance);
            int z = (int)(player.getZ() + dz * distance);
            int y = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).getY();

            BlockPos pos = new BlockPos(x, y, z);
            if (this.level().isEmptyBlock(pos) && this.level().isEmptyBlock(pos.above())) {
                return pos;
            }
        }
        return null;
    }

    private BlockPos findRandomPosition(int radius) {
        if (this.level() == null) {
            return null;
        }

        for (int attempt = 0; attempt < 10; ++attempt) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            double distance = this.random.nextDouble() * radius;
            int x = (int)(this.getX() + Math.cos(angle) * distance);
            int z = (int)(this.getZ() + Math.sin(angle) * distance);
            int y = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).getY();

            BlockPos pos = new BlockPos(x, y, z);
            if (this.level().isEmptyBlock(pos) && this.level().isEmptyBlock(pos.above())) {
                return pos;
            }
        }
        return null;
    }

    private void moveTowardsPlayerStealthily(Player player, double minDistance, double maxDistance) {
        if (player == null) return;

        double distance = this.distanceTo(player);
        if (distance < minDistance) {
            // Too close, move away
            double angle = Math.atan2(this.getZ() - player.getZ(), this.getX() - player.getX());
            double targetX = player.getX() + Math.cos(angle) * minDistance;
            double targetZ = player.getZ() + Math.sin(angle) * minDistance;
            int targetY = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos((int)targetX, 0, (int)targetZ)).getY();
            this.getNavigation().moveTo(targetX, targetY, targetZ, 0.4);
        } else if (distance > maxDistance) {
            // Too far, move closer
            double angle = Math.atan2(player.getZ() - this.getZ(), player.getX() - this.getX());
            double targetX = this.getX() + Math.cos(angle) * (distance - maxDistance + 5);
            double targetZ = this.getZ() + Math.sin(angle) * (distance - maxDistance + 5);
            int targetY = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos((int)targetX, 0, (int)targetZ)).getY();
            this.getNavigation().moveTo(targetX, targetY, targetZ, 0.3);
        }
    }

    private void triggerEnvironmentalEffect(Player player) {
        if (player == null || this.level().isClientSide) return;

        // Play ambient spooky sound
        this.level().playSound(null, player.getX(), player.getY(), player.getZ(), NextbotsSmartModSounds.AMBIENTSPOOKY.get(), SoundSource.AMBIENT, 0.3f, 0.8f + this.random.nextFloat() * 0.4f);

        NextbotsSmartMod.LOGGER.info("Pin Head triggered environmental effect near player");
    }

    private void applyPsychologicalEffect(Player player) {
        if (player == null || this.level().isClientSide) return;

        // Apply brief paranoia effect
        if (player instanceof ServerPlayer) {
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false));

            // Play watching sound
            this.level().playSound(null, player.getX(), player.getY(), player.getZ(), NextbotsSmartModSounds.WATCHING.get(), SoundSource.HOSTILE, 0.2f, 0.7f);
        }

        NextbotsSmartMod.LOGGER.info("Pin Head applied psychological effect to player");
    }

    public static void init() {
        // Initialization method for entity setup
    }

    public static boolean checkPinHeadSpawnRules(EntityType<PinHeadEntitySimplified> entityType, LevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (world.getDifficulty() != Difficulty.PEACEFUL) {
            return checkMobSpawnRules(entityType, (ServerLevelAccessor)world, reason, pos, random);
        }
        return false;
    }

    // Debug and testing methods
    public static void setDebugMode(boolean debug) {
        DEBUG_MODE = debug;
    }

    public static void setTestMode(boolean test) {
        TEST_MODE = test;
    }

    public void forceBehaviorState(BehaviorState state) {
        this.currentState = state;
        this.behaviorTimer = 0;
        NextbotsSmartMod.LOGGER.info("Pin Head behavior forced to: " + state);
    }

    public void triggerJumpscare(Player player) {
        if (player instanceof ServerPlayer) {
            NextbotsSmartMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new JumpscareMessage());
            NextbotsSmartMod.LOGGER.info("Jumpscare triggered for player: " + player.getName().getString());
        }
    }
}
