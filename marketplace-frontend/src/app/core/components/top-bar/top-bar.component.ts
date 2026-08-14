import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-top-bar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './top-bar.component.html',
  styleUrl: './top-bar.component.scss'
})
export class TopBarComponent {
  readonly title = input<string>();
  readonly showBack = input<boolean>(true);
  readonly showMenu = input<boolean>(false);

  goBack(): void {
    history.back();
  }
}