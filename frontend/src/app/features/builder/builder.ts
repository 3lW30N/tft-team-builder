import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { ApiService } from '../../core/api.service';
import { BoardStore } from '../../core/board-store';
import { Item, Unit } from '../../core/models';
import { Portrait } from '../../shared/portrait';
import { ItemIcon } from '../../shared/item-icon';

type Tab = 'champions' | 'items';

@Component({
  selector: 'tft-builder',
  standalone: true,
  imports: [FormsModule, Portrait, ItemIcon],
  templateUrl: './builder.html',
  styleUrl: './builder.scss',
})
export class Builder {
  private api = inject(ApiService);
  private router = inject(Router);
  store = inject(BoardStore);

  // reference data
  private unitsResource = toSignal(this.api.getUnits(), { initialValue: null });
  private itemsResource = toSignal(this.api.getItems(), { initialValue: null });

  loading = computed(() => this.unitsResource() === null || this.itemsResource() === null);
  allUnits = computed<Unit[]>(() => this.unitsResource() ?? []);
  allItems = computed<Item[]>(() => this.itemsResource() ?? []);

  // which picker tab is active
  tab = signal<Tab>('champions');

  // search / filter
  unitQuery = signal('');
  itemQuery = signal('');

  filteredUnits = computed(() => {
    const q = this.unitQuery().trim().toLowerCase();
    const list = [...this.allUnits()].sort((a, b) => a.name.localeCompare(b.name));
    return q ? list.filter((u) => u.name.toLowerCase().includes(q)) : list;
  });

  filteredItems = computed(() => {
    const q = this.itemQuery().trim().toLowerCase();
    const list = [...this.allItems()].sort((a, b) => a.name.localeCompare(b.name));
    return q ? list.filter((i) => i.name.toLowerCase().includes(q)) : list;
  });

  picked = (id: string) => this.store.unitIds().has(id);

  cycleStars(unitId: string, current: number, e: Event) {
    e.stopPropagation();
    this.store.setStars(unitId, current >= 3 ? 1 : current + 1);
  }

  findMatches() {
    if (!this.store.hasUnits()) return;
    this.router.navigate(['/results'], { queryParams: { board: this.store.serialize() } });
  }
}
