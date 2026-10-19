import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/builder/builder').then((m) => m.Builder),
    title: 'TFT Team Builder — Find your comp',
  },
  {
    path: 'results',
    loadComponent: () => import('./features/results/results').then((m) => m.Results),
    title: 'TFT Team Builder — Matching comps',
  },
  {
    path: 'comp/:id',
    loadComponent: () => import('./features/results/comp-detail').then((m) => m.CompDetail),
    title: 'TFT Team Builder — Comp detail',
  },
  { path: '**', redirectTo: '' },
];
