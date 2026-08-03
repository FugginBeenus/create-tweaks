# Create Tweaks

A drop-in rebalance for the Create mod, aimed at long-running multiplayer servers where Create has quietly become the whole economy.

If your server has hit the point where one player's contraption is eating the terrain, ore is worthless because everyone is doubling it, and someone has printed a downloaded megabuild overnight, this fixes those four problems and leaves the rest of Create alone.

Create stays a processing, logistics, and rail mod. It stops being an automation mod.

## The rule this is built on

Nothing self-perpetuates. A machine has to be started and fed by a player.

The test for any block: if a player builds this, walks away, and comes back in a week, is there more stuff than when they left? If yes, it changes.

Three things were treated as untouchable and all still work exactly as they do in stock Create:

1. Trains, monorails, stations, signals, and schedules.
2. Basic contraptions. Elevators, doors, drawbridges, windmills, rotating builds.
3. Quality of life. Toolbox, goggles, wrench, belts, funnels, vaults, decorative blocks.

## Requirements

Built and verified against:

| | |
|---|---|
| Minecraft | 1.20.1 |
| Loader | Fabric |
| Create | 6.0.8.1 |
| Fabric API | 0.92.11 |

No other mods are required. Fabric API is needed for the resource conditions the recipe removals use, and Create already depends on it.

It should work on any Create 6.0.x. On Create 0.5.x the block and tag names are the same but several recipe paths are not, so check before assuming.

This is a Fabric pack. The two tag files and the config caps are loader neutral, but the 36 recipe removals use `fabric:load_conditions`, and the equivalent on Forge or NeoForge is spelled differently. Everything except the recipe removals works unchanged on other loaders. Porting them is mechanical, since all 36 files are byte-identical apart from their path.

## Install

There are two parts. The datapack is the bulk of it. The config caps are four lines you edit by hand, because a datapack cannot reach a `.toml` file.

### Part 1, the datapack

Drop `create-tweaks-1.0.zip` into either place. Do not unzip it.

**`world/datapacks/`** is the normal way and works on any server. It applies to that one world and is enabled automatically on the next load. Confirm with `/datapack list`.

**`config/paxi/datapacks/`** is worth it if you already run [Paxi](https://modrinth.com/mod/paxi). Paxi loads datapacks globally rather than per world, so the pack survives a world reset and applies to every world on the server. Paxi accepts the zip as-is.

Paxi loads silently, with no startup log line confirming success, so `/datapack list` in game is the only way to check either path. Note that this folder is inside `config/`, not the game directory root. A `datapacks` folder at the top level of a profile or server directory is not read by anything.

Either way, restart the server afterwards.

### Part 2, the config caps

Apply the values under "Megabuild caps" below to `config/create-server.toml` and, if you run Effortless Building, `config/effortless.toml`.

This part is optional and independent. The datapack works without it. Skipping it leaves schematic imports uncapped, which on most servers is the loudest complaint of the four problems this pack addresses.

### Clients

Clients do not need to install anything.

## What changes

### 1. Actors are stationary only

`data/create/tags/blocks/non_movable.json`

Adds four blocks to a tag Create already ships:

```
create:mechanical_drill
create:mechanical_saw
create:mechanical_harvester
create:deployer
```

Create refuses to assemble a contraption containing a block in `non_movable`. A player who glues a drill to a piston gets a message naming the block and its coordinates, so there is nothing to explain in a rules channel.

This is the main change and it does a lot of work in one file. It ends contraption quarries, world eater drills, mobile tree farms, mobile crop farms, and every mobile deployer farm. All four blocks keep their recipe, their crafting tree, and their normal stationary behaviour.

Deliberately left movable:

- `create:mechanical_roller` places blocks instead of removing them. It is a construction tool and it is how you build roadbed and rail embankments.
- `create:mechanical_plough` only tills soil and pushes entities. With the harvester gone it cannot form a farm on its own.
- `create:portable_storage_interface` and `create:portable_fluid_interface` are how trains load freight. With the actors above blocked, they can only move cargo a player put there.

To loosen: delete a line from the tag file. To turn the whole thing off, delete the file.

### 2. No auto-crafting brain

Removes the crafting recipes for `create:factory_gauge` and `create:redstone_requester`.

Still working: Packager, Re-packager, Stock Link, Stock Ticker, Package Frogport.

The reasoning is that the Stock Ticker lets a player walk up and request items from a warehouse, which is quality of life worth keeping. The Factory Gauge is what schedules crafting to a target stock level with no player present, and the Redstone Requester is what fires an order off a redstone pulse. Those two are the automation. The rest is logistics.

The Stock Ticker can still place a one-off craft order through JEI's recipe transfer button. That takes a player click per order. It has no timer, no repeat, and no way to trigger itself.

Also removes the two `_clear` recipes, which only reset a placed gauge or requester and would otherwise show in JEI as recipes for items you cannot obtain.

To loosen: delete the six files under `data/create/recipes/crafting/logistics/`.

### 3. No ore doubling

Removes 30 crushing recipes. Every one of them turns an ore block into more of its product than mining that ore by hand would give you.

| | Crushed | Mined by hand |
|---|---|---|
| Iron ore | 1.75 | 1 |
| Deepslate iron ore | 2.25 | 1 |
| Copper ore | 5.25 | 2 to 5 |
| Lapis ore | 10.5 | 4 to 9 |
| Nether gold ore | 18 nuggets | 2 to 6 nuggets |

This is more surgical than it sounds, because the multiplication lives entirely in the ore recipes and nowhere else in the chain. Everything downstream is one to one and is untouched:

- `crushing/raw_iron.json` and every other raw metal: 1 raw to 1 crushed.
- `crushing/raw_iron_block.json` and friends: 9 crushed from a block of 9. Not a bonus.
- `blasting/iron_ingot_from_crushed.json`: 1 crushed to 1 ingot.
- `splashing/crushed_raw_iron.json`: 1 ingot worth of nuggets, plus redstone as a side product.

These recipes are removed rather than rebalanced to one to one, so an ore block put into Crushing Wheels now does nothing. That costs nothing in practice: ore blocks still smelt to one ingot in a furnace, and without Silk Touch you get raw ore rather than the block anyway. The normal Create route is unchanged, mine the ore, crush the raw at one to one or smelt it directly.

Crushing Wheels and the Millstone keep working for everything else, which is most of what they do: gravel, netherrack, obsidian, blaze rods, amethyst, wool, the Create stone types, and all the recycling recipes.

Zinc still works. Zinc is Create's own ore and the whole brass and train progression sits on it, so it was checked specifically. Only `crushing/zinc_ore.json` and `crushing/deepslate_zinc_ore.json` are removed. `blasting/zinc_ingot_from_ore.json` and `blasting/zinc_ingot_from_raw_ore.json` are plain one to one furnace recipes and both survive, and Asurine still crushes to zinc at one to one.

`gilded_blackstone` is in the removal list even though it is not named an ore. It yields 18 gold nuggets, the same as nether gold ore, and leaving it would just make it the obvious workaround.

To loosen: delete the file for that ore from `data/create/recipes/crushing/`.

### 4. Megabuild caps

Two config files, edited by hand. These are not part of the datapack because overwriting another server's config would clobber unrelated settings.

`config/create-server.toml`:

```toml
[schematics]
  maxSchematics = 3                        # was 10
  maxTotalSchematicSize = 24               # was 100000, in KB
[schematics.schematicannon]
  schematicannonDelay = 20                 # was 10
  schematicannonShotsPerGunpowder = 100    # was 400
```

Create's own default for `maxTotalSchematicSize` is 256. A value of 100000 accepts schematic uploads of roughly 100 MB, about 390 times stock, and that single line is why downloaded megabuilds work. At 24 KB a player's house, station, bridge, or facade still uploads without trouble.

`config/effortless.toml`:

```toml
[global]
  maxReachDistance = 32                # was 128
  maxBlockPlaceVolume = 1024           # was 10000
  maxBlockBreakVolume = 1024           # was 10000
  maxBlockInteractVolume = 1024        # was 10000
  maxStructureCopyPasteVolume = 512    # was 10000
```

If you run Effortless Building, capping the schematicannon alone accomplishes nothing. Effortless can copy and paste 10,000 blocks from 128 blocks away out of the box, which is a second import pipeline straight past the cap you just set. Mirror, array, and radial building all still work at these numbers.

If you have Axiom installed, confirm it stays disabled on the server. It is a full creative world editor.

To loosen: raise the numbers. They are ordinary config values with no dependencies.

### 5. No infinite lava

`data/create/tags/fluids/bottomless/allow.json`, shipped with `"replace": true` and water only.

A Hose Pulley over a large enough fluid body normally drains it forever. On a Nether lava lake that is unlimited free fuel. Water stays infinite so boilers, and therefore trains, keep working.

This relies on `bottomlessFluidMode` being `ALLOW_BY_TAG` in `create-server.toml`, which is Create's default.

To loosen: add `minecraft:lava` back to the values list, or delete the file.

### 6. Mechanical Arm and Mechanical Harvester removed

Removes the crafting recipes for `create:mechanical_arm` and `create:mechanical_harvester`.

The Mechanical Arm routes items between machines with no player involved, which is the definition of the thing this pack is trying to stop. Nothing else in Create or Steam 'n Rails needs it as an ingredient, so removing it costs nothing downstream.

The Mechanical Harvester only functions as a contraption actor. Its block entity is not kinetic, it takes no rotation, and it does nothing at all when placed on its own. Once change 1 blocks it from contraptions there is no working use left, so leaving it craftable would only be a trap. It is removed rather than left as a dead block.

This is the strictest part of the pack and the first thing to drop if you want a softer ruleset.

To loosen: delete `data/create/recipes/crafting/kinetics/mechanical_arm.json` and `mechanical_harvester.json`. If you restore the harvester, also remove its line from the `non_movable` tag or it will still do nothing.

## What this deliberately does not fix

Worth knowing before you install, so nothing is a surprise later.

**The stationary Deployer still farms.** A Deployer with a sword and a fan is a mob farm, and with the right item it does auto-breeding and bonemeal farms. It cannot be removed. Train Track comes only from `sequenced_assembly/track.json`, whose sequence is two deploying steps, and Precision Mechanism, which gates Train Controls, is five loops of three deploying steps. Removing the Deployer removes trains. No tag or config setting separates "deployer that crafts track" from "deployer that punches a zombie", so this one is left standing on purpose.

**The stationary Drill still makes cobblestone.** Create even ships a dedicated optimisation for cobble generators. It is localized and it does not consume terrain, so it was left alone.

**The Encased Fan still kills mobs** and still does bulk washing, smoking, and blasting.

**Ore crushing recipes for modded ores are only covered at the top level.** The 30 removed recipes include Create's built-in support for aluminum, lead, nickel, osmium, platinum, quicksilver, silver, tin, and uranium, which activate if a mod provides those ores. Create also ships around 70 more crushing recipes under `crushing/compat/` for Silent Gems, Thermal, Elementary Ores, Oh The Biomes We've Gone, Aether, and Druidcraft. Those are not covered. If you run one of those mods, copy any of the shipped override files to the matching path under `data/create/recipes/crushing/compat/` and it will work the same way.

## How the recipe removals work

Each removed recipe is replaced by a file at the same path containing a Fabric resource condition that can never be true:

```json
{
  "fabric:load_conditions": [
    {
      "condition": "fabric:all_mods_loaded",
      "values": ["create_tweaks_recipe_disabled"]
    }
  ],
  "type": "minecraft:crafting_shapeless",
  "category": "misc",
  "ingredients": [{ "item": "minecraft:structure_void" }],
  "result": { "item": "minecraft:structure_void" }
}
```

The datapack overrides the mod's file, then the condition fails and no recipe registers. The item vanishes from JEI rather than showing an empty or broken entry. Nothing is deleted from the registry, so no block breaks, no chunk fails to load, and uninstalling restores everything.

This is why there is no KubeJS script and no mod in this pack. It is one datapack folder and nine edited config lines.

## Uninstall

Delete the zip from wherever you put it and restart. Optionally put the config values back. Nothing persists in the world.

Blocks players already built stay where they are. A player holding a Mechanical Arm keeps it.

## Tuning it for your server

Every change is one file or one config line, and each section above says what to delete to undo it.

A softer ruleset: keep changes 1, 4, and 5, and drop 2, 3, and 6. That leaves you with no world eaters and no megabuild imports, and everything else stock.

A stricter one: also remove the crafting recipe for `create:mechanical_drill`, which ends stationary cobble generators and stationary mining. Copy any of the shipped override files to `data/create/recipes/crafting/kinetics/mechanical_drill.json`. Note that this does not affect trains, since nothing in the rail progression needs a drill.
