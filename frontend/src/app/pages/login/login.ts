// Página de login - Inicio de sesión de usuarios
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth';
import { UsersService } from '../../services/users';
import { TranslatePipe } from '../../pipes/translate.pipe';
import { TranslationService, Language } from '../../services/translation';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, TranslatePipe],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login implements OnInit, OnDestroy {
  username = '';
  password = '';
  errorMessage = '';
  currentLang: Language = 'eu';
  private langSubscription?: Subscription;

  constructor(
    private authService: AuthService,
    private usersService: UsersService,
    private router: Router,
    private translationService: TranslationService
  ) {}

  ngOnInit() {
    this.currentLang = this.translationService.lang;
    this.langSubscription = this.translationService.currentLang$.subscribe((lang) => {
      this.currentLang = lang;
    });
  }

  ngOnDestroy() {
    this.langSubscription?.unsubscribe();
  }

  // Cambia el idioma de la aplicación
  cambiarIdioma(lang: Language) {
    this.translationService.cambiarIdioma(lang);
  }

  // Envía el formulario de login
  onLogin() {
    this.usersService.login(this.username, this.password).subscribe((user) => {
      if (user) {
        this.authService.guardarUsuario(user);
        // Redirigir según el tipo de usuario
        switch (user.tipo_id) {
          case 1: this.router.navigate(['/god']); break;      // God
          case 2: this.router.navigate(['/admin']); break;    // Admin
          case 3: this.router.navigate(['/teacher']); break;  // Profesor
          case 4: this.router.navigate(['/student']); break;  // Estudiante
        }
      } else {
        this.errorMessage = this.translationService.traducir('LOGIN.ERROR');
      }
    });
  }
}
