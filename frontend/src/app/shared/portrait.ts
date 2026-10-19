import { Component, input } from '@angular/core';
import { Unit } from '../core/models';

/** Champion portrait with cost-colored ring and star level. */
@Component({
  selector: 'tft-portrait',
  standalone: true,
  template: `
    <div class="portrait" [class]="costClass()" [attr.title]="unit().name">
      <img
        [src]="src()"
        [alt]="unit().name"
        loading="lazy"
        />
      @if (stars()) {
        <div class="stars" [attr.data-stars]="stars()">
          @for (s of starArr(); track $index) { <span>★</span> }
        </div>
      }
    </div>
  `,
  styles: [`
    .portrait {
      position: relative;
      width: var(--size, 64px);
      height: var(--size, 64px);
      border-radius: 8px;
      overflow: hidden;
      border: 2px solid var(--ring, var(--border));
      background: var(--surface);
      box-shadow: 0 2px 8px rgba(0,0,0,.4);
    }
    img { width: 100%; height: 100%; object-fit: cover; display: block; }
    .stars {
      position: absolute; bottom: 0; left: 0; right: 0;
      display: flex; justify-content: center; gap: 1px;
      font-size: 0.6rem; line-height: 1;
      padding: 1px 0 2px;
      background: linear-gradient(180deg, transparent, rgba(0,0,0,.75));
      color: var(--cost-5);
      text-shadow: 0 1px 2px #000;
    }
  `],
})
export class Portrait {
  unit = input.required<Unit>();
  stars = input<number | null | undefined>(null);
  cost = input<number | null | undefined>(null);

  src = () => this.unit().imageLink ?? '';
  starArr = () => Array.from({ length: this.stars() ?? 0 });
  costClass = () => (this.cost() ? `cost-${this.cost()}` : '');
}
