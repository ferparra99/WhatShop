import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { TopBarComponent } from '../../core/components/top-bar/top-bar.component';
import { BottomNavComponent } from '../../core/components/bottom-nav/bottom-nav.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink, TopBarComponent, BottomNavComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {
  private readonly router = inject(Router);

  readonly user = {
    name: 'Carlos Mendoza',
    email: 'carlos.mendoza@email.com',
    verified: 'Comprador verificado',
    avatarUrl: 'https://placehold.co/120x120/006B5F/FFFFFF?text=CM'
  };

  logout(): void {
    this.router.navigate(['/login']);
  }
}