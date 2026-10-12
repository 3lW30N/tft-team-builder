import json
import os
import re
from getpass import getpass
from pathlib import Path

import psycopg2

from dotenv import load_dotenv

PROJECT_ROOT = Path(__file__).resolve().parents[1]
load_dotenv(PROJECT_ROOT / ".env")

# ---------- Configuration ----------
DB = dict(
    host=os.environ.get("PGHOST", "localhost"),
    port=int(os.environ.get("PGPORT", "5432")),
    dbname=os.environ.get("PGDATABASE"),
    user=os.environ.get("PGUSER"),
    password=os.environ.get("PGPASSWORD") or getpass("Mot de passe PostgreSQL : "),
)

STATIC = PROJECT_ROOT / "src/main/resources/static"
CHAMPION_JSON = STATIC / "img/champion/champion.json"
ITEM_JSON = STATIC / "img/item/item.json"

# (dossier, préfixe des fichiers, chemin enregistré en base, mot d'emblème)
ITEM_SOURCES = [
    (STATIC / "img/item/item", "DA_", "img/item/item", ""),
    (STATIC / "img/item/component", "DA_Component_", "img/item/component", ""),
    (STATIC / "img/item/emblem", "DA_18_", "img/item/emblem", "Emblem"),
]

def make_id(name):
    words = re.sub(r"[^A-Za-z0-9 ]", "", name).split()
    return "".join(w[0].upper() + w[1:] for w in words)

def norm(s):
    return re.sub(r"[^a-z0-9]", "", s.lower())

def humanize(s):
    s = re.sub(r"(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])", " ", s)
    return s.replace("_", " ")

def load_data(path):
    with open(path, encoding="utf-8") as f:
        return json.load(f)["data"].values()


conn = psycopg2.connect(**DB)
cur = conn.cursor()

try:
    # ---------- Unités ----------
    cur.execute("select id, name from unit")
    units = {norm(name): id_ for id_, name in cur.fetchall()}

    updated = created = 0
    for c in load_data(CHAMPION_JSON):
        img = "img/champion/" + c["image"]["full"]
        key = norm(c["name"])
        if key in units:
            cur.execute(
                "update unit set tier = %s, image_link = %s where id = %s",
                (c["tier"], img, units[key]),
            )
            updated += 1
        else:
            cur.execute(
                """insert into unit (id, name, tier, image_link)
                   values (%s, %s, %s, %s)
                   on conflict (id) do update
                       set tier = excluded.tier, image_link = excluded.image_link""",
                (make_id(c["name"]), c["name"], c["tier"], img),
            )
            units[key] = c["id"]
            created += 1
            print("unit créée :", c["name"])
    print(f"Unités : {updated} mises à jour, {created} créées\n")

    # ---------- Items, composants, emblèmes ----------
    ref = {norm(i["name"]): i["name"] for i in load_data(ITEM_JSON)}

    cur.execute("select id, name from item")
    items = {norm(name): id_ for id_, name in cur.fetchall()}

    for folder, prefix, url_dir, emblem in ITEM_SOURCES:
        updated = created = unmatched = 0
        for png in sorted(folder.glob("*.png")):
            if not png.stem.startswith(prefix):
                continue
            raw = png.stem[len(prefix):]          # "InfinityEdge" ou "EmblemLunar"
            link = f"{url_dir}/{png.name}"

            # Variantes testées, dans l'ordre
            variants = [raw]
            base = raw
            if emblem:
                base = raw[len(emblem):] if raw.startswith(emblem) else raw
                variants += [base + emblem, base]  # "LunarEmblem", "Lunar"

            db_key = next((norm(v) for v in variants if norm(v) in items), None)
            ref_variant = next((v for v in variants if norm(v) in ref), None)

            if db_key:                             # déjà en base : on ajoute l'image
                cur.execute(
                    "update item set image_link = %s where id = %s",
                    (link, items[db_key]),
                )
                updated += 1
            elif ref_variant:                      # connu du JSON : on crée
                key = norm(ref_variant)
                cur.execute(
                    """insert into item (id, name, image_link) values (%s, %s, %s)
                       on conflict do nothing""",
                    (ref_variant, ref[key], link),
                )
                items[key] = ref_variant
                created += 1
                print("item créé :", ref[key])
            elif emblem:
                name = f"{humanize(base)} Emblem"
                item_id = base + emblem
                cur.execute(
                    """insert into item (id, name, image_link) values (%s, %s, %s)
                       on conflict do nothing""",
                    (item_id, name, link),
                )
                items[norm(name)] = item_id
                created += 1
                print("item créé :", name)
            else:                                  # inconnu partout : on ne devine pas
                unmatched += 1
                print("PNG sans correspondance :", png.name)

        print(f"{url_dir} : {updated} mis à jour, {created} créés, "
              f"{unmatched} sans correspondance\n")

    conn.commit()
except Exception:
    conn.rollback()
    raise
finally:
    cur.close()
    conn.close()