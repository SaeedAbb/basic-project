import { inject } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivateFn } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

export const authGuard: CanActivateFn = async (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const router = inject(Router);
  const keycloakService = inject(KeycloakService);

  // Check if user is authenticated
  const isLoggedIn = keycloakService.isLoggedIn();
  
  if (!isLoggedIn) {
    // Redirect to login
    await keycloakService.login({
      redirectUri: window.location.origin + state.url,
    });
    return false;
  }

  // Get the roles required from the route
  const requiredRoles = route.data['roles'] as string[];

  // Allow the user to proceed if no additional roles are required
  if (!requiredRoles || requiredRoles.length === 0) {
    return true;
  }

  // Check if user has required roles
  const userRoles = keycloakService.getUserRoles();
  const hasRequiredRoles = requiredRoles.every(role => userRoles.includes(role));

  if (!hasRequiredRoles) {
    // Redirect to unauthorized page or home
    router.navigate(['/']);
    return false;
  }

  return true;
};