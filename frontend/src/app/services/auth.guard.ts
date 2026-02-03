// Guard de rutas - Protege rutas segun login y rol
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Si no esta logueado, redirige a login
  if (!authService.estaAutenticado()) {
    router.navigate(['/login']);
    return false;
  }

  // Si la ruta requiere un rol especifico
  const requiredRole = route.data['role'];
  if (requiredRole && !authService.tieneRol(requiredRole)) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};