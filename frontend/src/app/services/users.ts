// Servicio de usuarios - CRUD de usuarios y busquedas
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, map } from 'rxjs';
import { environment } from '../../environments/environment';

// Interfaces
export interface Tipo {
  id: number;
  name: string;
  name_eu: string;
}

export interface Ciclo {
  id: number;
  nombre: string;
  nombre_eus?: string;
}

export interface Matriculacion {
  id: number;
  ciclo?: Ciclo;
}

export interface User {
  id: number;
  email: string;
  username: string;
  password?: string;
  nombre: string;
  apellidos: string;
  dni?: string;
  direccion?: string;
  telefono1?: string;
  telefono2?: string;
  tipo_id: number;
  tipo?: Tipo;
  argazkia_url?: string;
  matriculaciones?: Matriculacion[];
  created_at?: Date;
  updated_at?: Date;
}

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  // Login de usuario
  login(username: string, password: string): Observable<User | undefined> {
    return this.http.post<User>(`${this.apiUrl}/login`, { username, password }).pipe(
      catchError(() => of(undefined))
    );
  }

  // Obtiene todos los usuarios
  obtenerUsuarios(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene usuario por ID
  obtenerUsuarioPorId(id: number): Observable<User | undefined> {
    return this.http.get<User>(`${this.apiUrl}/${id}`).pipe(
      catchError(() => of(undefined))
    );
  }

  // Obtiene usuarios por tipo (1=God, 2=Admin, 3=Teacher, 4=Student)
  obtenerUsuariosPorTipo(tipoId: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/role/${tipoId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Busca usuarios por nombre
  buscarUsuarios(query: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/search/${query}`).pipe(
      catchError(() => of([]))
    );
  }

  // Busca alumnos con filtros (para profesores)
  buscarAlumnos(filters: { nombre?: string; apellidos?: string; dni?: string; ciclo?: number }): Observable<User[]> {
    let params: any = {};
    if (filters.nombre) params.nombre = filters.nombre;
    if (filters.apellidos) params.apellidos = filters.apellidos;
    if (filters.dni) params.dni = filters.dni;
    if (filters.ciclo) params.ciclo = filters.ciclo.toString();
    
    return this.http.get<User[]>(`${this.apiUrl}/students/search`, { params }).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene contadores para estadisticas
  obtenerContadores(): Observable<{ students: number; teachers: number; admins: number; total: number }> {
    return this.http.get<{ students: number; teachers: number; admins: number; total: number }>(`${this.apiUrl}/count/all`).pipe(
      catchError(() => of({ students: 0, teachers: 0, admins: 0, total: 0 }))
    );
  }

  // Obtiene todos los tipos de usuario
  obtenerTipos(): Observable<Tipo[]> {
    return this.http.get<Tipo[]>(`${this.apiUrl}/tipos/all`).pipe(
      catchError(() => of([]))
    );
  }

  // Elimina un usuario
  eliminarUsuario(id: number): Observable<boolean> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  // Crea un usuario nuevo
  crearUsuario(user: Partial<User>): Observable<User | undefined> {
    return this.http.post<User>(this.apiUrl, user).pipe(
      catchError(() => of(undefined))
    );
  }

  // Actualiza un usuario
  actualizarUsuario(id: number, userData: Partial<User>): Observable<User | undefined> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, userData).pipe(
      catchError(() => of(undefined))
    );
  }

  // URL de la foto del usuario
  obtenerUrlFoto(username: string): string {
    return `${environment.apiUrl.replace('/api', '')}/public/${username}.jpg`;
  }

  obtenerUrlFotoDefecto(): string {
    return '/assets/perfil.jpg';
  }

  obtenerFotoConFallback(user: User | string): string {
    const username = typeof user === 'string' ? user : user.username;
    return this.obtenerUrlFoto(username);
  }

  // Nombre del rol
  obtenerNombreRol(tipoId: number): string {
    switch (tipoId) {
      case 1: return 'God';
      case 2: return 'Admin';
      case 3: return 'Profesor';
      case 4: return 'Alumno';
      default: return 'Desconocido';
    }
  }

  obtenerNombreRolEus(tipoId: number): string {
    switch (tipoId) {
      case 1: return 'Jainkoa';
      case 2: return 'Administratzailea';
      case 3: return 'Irakaslea';
      case 4: return 'Ikaslea';
      default: return 'Ezezaguna';
    }
  }
}

