// Página de login - Inicio de sesión de usuarios
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ServicioAutenticacion } from '../../services/autenticacion';
import { ServicioUsuarios } from '../../services/usuarios';
import { TranslatePipe } from '../../pipes/translate.pipe';
import { ServicioTraduccion, Idioma } from '../../services/traduccion';
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
  idiomaActual: Idioma = 'eu';
  private suscripcionIdioma?: Subscription;

  constructor(
    private servicioAuth: ServicioAutenticacion,
    private servicioUsuarios: ServicioUsuarios,
    private router: Router,
    private servicioTraduccion: ServicioTraduccion
  ) {}

  ngOnInit() {
    this.idiomaActual = this.servicioTraduccion.idioma;
    this.suscripcionIdioma = this.servicioTraduccion.idiomaActual$.subscribe((idioma) => {
      this.idiomaActual = idioma;
    });
  }

  ngOnDestroy() {
    this.suscripcionIdioma?.unsubscribe();
  }

  // Cambia el idioma de la aplicación
  cambiarIdioma(idioma: Idioma) {
    this.servicioTraduccion.cambiarIdioma(idioma);
  }

  // Envía el formulario de login
  onLogin() {
    this.servicioUsuarios.login(this.username, this.password).subscribe((usuario) => {
      if (usuario) {
        this.servicioAuth.guardarUsuario(usuario);
        // Redirigir según el tipo de usuario
        switch (usuario.tipo_id) {
          case 1: this.router.navigate(['/god']); break;      // God
          case 2: this.router.navigate(['/admin']); break;    // Admin
          case 3: this.router.navigate(['/teacher']); break;  // Profesor
          case 4: this.router.navigate(['/student']); break;  // Estudiante
        }
      } else {
        this.errorMessage = this.servicioTraduccion.traducir('LOGIN.ERROR');
      }
    });
  }
}
