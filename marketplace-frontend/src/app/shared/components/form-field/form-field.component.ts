import { Component, forwardRef, input } from '@angular/core';
import { NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-form-field',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './form-field.component.html',
  styleUrl: './form-field.component.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => FormFieldComponent),
      multi: true
    }
  ]
})
export class FormFieldComponent implements ControlValueAccessor {
  readonly label = input<string>('');
  readonly icon = input<string>('');
  readonly placeholder = input<string>('');
  readonly type = input<string>('text');
  readonly prefix = input<string>('');
  readonly eyeToggle = input<boolean>(false);
  readonly required = input<boolean>(false);
  readonly invalid = input<boolean>(false);
  readonly error = input<string>('');

  showPassword = false;
  value = '';

  get inputType(): string {
    if (this.type() !== 'password') {
      return this.type();
    }
    return this.showPassword ? 'text' : 'password';
  }

  get eyeIcon(): string {
    return this.showPassword ? 'pi-eye-slash' : 'pi-eye';
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onInput(event: Event): void {
    const el = event.target as HTMLInputElement;
    this.value = el.value;
    this.onChange(el.value);
    this.onTouched();
  }

  // ControlValueAccessor
  onChange: (value: string) => void = () => {};
  onTouched: () => void = () => {};

  writeValue(value: string): void {
    this.value = value ?? '';
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    // no-op por ahora
  }
}