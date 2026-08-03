# Create Tweaks

A Fabric mod that rebalances the Create mod for long-running multiplayer servers.

If your server has reached the point where one player's contraption is eating the terrain, ore is worthless
because everyone doubles it, and a Deployer is quietly running a mob farm nobody has visited in a month, this
fixes those problems and leaves the rest of Create alone.

Create stays a processing, logistics and rail mod. It stops being an automation mod.

## The rule this is built on

Nothing self-perpetuates. A machine has to be started and fed by a player.

The test for any block: if a player builds this, walks away, and comes back in a week, is there more stuff
than when they left? If yes, it changes.

Three things are treated as untouchable and all work exactly as they do in stock Create:

1. Trains, monorails, stations, signals and schedules.
2. Basic contraptions. Elevators, doors, drawbridges, windmills, rotating builds.
3. Quality of life. Toolbox, goggles, wrench, belts, funnels, vaults, decorative blocks.

## Requires

Minecraft 1.20.1, Fabric, Create 6.0.x, Fabric API. Java 17.

This is the only version the Create Fabric port supports in any meaningful sense. Create Fabric ships for
1.18.2, 1.19.2 and 1.20.1 only, and 1.20.1 is 86% of its downloads and the only line still receiving
releases. There has never been a 1.21.1 Fabric release.

## What it changes

Everything below is individually toggleable in `config/createtweaks.json`.

### Actors are stationary only

The Mechanical Drill, Mechanical Saw, Mechanical Harvester and Deployer are added to `create:non_movable`.
A contraption containing one refuses to assemble and the player gets a message naming the block and its
coordinates.

This ends contraption quarries, world-eater drills, mobile tree farms, mobile crop farms and every mobile
deployer farm. All four keep their normal stationary behaviour.

Left movable on purpose: the Mechanical Roller places blocks rather than removing them and is how you build
roadbed and rail embankments. The Mechanical Plough only tills and pushes entities. The Portable Storage and
Fluid Interfaces are how trains load freight, and with the actors above blocked they can only move cargo a
player put there.

### The Deployer cannot attack

A Deployer with a sword and a fan is a mob farm that runs forever with nobody present. It now deals no damage
to living entities.

Everything else about the Deployer is untouched: item application, sequenced assembly, block placing, right
clicking, and its role in making Train Track and Precision Mechanism. The Deployer keeps its recipe because
removing it would remove trains.

### No ore doubling

Crushing recipes that turn an ore block into more product than mining it by hand are removed.

The match is semantic, not by recipe id:

```
type is create:crushing
AND the single ingredient is ore-like
      the item is a known ore, or the tag ends _ores and does not start raw_
AND the expected count of the primary product is greater than 1
```

This covers ores from mods that are not installed yet, and does not care if Create renames a recipe file.
Validated against Create 6.0.8.1: 201 crushing recipes, 35 matched, no false positives.

Everything 1:1 survives, which is most of what Crushing Wheels do. Raw materials, raw blocks (nine crushed
from a block of nine is not a bonus), the Create `-site` stones, all recycling recipes, gravel, netherrack,
obsidian, blaze rods, amethyst and wool.

Zinc still works. Only the zinc *ore* recipes go; raw zinc still crushes 1:1 and both zinc furnace recipes
are untouched, so brass and trains are reachable from scratch.

### No auto-crafting brain

The Factory Gauge and Redstone Requester lose their recipes. The Packager, Re-packager, Stock Link, Stock
Ticker and Package Frogport all still work, so a player can walk up and request items from a warehouse by
hand.

The Gauge is what schedules crafting with no player present and the Requester is what fires it off a redstone
pulse. Those two are the automation, the rest is logistics.

### Mechanical Arm and Mechanical Harvester removed

The Arm routes items between machines with no player involved.

The Harvester only functions as a contraption actor. Its block entity is not kinetic, it takes no rotation
and it does nothing at all placed on its own, so once it is blocked from contraptions there is no working use
left and leaving it craftable would only be a trap.

Both are also hidden from the creative menu, along with the Gauge and Requester.

### No infinite lava

A Hose Pulley over a Nether lava lake is unlimited free fuel. Only water drains forever now, so boilers and
therefore trains keep working.

## Not covered

The stationary Drill still makes cobblestone. Create ships a dedicated optimisation for cobble generators.
It is localized and does not consume terrain.

The Encased Fan still does bulk washing, smoking and blasting, and still kills mobs pushed into it.

Schematic and Effortless Building limits are config values in those mods, not something a Create addon should
reach into. If megabuild imports are a problem on your server, cap `maxTotalSchematicSize` in
`create-server.toml`; the stock value of 100000 accepts roughly 100 MB uploads.

## Config

`config/createtweaks.json`, created on first run.

```json
{
  "removeOreDoubling": true,
  "removeUncraftableRecipes": true,
  "hideUncraftableItems": true,
  "deployerCannotAttack": true,
  "extraOres": []
}
```

`extraOres` adds item ids to the ore rule for ore blocks that are in no conventional ore tag.
`minecraft:gilded_blackstone` is already handled.

## Building

See [mod/BUILDING.md](mod/BUILDING.md). The short version is that Gradle must run on JDK 17 or 21, not 25.

## Previous versions

Create Tweaks 1.0 was a datapack. It did most of this, but matched recipes by file path, which silently
breaks if Create renames one, and it could not touch the Deployer at all. It is still available on the
[v1.0 release](https://github.com/FugginBeenus/create-tweaks/releases/tag/v1.0) and its sources are in git
history.

## License

MIT.
