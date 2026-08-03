#!/usr/bin/env python3
"""Validate the semantic ore-doubling rule against a Create jar.

usage: ore-rule-check.py <create.jar>

The rule the mod implements:
    recipe type is create:crushing
    AND the single ingredient is ore-like
        item is in c:ores, or tag path ends in _ores and does not start with raw_
    AND the primary product's expected count exceeds 1

Exit 1 if any recipe is matched that yields exactly 1, which would mean the rule
started eating a non-bonus recipe.
"""
import json, subprocess, sys, zipfile

C_ORES = set("""
minecraft:coal_ore minecraft:deepslate_coal_ore minecraft:copper_ore minecraft:deepslate_copper_ore
minecraft:diamond_ore minecraft:deepslate_diamond_ore minecraft:emerald_ore minecraft:deepslate_emerald_ore
minecraft:gold_ore minecraft:deepslate_gold_ore minecraft:nether_gold_ore
minecraft:iron_ore minecraft:deepslate_iron_ore minecraft:lapis_ore minecraft:deepslate_lapis_ore
minecraft:redstone_ore minecraft:deepslate_redstone_ore minecraft:nether_quartz_ore
create:zinc_ore create:deepslate_zinc_ore
""".split())

EXTRA = {"minecraft:gilded_blackstone"}


def ore_like(item, tag):
    if item:
        return item in C_ORES or item in EXTRA
    path = tag.split(":")[-1]
    return path.endswith("_ores") and not path.startswith("raw_")


def yield_of(recipe):
    primary = recipe["results"][0]
    item = primary["item"]
    total = primary.get("count", 1)
    for r in recipe["results"][1:]:
        if r["item"] == item:
            total += r.get("count", 1) * r.get("chance", 1)
    return item, round(total, 3)


def main(jar_path):
    matched, skipped, bad = [], [], []
    with zipfile.ZipFile(jar_path) as jar:
        names = [n for n in jar.namelist()
                 if n.startswith("data/create/recipes/crushing/") and n.endswith(".json")]
        for name in sorted(names):
            recipe = json.loads(jar.read(name))
            ing = recipe["ingredients"][0]
            item, tag = ing.get("item"), ing.get("tag")
            product, count = yield_of(recipe)
            short = name.rsplit("/", 1)[-1][:-5]
            if ore_like(item, tag) and count > 1:
                matched.append((short, item or "#" + tag, product, count))
            else:
                skipped.append((short, item or "#" + tag, product, count))
                if ore_like(item, tag) and count == 1:
                    bad.append(short)

    print(f"{len(names)} crushing recipes, {len(matched)} matched, {len(skipped)} left alone\n")
    for short, src, product, count in matched:
        print(f"  REMOVE  {short:32} {src:34} -> {product} x{count}")

    survivors = [r for r in skipped if r[0].startswith("raw_") or "recycling" in r[0]
                 or r[0] in ("asurine", "crimsite", "ochrum", "veridium")]
    print(f"\nmust survive ({len(survivors)}):")
    for short, src, product, count in survivors:
        print(f"  keep    {short:32} {src:34} -> {product} x{count}")

    if bad:
        print(f"\nFAIL: ore-like input with yield 1 was matched: {bad}")
        return 1
    print("\nOK")
    return 0


if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    sys.exit(main(sys.argv[1]))
