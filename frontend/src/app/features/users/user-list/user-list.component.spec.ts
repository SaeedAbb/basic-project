import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { KeycloakAuthService } from '../../../core/services/keycloak-auth.service';

import { UserListComponent } from './user-list.component';

describe('UserListComponent', () => {
  let component: UserListComponent;
  let fixture: ComponentFixture<UserListComponent>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockKeycloakAuthService: jasmine.SpyObj<KeycloakAuthService>;

  beforeEach(async () => {
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockKeycloakAuthService = jasmine.createSpyObj('KeycloakAuthService', ['getToken']);
    mockKeycloakAuthService.getToken.and.returnValue('mock-token');

    await TestBed.configureTestingModule({
      imports: [UserListComponent, HttpClientTestingModule],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: KeycloakAuthService, useValue: mockKeycloakAuthService }
      ]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UserListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
