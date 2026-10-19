import { Component, computed, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { combineLatest } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { BoardStore } from '../../core/board-store';
import { Comp, Trait, Unit } from '../../core/models';
import { Portrait } from '../../shared/portrait';
import { ItemIcon } from '../../shared/item-icon';

@Component({
  selector: 'tft-comp-detail',
  standalone: true,
  imports: [RouterLink, DecimalPipe, Portrait, ItemIcon],
  templateUrl: './comp-detail.html',
  styleUrl: './comp-detail.scss',
})
export class CompDetail {
  private api = inject(ApiService);
  store = inject(BoardStore);

  // route param `id` bound as component input (withComponentInputBinding is default in v22 router? -> we read via snapshot fallback)
  id = input<string>();

  private data = toSignal(
    combineLatest({
      comps: this.api.getComps(),
      units: this.api.getUnits(),
      traitsByUnit: this.api.getTraitsByUnit(),
      traits: this.api.getTraits(),
    }),
    { initialValue: null },
  );

  loading = computed(() => this.data() === null);

  comp = computed<Comp | undefined>(() => {
    const d = this.data();
    const idNum = Number(this.id());
    return d?.comps.find((c) => c.id === idNum);
  });

  unitFor = (id: string): Unit => this.data()?.units.find((u) => u.id === id) ?? { id, name: id };
  owned = (unitId: string) => this.store.unitIds().has(unitId);

  /** Active traits across the comp, counted and sorted by strength. */
  compTraits = computed<{ trait: Trait; count: number }[]>(() => {
    const d = this.data();
    const c = this.comp();
    if (!d || !c) return [];
    const counts = new Map<string, number>();
    for (const b of c.builds) {
      for (const t of d.traitsByUnit.get(b.unitId) ?? []) counts.set(t, (counts.get(t) ?? 0) + 1);
    }
    const traitMap = new Map(d.traits.map((t) => [t.id, t]));
    return [...counts.entries()]
      .map(([id, count]) => ({ trait: traitMap.get(id) ?? { id, name: id }, count }))
      .sort((a, b) => b.count - a.count);
  });

  traitSrc = (t: Trait) => t.imageLink ?? '';
}
