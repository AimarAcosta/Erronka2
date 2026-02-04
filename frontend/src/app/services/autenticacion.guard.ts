// Guard de rutas - Protege rutas segun login y rol
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { ServicioAutenticacion } from './autenticacion';

export const guardAutenticacion: CanActivateFn = (ruta, estado) => {
  const servicioAuth = inject(ServicioAutenticacion);
  const router = inject(Router);

  // Si no esta logueado, redirige a login
  if (!servicioAuth.estaAutenticado()) {
    router.navigate(['/login']);
    return false;
  }

  // Si la ruta requiere un rol especifico
  const rolRequerido = ruta.data['role'];
  if (rolRequerido && !servicioAuth.tieneRol(rolRequerido)) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
