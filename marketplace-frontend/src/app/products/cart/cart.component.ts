import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TopBarComponent } from '../../core/components/top-bar/top-bar.component';
import { BottomNavComponent } from '../../core/components/bottom-nav/bottom-nav.component';

interface CartItem {
  id: string;
  label: string;
  name: string;
  price: number;
  imageUrl: string;
  quantity: number;
}

export const SHIPPING_COST = 0;
export const DISCOUNT = 25000;

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, TopBarComponent, BottomNavComponent],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent {
  readonly shippingCost = SHIPPING_COST;
  readonly discount = DISCOUNT;

  readonly items = signal<CartItem[]>([
    {
      id: '1',
      label: 'TechStore Bogotá',
      name: 'Auriculares Inalámbricos',
      price: 189900,
      imageUrl: 'https://placehold.co/128x128/ECEEF1/00471C?text=Auriculares',
      quantity: 2
    },
    {
      id: '2',
      label: 'Fashion Hub Colombia',
      name: 'Chaqueta Casual Premium',
      price: 320000,
      imageUrl: 'https://placehold.co/128x128/ECEEF1/00471C?text=Chaqueta',
      quantity: 1
    }
  ]);

  readonly subtotal = computed(() =>
    this.items().reduce((sum, item) => sum + item.price * item.quantity, 0)
  );

  readonly total = computed(() => this.subtotal() - DISCOUNT + SHIPPING_COST);

  countLabel = computed(() =>
    this.items().length === 1 ? 'Mi Carrito' : `Mi Carrito (${this.items().length})`
  );

  format(amount: number): string {
    return '$' + amount.toLocaleString('es-CO');
  }

  increment(id: string): void {
    this.items.update(items =>
      items.map(item => (item.id === id ? { ...item, quantity: item.quantity + 1 } : item))
    );
  }

  decrement(id: string): void {
    this.items.update(items =>
      items.map(item =>
        item.id === id && item.quantity > 1 ? { ...item, quantity: item.quantity - 1 } : item
      )
    );
  }

  remove(id: string): void {
    this.items.update(items => items.filter(item => item.id !== id));
  }

  clearCart(): void {
    this.items.set([]);
  }
}