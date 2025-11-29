import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

describe('AppComponent', () => {
  let mockRouter: jasmine.SpyObj<Router>;
  let mockKeycloakService: jasmine.SpyObj<KeycloakService>;

  beforeEach(async () => {
    mockRouter = jasmine.createSpyObj('Router', ['navigate'], {
      events: { pipe: () => ({ subscribe: () => { /* Empty handler for testing */ } }) },
      url: '/'
    });
    
    mockKeycloakService = jasmine.createSpyObj('KeycloakService', ['isLoggedIn']);

    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: KeycloakService, useValue: mockKeycloakService }
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should initialize with loading state', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.isLoading).toBeTruthy();
  });
});
