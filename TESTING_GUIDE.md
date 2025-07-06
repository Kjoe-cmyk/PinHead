# Pin Head Horror Mod - Testing Guide

## Quick Start Testing

### 1. Enable Test Mode
```
/pinhead_test enable
/pinhead_test debug on
```

### 2. Spawn Pin Head for Testing
```
/pinhead_test spawn
```

### 3. Basic Commands
```
/pinhead_test help                    # Show all available commands
/pinhead_test force <behavior>        # Force specific behavior
/pinhead_test trigger <effect>        # Trigger specific effects
/pinhead_test reset                   # Reset all timers
```

## Testing Different Behaviors

### Stalking Behavior (Default)
- Pin Head will follow you from a distance
- Look for: Distant footstep sounds, subtle presence
- **Debug logs**: Shows pathfinding decisions and distance calculations

### Watching Behavior
```
/pinhead_test force WATCHING
```
- Pin Head finds a position to watch you from
- Look for: Breathing sounds, feeling of being watched
- **Debug logs**: Shows watching position and timer

### Vanishing Behavior
```
/pinhead_test force VANISHING
```
- Pin Head becomes invisible and teleports away
- Look for: Vanish sound effect, entity disappearing
- **Debug logs**: Shows vanishing timer and invisibility state

### Chasing Behavior
```
/pinhead_test force CHASING
```
- Pin Head actively pursues you
- Look for: Chasing music, aggressive movement
- **Debug logs**: Shows chase target and pathfinding

### Environmental Manipulation
```
/pinhead_test force ENVIRONMENTAL_MANIPULATION
/pinhead_test trigger environmental
```
- Creates atmospheric effects around you
- Look for: Particles, distant sounds, block manipulation sounds
- **Debug logs**: Shows effect type and location

## Testing Horror Effects

### Psychological Effects
```
/pinhead_test trigger psychological
```
- Creates paranoia-inducing effects
- Look for: False sounds, peripheral vision effects, presence signs
- **Debug logs**: Shows effect type and parameters

### Paranoid Effects
```
/pinhead_test trigger paranoid
```
- Applies negative status effects
- Look for: Brief blindness, slowness, or nausea
- **Debug logs**: Shows which effect was applied

## What to Look For During Testing

### Audio Cues
- **Footsteps**: During stalking when moving
- **Breathing**: During watching behavior
- **Vanish sound**: When entity disappears
- **Ambient spooky**: Background atmospheric sounds
- **Chasing music**: When actively pursuing

### Visual Effects
- **Smoke particles**: Environmental manipulation
- **Dark particles**: Darkness effects and peripheral vision
- **Entity invisibility**: During vanishing state
- **Break particles**: Block manipulation effects

### Behavioral Patterns
- **Distance-based decisions**: Entity behavior changes based on how far you are
- **Line of sight**: Different behavior when you can/can't see the entity
- **Cooldown systems**: Effects don't spam, have proper timing
- **Progressive paranoia**: Effects get worse the longer you're stalked

## Debug Information

### Debug Logs (Every 5 seconds when debug is on)
- Current behavior state
- Distance to player
- Whether player can see entity
- All timer values
- Cooldown states
- Position information

### Key Debug Messages to Watch For
- Behavior state changes with reasons
- Effect triggers with parameters
- Timer resets and cooldowns
- Pathfinding decisions
- Sound effect triggers

## Testing Scenarios

### Scenario 1: Natural Stalking
1. Spawn Pin Head with `/pinhead_test spawn`
2. Walk around normally
3. Occasionally look behind you
4. Watch debug logs for natural behavior transitions

### Scenario 2: Forced Behavior Testing
1. Force each behavior state one by one
2. Observe the specific effects and sounds
3. Check debug logs for proper state management

### Scenario 3: Long-term Stalking
1. Let Pin Head stalk you for 30+ seconds without looking
2. Watch for paranoid effects to kick in
3. Check progressive horror escalation

### Scenario 4: Line of Sight Testing
1. Hide behind blocks and peek out
2. Watch how entity reacts to being spotted
3. Test the 30%/20%/20%/30% behavior choice system

## Troubleshooting

### No Entity Spawning
- Make sure you're in survival/creative mode
- Check that test mode is enabled
- Try spawning in different biomes

### No Sound Effects
- Check your sound settings
- Make sure hostile mob sounds are enabled
- Test with `/testjumpscare` to verify sound system works

### No Debug Logs
- Make sure debug mode is enabled with `/pinhead_test debug on`
- Check the console/log file for messages
- Verify you have operator permissions

### Effects Not Working
- Ensure test mode is enabled
- Check that you're close enough to the entity
- Reset timers with `/pinhead_test reset` if needed

## Expected Behavior Summary

The mod should create a **gradual horror experience**:
1. **Rare encounters** (low spawn rate)
2. **Subtle stalking** with distant audio cues
3. **Unpredictable responses** when spotted
4. **Environmental storytelling** through effects
5. **Progressive paranoia** that builds over time
6. **Atmospheric tension** rather than constant action

The entity should feel **intelligent and mysterious** rather than just aggressive.
