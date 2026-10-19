import { Component, input } from '@angular/core';
import { Item } from '../core/models';

/** Small item icon. */
@Component({
  selector: 'tft-item-icon',
  standalone: true,
  template: `
    <img
      class="item-icon"
      [src]="src()"
      [alt]="item().name"
      [attr.title]="item().name"
      loading="lazy"
      />
  `,
  styles: [`
    .item-icon {
      width: var(--size, 24px);
      height: var(--size, 24px);
      border-radius: 4px;
      border: 1px solid var(--border);
      background: var(--bg-2);
      display: block;
    }
  `],
})
export class ItemIcon {
  item = input.required<Item>();

  src = () => this.item().imageLink ?? '';
}
