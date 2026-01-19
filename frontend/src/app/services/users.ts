import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, map } from 'rxjs';
import { environment } from '../../environments/environment';

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

  login(username: string, password: string): Observable<User | undefined> {
    return this.http.post<User>(`${this.apiUrl}/login`, { username, password }).pipe(
      catchError(() => of(undefined))
    );
  }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl).pipe(
      catchError(() => of([]))
    );
  }

  getUserById(id: number): Observable<User | undefined> {
    return this.http.get<User>(`${this.apiUrl}/${id}`).pipe(
      catchError(() => of(undefined))
    );
  }

  getUsersByTipo(tipoId: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/role/${tipoId}`).pipe(
      catchError(() => of([]))
    );
  }

  searchUsers(query: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/search/${query}`).pipe(
      catchError(() => of([]))
    );
  }

  searchStudents(filters: { nombre?: string; apellidos?: string; dni?: string; ciclo?: number }): Observable<User[]> {
    let params: any = {};
    if (filters.nombre) params.nombre = filters.nombre;
    if (filters.apellidos) params.apellidos = filters.apellidos;
    if (filters.dni) params.dni = filters.dni;
    if (filters.ciclo) params.ciclo = filters.ciclo.toString();
    
    return this.http.get<User[]>(`${this.apiUrl}/students/search`, { params }).pipe(
      catchError(() => of([]))
    );
  }

  getUsersCount(): Observable<{ students: number; teachers: number; admins: number; total: number }> {
    return this.http.get<{ students: number; teachers: number; admins: number; total: number }>(`${this.apiUrl}/count/all`).pipe(
      catchError(() => of({ students: 0, teachers: 0, admins: 0, total: 0 }))
    );
  }

  getTipos(): Observable<Tipo[]> {
    return this.http.get<Tipo[]>(`${this.apiUrl}/tipos/all`).pipe(
      catchError(() => of([]))
    );
  }

  deleteUser(id: number): Observable<boolean> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  createUser(user: Partial<User>): Observable<User | undefined> {
    return this.http.post<User>(this.apiUrl, user).pipe(
      catchError(() => of(undefined))
    );
  }

  updateUser(id: number, userData: Partial<User>): Observable<User | undefined> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, userData).pipe(
      catchError(() => of(undefined))
    );
  }

  getUserPhotoUrl(username: string): string {
    return `${environment.apiUrl.replace('/api', '')}/public/${username}.jpg`;
  }

  getDefaultPhotoUrl(): string {
    return '/assets/perfil.jpg';
  }

  getPhotoWithFallback(user: User | string): string {
    const username = typeof user === 'string' ? user : user.username;
    return this.getUserPhotoUrl(username);
  }

  getRoleName(tipoId: number): string {
    switch (tipoId) {
      case 1: return 'God';
      case 2: return 'Admin';
      case 3: return 'Profesor';
      case 4: return 'Alumno';
      default: return 'Desconocido';
    }
  }

  getRoleNameEus(tipoId: number): string {
    switch (tipoId) {
      case 1: return 'Jainkoa';
      case 2: return 'Administratzailea';
      case 3: return 'Irakaslea';
      case 4: return 'Ikaslea';
      default: return 'Ezezaguna';
    }
  }
}

