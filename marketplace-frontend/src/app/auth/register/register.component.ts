import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { TopBarComponent } from '../../core/components/top-bar/top-bar.component';
import { AuthLayoutComponent } from '../../shared/components/auth-layout/auth-layout.component';
import { FormFieldComponent } from '../../shared/components/form-field/form-field.component';
import { AuthService } from '../../core/services/auth.service';
import { RegisterRequest, StoreInfo } from '../../core/models/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    TopBarComponent,
    AuthLayoutComponent,
    FormFieldComponent
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly isSeller = signal(false);
  readonly loading = signal(false);
  readonly error = signal('');

  readonly form = this.fb.group({
    fullName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(8)]],
    businessName: [''],
    nit: ['']
  });

  private readonly missingLabels: Record<string, string> = {
    fullName: 'Nombre completo',
    email: 'Correo electrónico',
    phone: 'Teléfono',
    password: 'Contraseña',
    businessName: 'Nombre del negocio'
  };

  private buildMissingMessage(): string {
    const controls = this.form.controls as Record<string, FormControl>;
    const missing = Object.keys(controls)
      .filter(key => controls[key].invalid && this.missingLabels[key])
      .map(key => this.missingLabels[key]);
    if (!missing.length) {
      return 'Completa todos los campos obligatorios.';
    }
    return `Faltan campos obligatorios: ${missing.join(', ')}.`;
  }

  toggleSeller(): void {
    const businessName = this.form.controls.businessName;
    if (this.isSeller()) {
      businessName.clearValidators();
    } else {
      businessName.setValidators(Validators.required);
    }
    businessName.updateValueAndValidity();
    this.isSeller.update(v => !v);
  }

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
    if (control.hasError('minlength')) {
      return `Mínimo ${control.getError('minlength').requiredLength} caracteres`;
    }
    return 'Campo inválido';
  }

  onRegister(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set(this.buildMissingMessage());
      return;
    }

    const raw = this.form.value;
    const payload: RegisterRequest = {
      fullName: raw.fullName!,
      email: raw.email!,
      phone: '+57' + raw.phone!.replace(/\D/g, ''),
      password: raw.password!,
      role: this.isSeller() ? 'SELLER' : 'BUYER'
    };

    if (this.isSeller()) {
      const store: StoreInfo = {
        storeName: raw.businessName!,
        nit: raw.nit || undefined
      };
      payload.store = store;
    }

    this.loading.set(true);
    this.error.set('');

    this.authService.register(payload).subscribe({
      next: response => {
        if (response.success) {
          this.router.navigate(['/catalog']);
        } else {
          this.error.set(response.message || 'No se pudo crear la cuenta');
        }
      },
      error: err => {
        this.error.set(err.error?.message || err.error?.detail || 'Error al registrar. Verifica los datos.');
      },
      complete: () => this.loading.set(false)
    });
  }
}