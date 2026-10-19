// Domain models — mirror the Spring Boot entities (fr.epf.tftteambuilder.models).

export interface Unit {
  id: string;
  name: string;
  tier?: number | null;      // schema has `tier`; entity doesn't expose it yet (see API report)
  imageLink?: string | null;
}

export interface Trait {
  id: string;
  name: string;
  imageLink?: string | null;
}

export interface Item {
  id: string;
  name: string;
  imageLink?: string | null;
  // Component ids are @JsonIgnore'd on the entity, so they won't arrive from /item/all.
  // Kept optional so the client recipe map can fill them if needed.
  component1Id?: string | null;
  component2Id?: string | null;
}

/**
 * A build is one unit slot inside a comp: the champion, its items, star level.
 * The backend `Build` entity currently nests `item1/2/3` as full Item objects but
 * does NOT expose the `unit_id` relation (see API report). We model the shape the
 * frontend needs; the service normalises whatever the API returns into this.
 */
export interface Build {
  id?: number;
  unitId: string;
  unit?: Unit | null;
  items: (Item | null)[];   // length 0..3
  stars?: number | null;
  avgPlacement?: number | null;
}

export interface Comp {
  id: number;
  name: string;
  avgPlacement?: number | null;
  winRate?: number | null;
  difficulty?: number | null;
  levelling?: string | null;
  builds: Build[];          // the units that make up the comp
}

// ---- User's current in-game board (no accounts, lives in memory / query param) ----

export interface BoardSlot {
  unit: Unit;
  stars: number;            // 1..3
}

export interface Board {
  slots: BoardSlot[];
  items: Item[];            // the player's item pool — a flat list, not pinned to units
}

// ---- Ranking result ----

export interface CompMatch {
  comp: Comp;
  score: number;            // 0..100 similarity
  matchedUnits: string[];   // unit ids the player already has
  missingUnits: string[];   // unit ids in the comp the player lacks
  reasons: string[];        // human-readable "why this matched"
}
