import json
import re


def make_id(name):
    words = re.sub(r"[^A-Za-z0-9 ]", "", name).split()
    return "".join(w[0].upper() + w[1:] for w in words)


def q(v):
    if v is None:
        return "NULL"
    if isinstance(v, (int, float)):
        return str(v)
    return "'" + str(v).replace("'", "''") + "'"


with open("comps_data.json", encoding="utf-8") as f:
    comps = json.load(f)["data"]["comps"]

out = []
seen = {}  # id -> nom, pour détecter deux noms qui donneraient le même id


def register(table, name):
    id_ = make_id(name)
    if seen.setdefault((table, id_), name) != name:
        raise ValueError(f"Collision d'id dans {table} : {name!r} et {seen[(table, id_)]!r} -> {id_}")
    out.append(
        f"insert into {table} (id, name) values ({q(id_)}, {q(name)}) on conflict do nothing;"
    )
    return id_


for comp in comps:
    for t in comp["traits"]:
        register("trait", t["name"])

    for b in comp["builds"]:
        for i in b["items"]:
            register("item", i)
    for t in comp["top_items"]:
        register("item", t["item"])

    out.append(
        "insert into comp (name, avg_placement, win_rate, difficulty, levelling) "
        f"values ({q(comp['name'])}, {comp['avg_placement']}, {comp['win_rate']}, "
        f"{comp['difficulty']}, {q(comp['levelling'])}) on conflict (name) do nothing;"
    )
    comp_id = f"(select id from comp where name = {q(comp['name'])})"

    units_with_build = set()
    for b in comp["builds"]:
        unit_id = register("unit", b["unit"])
        units_with_build.add(b["unit"])
        # tri sur les IDS (et plus sur les noms) : exigé par chk_item_item_order
        items = sorted(make_id(i) for i in b["items"])
        items += [None] * (3 - len(items))
        out.append(
            "insert into build (comp_id, avg_placement, unit_id, item1_id, item2_id, item3_id) "
            f"values ({comp_id}, {b['avg_placement']}, {q(unit_id)}, "
            f"{q(items[0])}, {q(items[1])}, {q(items[2])}) on conflict do nothing;"
        )

    for u in comp["units"]:
        if u in units_with_build:
            continue
        unit_id = register("unit", u)
        out.append(
            f"insert into build (comp_id, unit_id) values ({comp_id}, {q(unit_id)}) on conflict do nothing;"
        )

with open("../src/main/resources/database/02-data.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(out))