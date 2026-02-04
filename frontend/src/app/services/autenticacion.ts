// Servicio de autenticacion - Guarda usuario en localStorage
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class ServicioAutenticacion {
  private claveUsuario = 'currentUser';

  constructor(private router: Router) {}

  // Guarda el usuario en localStorage
  guardarUsuario(usuario: any) {
    localStorage.setItem(this.claveUsuario, JSON.stringify(usuario));
  }

  // Cierra sesion y redirige a login
  cerrarSesion() {
    localStorage.removeItem(this.claveUsuario);
    this.router.navigate(['/login']);
  }

  // Devuelve el usuario actual o null
  obtenerUsuario() {
    const usuarioStr = localStorage.getItem(this.claveUsuario);
    return usuarioStr ? JSON.parse(usuarioStr) : null;
  }

  // Comprueba si hay usuario logueado
  estaAutenticado(): boolean {
    return !!localStorage.getItem(this.claveUsuario);
  }

  // Comprueba si el usuario tiene el rol esperado
  // God (tipo_id=1) tiene acceso a todo
  tieneRol(rolEsperado: string): boolean {
    const usuario = this.obtenerUsuario();
    if (!usuario) return false;
    
    if (usuario.tipo_id === 1) return true; // God puede todo
    
    const mapaRoles: { [key: string]: number } = {
      'god': 1,
      'admin': 2,
      'teacher': 3,
      'student': 4
    };
    
    return usuario.tipo_id === mapaRoles[rolEsperado];
  }
}
