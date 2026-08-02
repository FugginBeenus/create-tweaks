# Changelog

## 1.0

First release. Verified against Create 6.0.8.1 on Fabric 1.20.1.

- Mechanical Drill, Mechanical Saw, Mechanical Harvester, and Deployer added to `create:non_movable`, so contraptions containing them refuse to assemble.
- Crafting recipes removed for Factory Gauge, Redstone Requester, Mechanical Arm, and Mechanical Harvester, plus the two `_clear` variants.
- 30 ore crushing recipes removed, covering every top-level `*_ore.json` plus `gilded_blackstone`. The rest of the ore chain is one to one and is untouched.
- Hose Pulley infinite drain restricted to water.
- Config caps documented for `create-server.toml` and `effortless.toml`.

The Deployer keeps its recipe. Train Track and Precision Mechanism both come only from sequenced assembly with deploying steps, so removing it would remove trains.
