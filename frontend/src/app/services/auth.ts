// Servicio de autenticacion - Guarda usuario en localStorage
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userKey = 'currentUser';

  constructor(private router: Router) {}

  // Guarda el usuario en localStorage
  guardarUsuario(user: any) {
    localStorage.setItem(this.userKey, JSON.stringify(user));
  }

  // Cierra sesion y redirige a login
  cerrarSesion() {
    localStorage.removeItem(this.userKey);
    this.router.navigate(['/login']);
  }

  // Devuelve el usuario actual o null
  obtenerUsuario() {
    const userStr = localStorage.getItem(this.userKey);
    return userStr ? JSON.parse(userStr) : null;
  }

  // Comprueba si hay usuario logueado
  estaAutenticado(): boolean {
    return !!localStorage.getItem(this.userKey);
  }

  // Comprueba si el usuario tiene el rol esperado
  // God (tipo_id=1) tiene acceso a todo
  tieneRol(expectedRole: string): boolean {
    const user = this.obtenerUsuario();
    if (!user) return false;
    
    if (user.tipo_id === 1) return true; // God puede todo
    
    const roleMap: { [key: string]: number } = {
      'god': 1,
      'admin': 2,
      'teacher': 3,
      'student': 4
    };
    
    return user.tipo_id === roleMap[expectedRole];
  }
}