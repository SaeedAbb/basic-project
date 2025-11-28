import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { KeycloakProfile } from 'keycloak-js';
import { from, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private keycloakService: KeycloakService) {}

  public getLoggedUser(): KeycloakProfile | undefined {
    try {
      const userProfile = this.keycloakService.getKeycloakInstance().profile;
      return userProfile ?? undefined;
    } catch (e) {
      console.error('Failed to load user profile', e);
      return undefined;
    }
  }

  public isLoggedIn(): boolean {
    return this.keycloakService.isLoggedIn();
  }

  public login(): void {
    this.keycloakService.login();
  }

  public logout(): void {
    this.keycloakService.logout();
  }

  public register(): void {
    this.keycloakService.register();
  }

  public getUserRoles(): string[] {
    return this.keycloakService.getUserRoles();
  }

  public hasRole(role: string): boolean {
    return this.getUserRoles().includes(role);
  }

  public getToken(): Observable<string> {
    return from(this.keycloakService.getToken());
  }

  public async loadUserProfile(): Promise<KeycloakProfile> {
    return this.keycloakService.loadUserProfile();
  }

  public getUsername(): string | undefined {
    try {
      if (!this.isLoggedIn()) {
        return undefined;
      }
      return this.keycloakService.getUsername();
    } catch (error) {
      return undefined;
    }
  }

  public getUserId(): string | undefined {
    const tokenParsed = this.keycloakService.getKeycloakInstance().tokenParsed;
    return tokenParsed?.sub;
  }

  public getEmail(): string | undefined {
    const tokenParsed = this.keycloakService.getKeycloakInstance().tokenParsed;
    return tokenParsed ? (tokenParsed as any).email : undefined;
  }

  public isTokenExpired(): boolean {
    return this.keycloakService.isTokenExpired();
  }

  public updateToken(minValidity: number = 5): Observable<boolean> {
    return from(this.keycloakService.updateToken(minValidity));
  }
}