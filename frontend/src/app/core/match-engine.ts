import { Injectable } from '@angular/core';
import { Board, Comp, CompMatch } from './models';

/**
 * Ranks meta comps by how close they are to the player's current board.
 *
 * Runs entirely client-side so the tool works against the existing API today.
 * The scoring is intentionally transparent (the UI shows the reasons) rather
 * than a black box — see API report for the server-side /comp/match proposal.
 *
 * Score = weighted sum, normalised to 0..100:
 *   - unitOverlap (0.60): share of the comp's units the player already holds,
 *       with carries (units that hold items in the comp) weighted 2x.
 *   - traitOverlap (0.25): share of the comp's active traits the player shares.
 *   - itemMatch (0.15): of the items this comp wants (across all its carries),
 *       how many the player already holds in their item pool. The pool is a flat
 *       list — items are NOT pinned to units — compared as a multiset so holding
 *       two of the same item counts toward two of that item being needed.
 * A comp you can't realistically reach (you share <1 unit) is pushed down.
 */
@Injectable({ providedIn: 'root' })
export class MatchEngine {
  rank(board: Board, comps: Comp[], traitsByUnit: Map<string, string[]>): CompMatch[] {
    const myUnits = new Set(board.slots.map((s) => s.unit.id));
    const myItems = this.multiset(board.items.map((i) => i.id));
    const myTraits = this.traitsOf([...myUnits], traitsByUnit);

    const matches = comps.map((comp) => this.score(comp, myUnits, myItems, myTraits, traitsByUnit));
    return matches.sort((a, b) => b.score - a.score);
  }

  private score(
    comp: Comp,
    myUnits: Set<string>,
    myItems: Map<string, number>,
    myTraits: Map<string, number>,
    traitsByUnit: Map<string, string[]>,
  ): CompMatch {
    const compUnits = comp.builds.map((b) => b.unitId).filter(Boolean);
    const carrySet = new Set(comp.builds.filter((b) => b.items.length > 0).map((b) => b.unitId));

    // ---- unit overlap (carry-weighted) ----
    let haveWeight = 0;
    let totalWeight = 0;
    const matchedUnits: string[] = [];
    const missingUnits: string[] = [];
    for (const u of compUnits) {
      const w = carrySet.has(u) ? 2 : 1;
      totalWeight += w;
      if (myUnits.has(u)) { haveWeight += w; matchedUnits.push(u); }
      else { missingUnits.push(u); }
    }
    const unitOverlap = totalWeight ? haveWeight / totalWeight : 0;

    // ---- trait overlap ----
    const compTraits = this.traitsOf(compUnits, traitsByUnit);
    let sharedTraitPts = 0;
    let compTraitPts = 0;
    for (const [t, cnt] of compTraits) {
      compTraitPts += cnt;
      sharedTraitPts += Math.min(cnt, myTraits.get(t) ?? 0);
    }
    const traitOverlap = compTraitPts ? sharedTraitPts / compTraitPts : 0;

    // ---- item match: player's pool vs every item this comp wants (multiset) ----
    const wanted = this.multiset(
      comp.builds.flatMap((b) => b.items.filter(Boolean).map((i) => i!.id)),
    );
    const pool = new Map(myItems); // copy so we can "spend" items as we match them
    let itemHave = 0;
    let itemTotal = 0;
    for (const [id, need] of wanted) {
      itemTotal += need;
      const got = Math.min(need, pool.get(id) ?? 0);
      itemHave += got;
      if (got) pool.set(id, (pool.get(id) ?? 0) - got);
    }
    const itemMatch = itemTotal ? itemHave / itemTotal : 0;

    let score = 100 * (0.6 * unitOverlap + 0.25 * traitOverlap + 0.15 * itemMatch);
    // Comps you share nothing with aren't a "pivot" — don't surface them as near-matches.
    if (matchedUnits.length === 0) score = Math.min(score, 5);

    return {
      comp,
      score: Math.round(score),
      matchedUnits,
      missingUnits,
      reasons: this.reasons(matchedUnits, carrySet, compUnits, traitOverlap, itemHave, itemTotal),
    };
  }

  private reasons(
    matched: string[], carrySet: Set<string>, compUnits: string[],
    traitOverlap: number, itemHave: number, itemTotal: number,
  ): string[] {
    const out: string[] = [];
    const matchedCarries = matched.filter((u) => carrySet.has(u));
    if (matched.length) out.push(`You have ${matched.length}/${compUnits.length} of the comp's units`);
    if (matchedCarries.length) out.push(`Shares ${matchedCarries.length} carry${matchedCarries.length > 1 ? ' units' : ''}`);
    if (traitOverlap >= 0.5) out.push('Strong trait overlap');
    if (itemTotal && itemHave) out.push(`You hold ${itemHave}/${itemTotal} of its items`);
    return out;
  }

  /** Count trait occurrences across a set of units (how many units carry each trait). */
  private traitsOf(unitIds: string[], traitsByUnit: Map<string, string[]>): Map<string, number> {
    const m = new Map<string, number>();
    for (const u of unitIds) {
      for (const t of traitsByUnit.get(u) ?? []) m.set(t, (m.get(t) ?? 0) + 1);
    }
    return m;
  }

  /** id[] -> Map<id, count>. */
  private multiset(ids: string[]): Map<string, number> {
    const m = new Map<string, number>();
    for (const id of ids) m.set(id, (m.get(id) ?? 0) + 1);
    return m;
  }
}
