import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-splash',
  standalone: true,
  imports: [],
  templateUrl: './splash.component.html',
  styleUrl: './splash.component.scss'
})
export class SplashComponent implements OnInit, OnDestroy {
  private readonly router = inject(Router);
  private timer?: ReturnType<typeof setTimeout>;

  ngOnInit(): void {
    // Redirige a Login tras 2.5s (simula tiempo de carga)
    this.timer = setTimeout(() => {
      this.router.navigate(['/login']);
    }, 2500);
  }

  ngOnDestroy(): void {
    if (this.timer) {
      clearTimeout(this.timer);
    }
  }
}