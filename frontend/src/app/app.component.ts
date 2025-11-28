import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { UserListComponent } from './features/users/user-list/user-list.component';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    ButtonModule,
    TagModule,
    UserListComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'Basis Project';
  authService = inject(AuthService);
  window = window;

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get username(): string | undefined {
    return this.authService.getUsername();
  }

  login(): void {
    this.authService.login();
  }

  logout(): void {
    this.authService.logout();
  }

  register(): void {
    this.authService.register();
  }
}
