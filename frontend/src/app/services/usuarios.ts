// Servicio de usuarios - CRUD de usuarios y busquedas
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, map } from 'rxjs';
import { environment } from '../../environments/environment';

// Interfaces
export interface Tipo {
  id: number;
  nombre: string;
  nombre_eu: string;
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

export interface Usuario {
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
export class ServicioUsuarios {
  private urlApi = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  // Login de usuario
  login(username: string, password: string): Observable<Usuario | undefined> {
    return this.http.post<Usuario>(`${this.urlApi}/login`, { username, password }).pipe(
      catchError(() => of(undefined))
    );
  }

  // Obtiene todos los usuarios
  obtenerUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.urlApi).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene usuario por ID
  obtenerUsuarioPorId(id: number): Observable<Usuario | undefined> {
    return this.http.get<Usuario>(`${this.urlApi}/${id}`).pipe(
      catchError(() => of(undefined))
    );
  }

  // Obtiene usuarios por tipo (1=God, 2=Admin, 3=Profesor, 4=Alumno)
  obtenerUsuariosPorTipo(tipoId: number): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.urlApi}/role/${tipoId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Busca usuarios por nombre
  buscarUsuarios(consulta: string): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.urlApi}/search/${consulta}`).pipe(
      catchError(() => of([]))
    );
  }

  // Busca alumnos con filtros (para profesores)
  buscarAlumnos(filtros: { nombre?: string; apellidos?: string; dni?: string; ciclo?: number }): Observable<Usuario[]> {
    let params: any = {};
    if (filtros.nombre) params.nombre = filtros.nombre;
    if (filtros.apellidos) params.apellidos = filtros.apellidos;
    if (filtros.dni) params.dni = filtros.dni;
    if (filtros.ciclo) params.ciclo = filtros.ciclo.toString();
    
    return this.http.get<Usuario[]>(`${this.urlApi}/students/search`, { params }).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene contadores para estadisticas
  obtenerContadores(): Observable<{ students: number; teachers: number; admins: number; total: number }> {
    return this.http.get<{ students: number; teachers: number; admins: number; total: number }>(`${this.urlApi}/count/all`).pipe(
      catchError(() => of({ students: 0, teachers: 0, admins: 0, total: 0 }))
    );
  }

  // Obtiene todos los tipos de usuario
  obtenerTipos(): Observable<Tipo[]> {
    return this.http.get<Tipo[]>(`${this.urlApi}/tipos/all`).pipe(
      catchError(() => of([]))
    );
  }

  // Elimina un usuario
  eliminarUsuario(id: number): Observable<boolean> {
    return this.http.delete<{ message: string }>(`${this.urlApi}/${id}`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  // Crea un usuario nuevo
  crearUsuario(usuario: Partial<Usuario>): Observable<Usuario | undefined> {
    return this.http.post<Usuario>(this.urlApi, usuario).pipe(
      catchError(() => of(undefined))
    );
  }

  // Actualiza un usuario
  actualizarUsuario(id: number, datosUsuario: Partial<Usuario>): Observable<Usuario | undefined> {
    return this.http.put<Usuario>(`${this.urlApi}/${id}`, datosUsuario).pipe(
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

  obtenerFotoConFallback(usuario: Usuario | string): string {
    const username = typeof usuario === 'string' ? usuario : usuario.username;
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
