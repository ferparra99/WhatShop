import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { AuthLayoutComponent } from '../../shared/components/auth-layout/auth-layout.component';
import { FormFieldComponent } from '../../shared/components/form-field/form-field.component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    AuthLayoutComponent,
    FormFieldComponent
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly error = signal('');

  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  errorFor(control: FormControl | null): string {
    if (!control || !control.invalid) {
      return '';
    }
    if (control.hasError('required')) {
      return 'Campo obligatorio';
    }
    if (control.hasError('email')) {
      return 'Ingresa un correo válido';
    }
    return 'Campo inválido';
  }

  private buildMissingMessage(): string {
    const labels: Record<string, string> = {
      email: 'Correo electrónico',
      password: 'Contraseña'
    };
    const controls = this.form.controls as Record<string, FormControl>;
    const missing = Object.keys(controls)
      .filter(key => controls[key].invalid && labels[key])
      .map(key => labels[key]);
    if (!missing.length) {
      return 'Completa los campos obligatorios para iniciar sesión.';
    }
    return `Faltan campos: ${missing.join(', ')}.`;
  }

  onLogin(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set(this.buildMissingMessage());
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.authService.login(this.form.value as { email: string; password: string }).subscribe({
      next: response => {
        if (response.success) {
          this.router.navigate(['/catalog']);
        } else {
          this.error.set(response.message || 'No se pudo iniciar sesión');
        }
      },
      error: err => {
        this.error.set(err.error?.message || err.error?.detail || 'Credenciales incorrectas. Inténtalo de nuevo.');
      },
      complete: () => this.loading.set(false)
    });
  }
}