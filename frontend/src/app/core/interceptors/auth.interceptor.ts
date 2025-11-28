import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { from, mergeMap } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloak = inject(KeycloakService);
  
  // Skip token attachment for Keycloak URLs
  if (req.url.includes('/realms/')) {
    return next(req);
  }

  // Check if user is logged in
  if (!keycloak.isLoggedIn()) {
    return next(req);
  }

  // Add token to request
  return from(keycloak.getToken()).pipe(
    mergeMap(token => {
      if (token) {
        const authReq = req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`
          }
        });
        return next(authReq);
      }
      return next(req);
    })
  );
};