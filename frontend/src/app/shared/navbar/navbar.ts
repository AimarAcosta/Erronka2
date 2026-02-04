// Navbar - Barra de navegacion con logo, idioma y logout
import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common'; 
import { ServicioAutenticacion } from '../../services/autenticacion';
import { ServicioTraduccion, Idioma } from '../../services/traduccion';
import { TranslatePipe } from '../../pipes/translate.pipe';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule, TranslatePipe],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class Navbar implements OnInit, OnDestroy {
  
  idiomaActual: Idioma = 'eu';
  
  idiomas = [
    { codigo: 'eu' as Idioma, nombre: 'Euskara', bandera: '🇪🇺' },
    { codigo: 'es' as Idioma, nombre: 'Español', bandera: '🇪🇸' },
    { codigo: 'en' as Idioma, nombre: 'English', bandera: '🇬🇧' }
  ];
  
  private suscripcionIdioma?: Subscription;
  
  get usuarioActual() {
    return this.servicioAuth.obtenerUsuario();
  }

  constructor(
    private servicioAuth: ServicioAutenticacion, 
    private router: Router,
    private servicioTraduccion: ServicioTraduccion
  ) {}

  ngOnInit() {
    this.idiomaActual = this.servicioTraduccion.idioma;
    this.suscripcionIdioma = this.servicioTraduccion.idiomaActual$.subscribe(idioma => {
      this.idiomaActual = idioma;
    });
  }

  ngOnDestroy() {
    this.suscripcionIdioma?.unsubscribe();
  }

  cambiarIdioma(idioma: Idioma) {
    this.servicioTraduccion.cambiarIdioma(idioma);
  }

  cerrarSesion() {
    this.servicioAuth.cerrarSesion(); 
    this.router.navigate(['/login']); 
  }
}