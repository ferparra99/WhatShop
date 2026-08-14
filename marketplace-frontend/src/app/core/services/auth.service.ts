import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth';
import { TokenStorageService } from './token-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly authUrl = `${environment.apiUrl}/auth`;

  login(credentials: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.authUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.success && response.data?.token) {
          this.tokenStorage.saveToken(response.data.token);
        }
      })
    );
  }

  register(payload: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.authUrl}/register`, payload).pipe(
      tap(response => {
        if (response.success && response.data?.token) {
          this.tokenStorage.saveToken(response.data.token);
        }
      })
    );
  }
}