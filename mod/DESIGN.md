# Create Tweaks, mod build

## Target

**MC 1.20.1, Fabric, Java 17. One target.**

"Every version the Create Fabric port works with" resolves to less than it sounds. The Modrinth API for
`create-fabric` returns exactly `["1.18.2","1.19.2","1.20.1"]`, last updated 2 Dec 2025. There has never been
a 1.21.1 Fabric release; the branch exists and has been dead since March 2025. Upstream skipped 1.21.2
through 1.21.11 entirely.

| MC | Create Fabric | Share of downloads | Decision |
|---|---|---|---|
| 1.20.1 | 6.0.8.1, active | 86% | ship |
| 1.19.2 | 0.5.1-i, frozen 2024 | 12% | exclude |
| 1.18.2 | 0.5.1-i, frozen 2024 | 1.5% | exclude |
| 1.21.1+ | never existed | 0% | nothing to target |

1.19.2 and 1.18.2 are pre-Create-6. The `com.simibubi.create.api` package barely existed before 6.0.0, six
files against 85. That is a separate branch, not a config flag, for a declining 12%.

## No version matrix

Stonecutter is the right tool for a multiversion Fabric mod and it is not needed here. Its entire value is
managing version-specific source, and this mod has none:

| Hook | Mechanism | Create classes referenced |
|---|---|---|
| Stationary-only actors | `create:non_movable` tag shipped in the jar | none |
| No infinite lava | `create:bottomless/allow` tag shipped in the jar | none |
| Ore doubling removed | vanilla `RecipeManager` mixin | none |
| Uncraftable items | vanilla `RecipeManager` mixin | none |
| Creative hiding | Fabric `ItemGroupEvents` | none |
| Deployer cannot attack | Fabric `ServerLivingEntityEvents` | `DeployerFakePlayer` only |

The one Create symbol, `com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer`, is at an identical
path in 0.5.1, 6.x Forge and 6.x Fabric.

Stress impact values were the only genuine cross-version divergence, keyed by namespace in 0.5.x and per
block in 6.x with no sane shared abstraction. They are not implemented, because the Mechanical Harvester is
not a kinetic block and cannot be given a stress cost at all. Dropping that hook is what removes the last
reason for a build matrix.

If Create Fabric ever ships another MC version, add Stonecutter then. Everything version-sensitive lives in
`Versions.java` so the diff is small.

## Recipe matching

Path matching is what the datapack does and why it is not ironclad. Rename a recipe file upstream and the
override silently becomes a no-op.

This matches semantically instead, on the recipe JSON, before parsing:

```
type == create:crushing
AND ingredient is ore-like
      item is in the vanilla ore set, or
      tag path ends _ores and does not start raw_
AND expected count of the primary product > 1
```

Validated by `research/ore-rule-check.py` against Create 6.0.8.1: 201 crushing recipes, 35 matched, 0 false
positives. The datapack covered 30. The extra 5 are `compat/` ore recipes for mods that are not installed,
which the datapack documented as an uncovered gap.

Both conditions are load bearing.

Dropping `> 1` matches 1:1 recipes that are not bonuses, including all 13 `raw_*_block` recipes, which give 9
crushed from a block of 9.

Dropping the `raw_` exclusion matches `c:raw_zinc_ores`, which is Fabric's conventional tag for raw zinc
*items*, not ore blocks. That would delete the 1:1 raw zinc crushing recipe the brass progression runs
through. Ten recipes break this way.

Matching on JSON rather than parsed recipes is deliberate. It needs no Create recipe classes, avoids the
tag-versus-recipe reload ordering question, and is the exact rule the harness already proves against real
data.

`minecraft:gilded_blackstone` is in no ore tag and yields 18 gold nuggets. It stays an explicit config entry
rather than something a general rule guesses at.

## What stays working

Trains. The Deployer keeps its recipe because `create:track` comes only from a sequenced assembly with two
deploying steps, and Precision Mechanism is five loops of three, which gates Train Controls.

Sequenced assembly, item application and every USE-mode deployer behaviour. Only entity damage is blocked.

Every 1:1 recipe downstream of crushing, the Create `-site` stones, all recycling recipes, and every raw
material path.
