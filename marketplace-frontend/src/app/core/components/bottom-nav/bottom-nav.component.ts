import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

export type BottomNavItem = {
  id: string;
  label: string;
  icon: string;
  route: string;
};

@Component({
  selector: 'app-bottom-nav',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './bottom-nav.component.html',
  styleUrl: './bottom-nav.component.scss'
})
export class BottomNavComponent {
  readonly items = input<BottomNavItem[]>([
    { id: 'home', label: 'Inicio', icon: 'pi-home', route: '/catalog' },
    { id: 'search', label: 'Buscar', icon: 'pi-search', route: '/catalog' },
    { id: 'orders', label: 'Pedidos', icon: 'pi-shopping-bag', route: '/cart' },
    { id: 'inbox', label: 'Chat', icon: 'pi-comments', route: '/profile' },
    { id: 'profile', label: 'Perfil', icon: 'pi-user', route: '/profile' }
  ]);
  readonly active = input<string>('home');
}