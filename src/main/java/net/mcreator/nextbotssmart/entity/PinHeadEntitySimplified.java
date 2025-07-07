package net.mcreator.nextbotssmart.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.mcreator.nextbotssmart.init.NextbotsSmartModEntities;

/**
 * Simplified version of PinHeadEntity for initial compilation testing
 * This removes complex behaviors to get a basic working build
 */
public class PinHeadEntitySimplified extends Monster {
    
    public PinHeadEntitySimplified(EntityType<? extends PinHeadEntitySimplified> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.0f);
        this.xpReward = 0;
        this.setPersistenceRequired();
        this.setPathfindingMalus(BlockPathTypes.DOOR_OPEN, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DOOR_WOOD_CLOSED, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0f);
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
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.2));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 64.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));
        
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean canAttack(net.minecraft.world.entity.LivingEntity target) {
        if (target instanceof Player) {
            Player player = (Player)target;
            return !player.isCreative();
        }
        return super.canAttack(target);
    }

    @Override
    public void tick() {
        super.tick();
        // Basic tick logic - complex behaviors removed for now
    }
}
