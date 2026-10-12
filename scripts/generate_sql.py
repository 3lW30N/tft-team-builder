import json

def q(v):
    if v is None:
        return "NULL"
    return "'" + str(v).replace("'", "''") + "'"

with open("comps_data.json", encoding="utf-8") as f:
    comps = json.load(f)["data"]["comps"]

out = []

def item(name):
    out.append(f"insert into item (id, name) values ({q(name)}, {q(name)}) on conflict do nothing;")

def unit(name):
    out.append(f"insert into unit (id, name) values ({q(name)}, {q(name)}) on conflict do nothing;")

for comp in comps:
    for t in comp["traits"]:
        n = t["name"]
        out.append(f"insert into trait (id, name) values ({q(n)}, {q(n)}) on conflict do nothing;")

    for b in comp["builds"]:
        for i in b["items"]:
            item(i)
    for t in comp["top_items"]:
        item(t["item"])

    out.append(
        "insert into comp (name, avg_placement, win_rate, difficulty, levelling) "
        f"values ({q(comp['name'])}, {comp['avg_placement']}, {comp['win_rate']}, "
        f"{comp['difficulty']}, {q(comp['levelling'])}) on conflict (name) do nothing;"
    )
    comp_id = f"(select id from comp where name = {q(comp['name'])})"

    units_with_build = set()
    for b in comp["builds"]:
        unit(b["unit"])
        units_with_build.add(b["unit"])
        items = sorted(b["items"])          # obligatoire : chk_item_item_order
        items += [None] * (3 - len(items))
        out.append(
            "insert into build (comp_id, avg_placement, unit_id, item1_id, item2_id, item3_id) "
            f"values ({comp_id}, {b['avg_placement']}, {q(b['unit'])}, "
            f"{q(items[0])}, {q(items[1])}, {q(items[2])}) on conflict do nothing;"
        )

    for u in comp["units"]:
        if u in units_with_build:
            continue
        unit(u)
        out.append(
            f"insert into build (comp_id, unit_id) values ({comp_id}, {q(u)}) on conflict do nothing;"
        )

with open("02-data.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(out))