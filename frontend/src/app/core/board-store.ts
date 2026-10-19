import { Injectable, computed, signal } from '@angular/core';
import { Board, BoardSlot, Item, Unit } from './models';

/** Holds the player's current in-game board. In-memory, no accounts. */
@Injectable({ providedIn: 'root' })
export class BoardStore {
  private readonly _slots = signal<BoardSlot[]>([]);
  private readonly _items = signal<Item[]>([]); // flat item pool, not pinned to units

  readonly slots = this._slots.asReadonly();
  readonly items = this._items.asReadonly();
  readonly board = computed<Board>(() => ({ slots: this._slots(), items: this._items() }));
  readonly isEmpty = computed(() => this._slots().length === 0 && this._items().length === 0);
  readonly hasUnits = computed(() => this._slots().length > 0);
  readonly unitIds = computed(() => new Set(this._slots().map((s) => s.unit.id)));

  // ---- units ----
  addUnit(unit: Unit): void {
    if (this.unitIds().has(unit.id)) return;
    this._slots.update((s) => [...s, { unit, stars: 1 }]);
  }

  removeUnit(unitId: string): void {
    this._slots.update((s) => s.filter((x) => x.unit.id !== unitId));
  }

  toggleUnit(unit: Unit): void {
    this.unitIds().has(unit.id) ? this.removeUnit(unit.id) : this.addUnit(unit);
  }

  setStars(unitId: string, stars: number): void {
    this._slots.update((s) => s.map((x) => (x.unit.id === unitId ? { ...x, stars } : x)));
  }

  // ---- item pool (duplicates allowed: you can hold two Deathblades) ----
  addItem(item: Item): void {
    this._items.update((items) => [...items, item]);
  }

  removeItemAt(index: number): void {
    this._items.update((items) => items.filter((_, i) => i !== index));
  }

  clear(): void {
    this._slots.set([]);
    this._items.set([]);
  }

  // ---- shareable serialization (URL ?board=) ----
  // Format: "unitId.stars,unitId.stars|itemId,itemId"  (units | items)
  serialize(): string {
    const units = this._slots().map((s) => `${s.unit.id}.${s.stars}`).join(',');
    const items = this._items().map((i) => i.id).join(',');
    return `${units}|${items}`;
  }

  restore(encoded: string, units: Map<string, Unit>, items: Map<string, Item>): void {
    if (!encoded) return;
    const [unitPart = '', itemPart = ''] = encoded.split('|');

    const slots: BoardSlot[] = [];
    for (const chunk of unitPart.split(',')) {
      if (!chunk) continue;
      const [uid, stars] = chunk.split('.');
      const unit = units.get(uid);
      if (unit) slots.push({ unit, stars: Number(stars) || 1 });
    }
    this._slots.set(slots);

    const pool = itemPart
      .split(',')
      .filter(Boolean)
      .map((id) => items.get(id))
      .filter((i): i is Item => !!i);
    this._items.set(pool);
  }
}
