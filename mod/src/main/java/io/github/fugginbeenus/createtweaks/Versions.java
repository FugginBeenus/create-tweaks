package io.github.fugginbeenus.createtweaks;

import java.util.List;
import java.util.Set;

public final class Versions {
    private Versions() {}

    public static final String CRUSHING_RECIPE_TYPE = "create:crushing";

    public static final String DEPLOYER_FAKE_PLAYER =
            "com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer";

    public static final List<String> UNCRAFTABLE = List.of(
            "create:crafting/logistics/factory_gauge",
            "create:crafting/logistics/factory_gauge_clear",
            "create:crafting/logistics/redstone_requester",
            "create:crafting/logistics/redstone_requester_clear",
            "create:crafting/kinetics/mechanical_arm",
            "create:crafting/kinetics/mechanical_harvester");

    public static final List<String> HIDDEN_ITEMS = List.of(
            "create:factory_gauge",
            "create:redstone_requester",
            "create:mechanical_arm",
            "create:mechanical_harvester");

    public static final Set<String> ORE_ITEMS = Set.of(
            "minecraft:coal_ore", "minecraft:deepslate_coal_ore",
            "minecraft:copper_ore", "minecraft:deepslate_copper_ore",
            "minecraft:diamond_ore", "minecraft:deepslate_diamond_ore",
            "minecraft:emerald_ore", "minecraft:deepslate_emerald_ore",
            "minecraft:gold_ore", "minecraft:deepslate_gold_ore", "minecraft:nether_gold_ore",
            "minecraft:iron_ore", "minecraft:deepslate_iron_ore",
            "minecraft:lapis_ore", "minecraft:deepslate_lapis_ore",
            "minecraft:redstone_ore", "minecraft:deepslate_redstone_ore",
            "minecraft:nether_quartz_ore",
            "create:zinc_ore", "create:deepslate_zinc_ore");

    public static final Set<String> EXTRA_ORES = Set.of("minecraft:gilded_blackstone");
}
