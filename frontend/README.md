# TFT Team Builder — frontend

Angular 22 app. Players enter their current board (champions + items), and it
ranks the meta comps closest to what they already have. No accounts. Set 18 /
patch 18 theming.

## Run it

```bash
cd frontend
npm install
npm start          # http://localhost:4200 — proxies /api and /img to :8080
```

The dev proxy (`proxy.conf.json`) forwards:
- `/api/**` → `http://localhost:8080` (strips `/api`, so `/api/unit/all` hits `/unit/all`)
- `/img/**` → `http://localhost:8080` (the Spring Boot static champion/item/trait art)

So start the Spring Boot backend on :8080 first. Without it the app still loads
and shows empty states.

## Build

```bash
npm run build      # outputs to dist/frontend
```

## What the API needs

The ranking feature needs comp endpoints that don't exist yet, and a couple of
data fixes. See **API_REQUIREMENTS.md** for the full list with example payloads.

## Structure

```
src/app/
  core/
    models.ts          # TS interfaces mirroring the JPA entities
    api.service.ts      # all HTTP calls, normalises backend shapes
    match-engine.ts     # client-side comp similarity scoring
    board-store.ts      # the player's current board (signals, no accounts)
  features/
    builder/            # pick champions + items (home page)
    results/            # ranked comp matches + comp detail
  shared/
    portrait.ts         # champion portrait w/ cost ring and stars
    item-icon.ts        # item icon
```

## Notes

- **Images**: image URLs come directly from the `imageLink` values returned by
  the API. The backend must populate `image_link` for units, items, and traits.
- **Matching** runs client-side and is transparent — the UI shows *why* each comp
  matched. Scoring lives in `match-engine.ts`. The player's items are a **flat
  pool** (not pinned to champions); item score is how many of a comp's recommended
  items the player already holds, compared as a multiset.
- **Shareable boards**: the board is encoded in `?board=` on the results URL
  (format `unitId.stars,...|itemId,...`), so a link reproduces the exact board.
