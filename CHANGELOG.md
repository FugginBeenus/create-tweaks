# Changelog

## 2.0

Rewritten as a Fabric mod. Requires Create 6.0.x on Minecraft 1.20.1.

- **The Deployer can no longer attack living entities.** This closes the stationary mob farm, which the
  datapack could not touch at all. Item application, sequenced assembly and every other Deployer use are
  unaffected, and it keeps its recipe because Train Track and Precision Mechanism need it.
- **Ore doubling is matched semantically instead of by recipe id.** Any `create:crushing` recipe whose
  ingredient is ore-like and whose primary product exceeds 1 is removed. This covers ores from mods that are
  not installed and does not break when Create renames a recipe file. Against Create 6.0.8.1 it matches 35
  recipes where the datapack's path matching reached 30.
- **Uncraftable items are hidden from the creative menu.**
- **Everything is toggleable** in `config/createtweaks.json`.

Unchanged in behaviour from 1.0: the `create:non_movable` additions, the Factory Gauge and Redstone Requester
removals, the Mechanical Arm and Mechanical Harvester removals, and water-only bottomless fluids.

Dropped: the schematic and Effortless Building config caps. Those are settings in other mods and are
documented in the README rather than reached into.

## 1.0

Datapack. Verified against Create 6.0.8.1 on Fabric 1.20.1.

- Mechanical Drill, Mechanical Saw, Mechanical Harvester and Deployer added to `create:non_movable`.
- Crafting recipes removed for Factory Gauge, Redstone Requester, Mechanical Arm and Mechanical Harvester.
- 30 ore crushing recipes removed, matched by file path.
- Hose Pulley infinite drain restricted to water.
- Config caps documented for `create-server.toml` and `effortless.toml`.
