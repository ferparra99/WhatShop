import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface ProductCardData {
  id: string;
  name: string;
  storeName: string;
  price: number;
  imageUrl: string;
}

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss'
})
export class ProductCardComponent {
  readonly product = input.required<ProductCardData>();

  get formattedPrice(): string {
    return '$' + this.product().price.toLocaleString('es-CO') + ' COP';
  }
}