import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TopBarComponent } from '../../core/components/top-bar/top-bar.component';
import { BottomNavComponent } from '../../core/components/bottom-nav/bottom-nav.component';
import {
  ProductCardComponent,
  ProductCardData
} from '../../shared/components/product-card/product-card.component';

const CATEGORIES = ['Todos', 'Tecnología', 'Moda', 'Hogar', 'Deportes'];

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, TopBarComponent, BottomNavComponent, ProductCardComponent],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.scss'
})
export class CatalogComponent {
  readonly categories = CATEGORIES;

  readonly products: ProductCardData[] = [
    {
      id: '1',
      name: 'Auriculares Inalámbricos',
      storeName: 'TechStore Bogotá',
      price: 189900,
      imageUrl: 'https://placehold.co/400x400/075E54/FFFFFF?text=Auriculares'
    },
    {
      id: '2',
      name: 'Chaqueta Casual Premium',
      storeName: 'Fashion Hub Colombia',
      price: 320000,
      imageUrl: 'https://placehold.co/400x400/25D366/FFFFFF?text=Chaqueta'
    },
    {
      id: '3',
      name: 'Lámpara LED Inteligente',
      storeName: 'Hogar Inteligente',
      price: 125000,
      imageUrl: 'https://placehold.co/400x400/00471C/FFFFFF?text=Lumpara'
    },
    {
      id: '4',
      name: 'Café Gourmet Premium',
      storeName: 'Market Gourmet',
      price: 45500,
      imageUrl: 'https://placehold.co/400x400/006B5F/FFFFFF?text=Cafe'
    }
  ];
}