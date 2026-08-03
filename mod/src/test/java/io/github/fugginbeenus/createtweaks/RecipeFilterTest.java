package io.github.fugginbeenus.createtweaks;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeFilterTest {

    private static boolean removes(String id, String json) {
        return RecipeFilter.shouldRemove(id, JsonParser.parseString(json));
    }

    private static String crushing(String ingredient, String results) {
        return "{\"type\":\"create:crushing\",\"ingredients\":[" + ingredient + "],\"results\":[" + results + "]}";
    }

    @Test
    void removesOreBlocksThatMultiply() {
        assertTrue(removes("create:crushing/iron_ore", crushing(
                "{\"item\":\"minecraft:iron_ore\"}",
                "{\"item\":\"create:crushed_raw_iron\"},{\"chance\":0.75,\"item\":\"create:crushed_raw_iron\"}")));

        assertTrue(removes("create:crushing/deepslate_iron_ore", crushing(
                "{\"item\":\"minecraft:deepslate_iron_ore\"}",
                "{\"count\":2,\"item\":\"create:crushed_raw_iron\"},{\"chance\":0.25,\"item\":\"create:crushed_raw_iron\"}")));

        assertTrue(removes("create:crushing/nether_gold_ore", crushing(
                "{\"item\":\"minecraft:nether_gold_ore\"}",
                "{\"count\":18,\"item\":\"minecraft:gold_nugget\"}")));
    }

    @Test
    void removesModdedOresThroughConventionalTags() {
        assertTrue(removes("create:crushing/lead_ore", crushing(
                "{\"tag\":\"c:lead_ores\"}",
                "{\"item\":\"create:crushed_raw_lead\"},{\"chance\":0.75,\"item\":\"create:crushed_raw_lead\"}")));
    }

    @Test
    void removesGildedBlackstoneWhichIsInNoOreTag() {
        assertTrue(removes("create:crushing/gilded_blackstone", crushing(
                "{\"item\":\"minecraft:gilded_blackstone\"}",
                "{\"count\":18,\"item\":\"minecraft:gold_nugget\"}")));
    }

    @Test
    void keepsRawMaterialsDespiteTheOresTagName() {
        assertFalse(removes("create:crushing/raw_zinc", crushing(
                "{\"tag\":\"c:raw_zinc_ores\"}",
                "{\"item\":\"create:crushed_raw_zinc\"}")));
    }

    @Test
    void keepsRawBlocksBecauseNineFromNineIsNotABonus() {
        assertFalse(removes("create:crushing/raw_iron_block", crushing(
                "{\"tag\":\"c:raw_iron_blocks\"}",
                "{\"count\":9,\"item\":\"create:crushed_raw_iron\"}")));
    }

    @Test
    void keepsSiteStonesAndRecycling() {
        assertFalse(removes("create:crushing/asurine", crushing(
                "{\"item\":\"create:asurine\"}",
                "{\"item\":\"create:crushed_raw_zinc\"}")));

        assertFalse(removes("create:crushing/tuff_recycling", crushing(
                "{\"tag\":\"create:stone_types/tuff\"}",
                "{\"item\":\"minecraft:flint\"}")));
    }

    @Test
    void ignoresOtherRecipeTypes() {
        assertFalse(removes("minecraft:iron_ingot", "{\"type\":\"minecraft:smelting\","
                + "\"ingredient\":{\"item\":\"minecraft:iron_ore\"},\"result\":\"minecraft:iron_ingot\"}"));
    }

    @Test
    void removesNamedRecipesById() {
        assertTrue(removes("create:crafting/logistics/factory_gauge",
                "{\"type\":\"minecraft:crafting_shapeless\"}"));

        assertFalse(removes("create:crafting/logistics/stock_ticker",
                "{\"type\":\"minecraft:crafting_shaped\"}"));
    }
}
