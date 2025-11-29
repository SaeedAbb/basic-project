import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { KeycloakAuthService } from '../services/keycloak-auth.service';
import { catchError, switchMap, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloakAuthService = inject(KeycloakAuthService);
  const router = inject(Router);
  
  // Skip token attachment for Keycloak URLs (auth endpoints)
  if (req.url.includes('/realms/') || req.url.includes('/auth/')) {
    return next(req);
  }

  // Get the stored token
  const token = keycloakAuthService.getToken();
  
  // If no token, proceed without authorization header
  if (!token) {
    return next(req);
  }

  // Add token to request
  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // If we get a 401 Unauthorized, try to refresh the token
      if (error.status === 401) {
        return keycloakAuthService.refreshToken().pipe(
          switchMap(tokenResponse => {
            // Retry the original request with new token
            const retryReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${tokenResponse.access_token}`
              }
            });
            return next(retryReq);
          }),
          catchError(refreshError => {
            // If refresh fails, redirect to login
            router.navigate(['/auth/login']);
            return throwError(() => refreshError);
          })
        );
      }
      
      return throwError(() => error);
    })
  );
};