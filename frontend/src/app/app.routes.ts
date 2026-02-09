// APP.ROUTES.TS - Configuración de rutas de la aplicación
// Define qué componente se carga para cada URL y qué rol se necesita

import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { HomeGod } from './pages/home-god/home-god';
import { HomeAdmin } from './pages/home-admin/home-admin';
import { HomeTeacher } from './pages/home-teacher/home-teacher';
import { HomeStudent } from './pages/home-student/home-student';
import { guardAutenticacion } from './services/autenticacion.guard';

export const routes: Routes = [
  // Ruta por defecto -> login
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // Login - Página de inicio de sesión
  { path: 'login', component: Login },

  // God - Panel de superadmin (puede hacer todo)
  { path: 'god', component: HomeGod, canActivate: [guardAutenticacion], data: { role: 'god' } },

  // Admin - Panel de administrador (gestiona usuarios)
  { path: 'admin', component: HomeAdmin, canActivate: [guardAutenticacion], data: { role: 'admin' } },

  // Teacher - Panel de profesor (ve horario y reuniones)
  { path: 'teacher', component: HomeTeacher, canActivate: [guardAutenticacion], data: { role: 'teacher' } },

  // Student - Panel de alumno (solo ve sus datos)
  { path: 'student', component: HomeStudent, canActivate: [guardAutenticacion], data: { role: 'student' } },

  // Cualquier otra ruta -> login
  { path: '**', redirectTo: 'login' },
];
