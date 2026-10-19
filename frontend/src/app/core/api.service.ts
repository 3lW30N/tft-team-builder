import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, of, catchError, shareReplay, forkJoin, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Unit, Item, Trait, Comp, Build } from './models';

/** Raw shapes the Spring Boot API currently returns (loose — tolerant of nulls). */
interface RawItem { id: string; name: string; imageLink?: string; }
interface RawTrait { id: string; name: string; imageLink?: string; }
interface RawUnit { id: string; name: string; tier?: number; imageLink?: string; }
interface RawBuild {
  id?: number;
  avgPlacement?: number;
  stars?: number;
  unitId?: string;
  unit?: RawUnit;
  item1?: RawItem | null;
  item2?: RawItem | null;
  item3?: RawItem | null;
}
interface RawComp {
  id: number;
  name: string;
  avgPlacement?: number;
  winRate?: number;
  difficulty?: number;
  levelling?: string;
  builds?: RawBuild[];
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private base = environment.apiBase;

  // ---- Existing endpoints -------------------------------------------------

  private units$?: Observable<Unit[]>;
  private items$?: Observable<Item[]>;
  private traits$?: Observable<Trait[]>;

  /** GET /unit/all — cached (static reference data). */
  getUnits(): Observable<Unit[]> {
    return (this.units$ ??= this.http
      .get<RawUnit[]>(`${this.base}/unit/all`)
      .pipe(map((u) => u ?? []), shareReplay(1)));
  }

  /** GET /item/all — cached. */
  getItems(): Observable<Item[]> {
    return (this.items$ ??= this.http
      .get<RawItem[]>(`${this.base}/item/all`)
      .pipe(map((i) => i ?? []), shareReplay(1)));
  }

  /** GET /trait/all — cached. */
  getTraits(): Observable<Trait[]> {
    return (this.traits$ ??= this.http
      .get<RawTrait[]>(`${this.base}/trait/all`)
      .pipe(map((t) => t ?? []), shareReplay(1)));
  }

  /** GET /traits/{unitId} — traits of one unit. */
  getTraitsOfUnit(unitId: string): Observable<Trait[]> {
    return this.http
      .get<RawTrait[]>(`${this.base}/traits/${encodeURIComponent(unitId)}`)
      .pipe(map((t) => t ?? []), catchError(() => of([])));
  }

  /** GET /traits/units/{traitId} — units that carry a trait. */
  getUnitsOfTrait(traitId: string): Observable<Unit[]> {
    return this.http
      .get<RawUnit[]>(`${this.base}/traits/units/${encodeURIComponent(traitId)}`)
      .pipe(map((u) => u ?? []), catchError(() => of([])));
  }

  // ---- Needed endpoints (see API report) ----------------------------------
  // These don't exist yet. The calls are written to the agreed contract and
  // fall back to [] so the UI degrades gracefully until the API ships them.

  private comps$?: Observable<Comp[]>;

  /**
   * GET /comp/all — every meta comp with its builds nested.
   * REQUIRED: there is no CompController yet. Expected JSON per comp:
   *   { id, name, avgPlacement, winRate, difficulty, levelling,
   *     builds: [{ unitId, stars, avgPlacement, item1, item2, item3 }] }
   */
  getComps(): Observable<Comp[]> {
    return (this.comps$ ??= this.http
      .get<RawComp[]>(`${this.base}/comp/all`)
      .pipe(
        map((comps) => (comps ?? []).map((c) => this.normalizeComp(c))),
        catchError(() => of([])),
        shareReplay(1),
      ));
  }

  /** GET /comp/{id} — one comp with builds. Falls back to the cached list. */
  getComp(id: number): Observable<Comp | undefined> {
    return this.http.get<RawComp>(`${this.base}/comp/${id}`).pipe(
      map((c) => (c ? this.normalizeComp(c) : undefined)),
      catchError(() => of(undefined)),
    );
  }

  /**
   * Returns a map of unitId -> trait ids, needed by the ranking engine.
   *
   * Preferred: a bulk endpoint GET /traits/all returning
   *   [{ unitId, traitIds: [...] }]  (see API report — avoids 1 request per unit).
   * Fallback: fan out /traits/{unitId} across all units (works today, chatty).
   */
  private traitsByUnit$?: Observable<Map<string, string[]>>;

  getTraitsByUnit(): Observable<Map<string, string[]>> {
    return (this.traitsByUnit$ ??= this.http
      .get<{ unitId: string; traitIds: string[] }[]>(`${this.base}/traits/all`)
      .pipe(
        map((rows) => {
          const m = new Map<string, string[]>();
          for (const r of rows ?? []) m.set(r.unitId, r.traitIds ?? []);
          return m;
        }),
        catchError(() => this.fanOutTraits()),
        shareReplay(1),
      ));
  }

  /** Fallback: one /traits/{unitId} call per unit, merged into a map. */
  private fanOutTraits(): Observable<Map<string, string[]>> {
    return this.getUnits().pipe(
      switchMap((units) => {
        const ids = units.map((u) => u.id);
        if (!ids.length) return of(new Map<string, string[]>());
        return forkJoin(
          ids.map((id) =>
            this.getTraitsOfUnit(id).pipe(map((traits) => [id, traits.map((t) => t.id)] as const)),
          ),
        ).pipe(map((pairs) => new Map<string, string[]>(pairs)));
      }),
    );
  }

  private normalizeComp(c: RawComp): Comp {
    return {
      id: c.id,
      name: c.name,
      avgPlacement: c.avgPlacement ?? null,
      winRate: c.winRate ?? null,
      difficulty: c.difficulty ?? null,
      levelling: c.levelling ?? null,
      builds: (c.builds ?? []).map((b) => this.normalizeBuild(b)),
    };
  }

  private normalizeBuild(b: RawBuild): Build {
    const items = [b.item1, b.item2, b.item3]
      .filter((x): x is RawItem => !!x)
      .map((x) => ({ id: x.id, name: x.name, imageLink: x.imageLink ?? null }));
    return {
      id: b.id,
      unitId: b.unitId ?? b.unit?.id ?? '',
      unit: b.unit ? { id: b.unit.id, name: b.unit.name, imageLink: b.unit.imageLink ?? null } : null,
      items,
      stars: b.stars ?? null,
      avgPlacement: b.avgPlacement ?? null,
    };
  }
}
