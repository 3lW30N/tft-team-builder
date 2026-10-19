# API requirements & suggestions — TFT Team Builder frontend

The Angular frontend is built against your existing Spring Boot API and degrades
gracefully where endpoints are missing (empty states, not crashes). To make it
**fully** functional, add the items below. Ordered by impact.

---

## 1. Comp endpoints — REQUIRED (the app's whole purpose)

There is no `CompController`. The ranking feature cannot work without it. The
frontend calls:

### `GET /comp/all`
Return every meta comp with its builds nested. Expected JSON:

```json
[
  {
    "id": 1,
    "name": "Lunar Aphelios Nidalee18_AP",
    "avgPlacement": 4.3157,
    "winRate": 58.58,
    "difficulty": 0.016,
    "levelling": "Fast 8",
    "builds": [
      {
        "unitId": "Aphelios",
        "stars": 3,
        "avgPlacement": 3.9507,
        "item1": { "id": "Deathblade", "name": "Deathblade", "imageLink": null },
        "item2": { "id": "GuinsoosRageblade", "name": "Guinsoo's Rageblade", "imageLink": null },
        "item3": { "id": "KrakensFury", "name": "Kraken's Fury", "imageLink": null }
      },
      { "unitId": "Diana", "item1": null, "item2": null, "item3": null }
    ]
  }
]
```

### `GET /comp/{id}`
Same shape, single comp. Used by the comp-detail page (also falls back to the
cached list, so it's optional but nice for deep-links).

**Backend changes this implies:**
- `Build` entity must expose `unitId` (or the nested `Unit`). Right now `Build`
  has `item1/2/3` but **no `unit` relation**, even though the `build` table has a
  `unit_id` FK column. Add it:
  ```java
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "unit_id")
  private Unit unit;
  ```
- `Build` has no `comp_id` relation either — add the `@ManyToOne Comp comp` so
  comps can nest their builds, or expose a `List<Build>` on `Comp` with
  `@OneToMany(mappedBy = "comp")`.
- Watch out for lazy-loading / `LazyInitializationException` when serializing.
  Return a DTO (you already have `ItemDto`; make a `CompDto` + `BuildDto`) rather
  than the entity graph, or use `@EntityGraph` to fetch builds + items eagerly.

---

## 2. `unit_trait` table is EMPTY — data gap

`02-data.sql` inserts units and traits but **never inserts into `unit_trait`**.
So `GET /traits/{unitId}` returns `[]` for every unit. Consequences:

- Trait synergy contributes **nothing** to comp ranking (the engine still works
  on unit + item overlap, so matches are reasonable, just not trait-aware).
- The comp-detail "traits" row is empty.

**Fix:** seed `unit_trait`. Each Set 18 champion has 2-3 traits. Example:

```sql
insert into unit_trait (unit_id, trait_id) values
  ('Aphelios', 'Lunar'), ('Aphelios', 'Rapidfire'),
  ('Diana', 'Lunar'), ('Diana', 'Executioner')
on conflict do nothing;
```

---

## 3. Bulk traits endpoint — RECOMMENDED (performance)

To rank comps the frontend needs every unit's traits. Today it must either call
`GET /traits/{unitId}` **once per unit** (~65 requests) or hard-code them. Add:

### `GET /traits/all`
```json
[
  { "unitId": "Aphelios", "traitIds": ["Lunar", "Rapidfire"] },
  { "unitId": "Diana",    "traitIds": ["Lunar", "Executioner"] }
]
```
The frontend already prefers this endpoint and falls back to the per-unit fan-out
if it 404s — so adding it is a drop-in speed-up.

---

## 4. Populate `image_link` — RECOMMENDED (removes client-side guessing)

The frontend uses the `imageLink` value returned by the API directly, so
`image_link` must be populated for every unit, item, and trait. The previous
client-side filename rules are listed below only to document the old behavior:

| Entity | Rule the client uses |
|--------|----------------------|
| trait  | `/img/trait/Trait_Icon_18_{id}.TFT_Set18.png` |
| item   | `/img/item/item/DA_{id}.png` |
| emblem | `/img/item/emblem/DA_18_Emblem{Trait}.png` (ids ending `Emblem`) |
| component | `/img/item/component/DA_Component_{id}.png` |
| unit   | lookup table (`champion-images.json`) — **unit filenames are irregular** |

Units are the fragile case: ids like `Nidalee18AP` map to files like
`TFT18_Nidalee_splash_centered_0.TFT_Set18.png` with no clean rule, so the
frontend ships a generated `public/data/champion-images.json`. The backend should **store the real `image_link` on each row** and return it.

The `unit` entit@y also doesn't expose the `tier` column that exists in the schema
— expose it to show champion cost colors on portraits.

---

## 5. Server-side match endpoint — OPTIONAL (future)

Matching currently runs client-side (transparent, no server load, works today).
If you later want it server-side (e.g. to weight by live win-rate data), add:

### `POST /comp/match`
Request: the player's board. Units carry a star level; items are a **flat pool**,
not pinned to any champion (duplicates allowed).
```json
{
  "units": [ { "unitId": "Aphelios", "stars": 2 }, { "unitId": "Diana", "stars": 1 } ],
  "itemIds": ["Deathblade", "GuinsoosRageblade", "KrakensFury"]
}
```
Response: ranked comps with a score 0-100 and the matched/missing unit ids. The
frontend's `MatchEngine` documents the exact scoring if you want to mirror it
(unit overlap 60%, trait overlap 25%, item-pool overlap 15%, multiset-compared).

---

## 6. CORS — minor

`ItemController` has `@CrossOrigin`; the others don't. In dev the Angular proxy
(`proxy.conf.json`) makes this moot. For a deployed frontend on a different
origin, add a global CORS config instead of per-controller annotations:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
  @Override public void addCorsMappings(CorsRegistry r) {
    r.addMapping("/**").allowedOrigins("https://your-frontend-domain");
  }
}
```

---

## 7. Minor backend bugs noticed (not frontend-blocking)

- `src/main/resources/database/Dockerfile` copies `02-data.sql`, but the files are
  `01-schema.sql`, `02-data.sql`, `03-recipes.sql`. The DB container won't seed.
  Copy the whole `database/` dir (Postgres runs `*.sql` in `/docker-entrypoint-initdb.d`
  alphabetically — the numeric prefixes already order them correctly).
- `02-data.sql` has duplicate `insert into item ... ProtectorsVow` lines; harmless
  with `on conflict do nothing` but noisy.
- `UnitController` imports `AbstractPersistable_.id` (unused) — dead import.
