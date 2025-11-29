import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { UserListComponent } from '../users/user-list/user-list.component';
import { AuthService } from '../../core/services/auth.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    TagModule,
    UserListComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
  title = 'Basis Project';
  authService = inject(AuthService);
  window = window;
  swaggerUrl = environment.swaggerUrl;

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get username(): string | undefined {
    return this.authService.getUsername();
  }

  logout(): void {
    this.authService.logout();
  }
}