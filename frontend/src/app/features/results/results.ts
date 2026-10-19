import { Component, computed, effect, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { combineLatest } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { BoardStore } from '../../core/board-store';
import { MatchEngine } from '../../core/match-engine';
import { Comp, CompMatch, Item, Unit } from '../../core/models';
import { Portrait } from '../../shared/portrait';
import { ItemIcon } from '../../shared/item-icon';

@Component({
  selector: 'tft-results',
  standalone: true,
  imports: [RouterLink, DecimalPipe, Portrait, ItemIcon],
  templateUrl: './results.html',
  styleUrl: './results.scss',
})
export class Results {
  private api = inject(ApiService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private engine = inject(MatchEngine);
  store = inject(BoardStore);

  // Reference data + comps, loaded together.
  private data = toSignal(
    combineLatest({
      units: this.api.getUnits(),
      items: this.api.getItems(),
      comps: this.api.getComps(),
      traitsByUnit: this.api.getTraitsByUnit(),
    }),
    { initialValue: null },
  );

  loading = computed(() => this.data() === null);

  constructor() {
    // Restore the board from ?board= once reference data arrives (shareable links, refresh-safe).
    effect(() => {
      const d = this.data();
      if (!d || !this.store.isEmpty()) return;
      const encoded = this.route.snapshot.queryParamMap.get('board') ?? '';
      if (!encoded) return;
      const unitMap = new Map<string, Unit>(d.units.map((u) => [u.id, u]));
      const itemMap = new Map<string, Item>(d.items.map((i) => [i.id, i]));
      this.store.restore(encoded, unitMap, itemMap);
    });
  }

  matches = computed<CompMatch[]>(() => {
    const d = this.data();
    if (!d) return [];
    if (!this.store.hasUnits()) return [];
    return this.engine.rank(this.store.board(), d.comps, d.traitsByUnit);
  });

  topMatches = computed(() => this.matches().filter((m) => m.score > 0).slice(0, 20));
  noComps = computed(() => !this.loading() && (this.data()?.comps.length ?? 0) === 0);

  // Resolve unit objects for a comp's builds so portraits render names/images.
  unitFor = (id: string): Unit => {
    const d = this.data();
    return d?.units.find((u) => u.id === id) ?? { id, name: id };
  };

  scoreClass(score: number): string {
    if (score >= 70) return 'score-high';
    if (score >= 40) return 'score-mid';
    return 'score-low';
  }

  editBoard() {
    this.router.navigate(['/'], { queryParams: {} });
  }
}
