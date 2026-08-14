import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./auth/splash/splash.component').then(m => m.SplashComponent)
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'catalog',
    loadComponent: () =>
      import('./products/catalog/catalog.component').then(m => m.CatalogComponent)
  },
  {
    path: 'cart',
    loadComponent: () =>
      import('./products/cart/cart.component').then(m => m.CartComponent)
  },
  {
    path: 'profile',
    loadComponent: () =>
      import('./user/profile/profile.component').then(m => m.ProfileComponent)
  },
  { path: '**', redirectTo: '' }
];