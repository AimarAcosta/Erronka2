// Servicio de reuniones - CRUD de reuniones profesor-alumno
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from './users';

// Estados posibles de una reunion
export type ReunionEstado = 'pendiente' | 'aceptada' | 'denegada' | 'conflicto';
export type ReunionEstadoEus = 'onartzeke' | 'onartuta' | 'ezeztatuta' | 'gatazka';

export interface Reunion {
  id_reunion: number;
  estado: ReunionEstado;
  estado_eus?: ReunionEstadoEus;
  profesor_id?: number;
  alumno_id?: number;
  id_centro?: string;
  titulo?: string;
  asunto?: string;
  aula?: string;
  fecha?: Date;
  created_at?: Date;
  updated_at?: Date;
  profesor?: User;
  alumno?: User;
}

@Injectable({
  providedIn: 'root',
})
export class ReunionesService {
  private apiUrl = `${environment.apiUrl}/reuniones`;

  constructor(private http: HttpClient) {}

  // Obtiene todas las reuniones
  obtenerReuniones(): Observable<Reunion[]> {
    return this.http.get<Reunion[]>(this.apiUrl).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene reunion por ID
  obtenerReunionPorId(id: number): Observable<Reunion | undefined> {
    return this.http.get<Reunion>(`${this.apiUrl}/${id}`).pipe(
      catchError(() => of(undefined))
    );
  }

  // Obtiene reuniones de un profesor
  obtenerReunionesProfesor(profesorId: number): Observable<Reunion[]> {
    return this.http.get<Reunion[]>(`${this.apiUrl}/profesor/${profesorId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene reuniones de un alumno
  obtenerReunionesAlumno(alumnoId: number): Observable<Reunion[]> {
    return this.http.get<Reunion[]>(`${this.apiUrl}/alumno/${alumnoId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Cuenta reuniones de hoy (para estadisticas)
  contarReunionesHoy(): Observable<number> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/today/count`).pipe(
      map((res) => res.count),
      catchError(() => of(0))
    );
  }

  // Crea una nueva reunion
  crearReunion(reunion: Partial<Reunion>): Observable<Reunion | undefined> {
    return this.http.post<Reunion>(this.apiUrl, reunion).pipe(
      catchError(() => of(undefined))
    );
  }

  // Actualiza una reunion (cambiar estado, etc)
  actualizarReunion(id: number, data: Partial<Reunion>): Observable<Reunion | undefined> {
    return this.http.put<Reunion>(`${this.apiUrl}/${id}`, data).pipe(
      catchError(() => of(undefined))
    );
  }

  // Elimina una reunion
  eliminarReunion(id: number): Observable<boolean> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  // Traduce estado a euskera
  obtenerEstadoEus(estado: ReunionEstado): ReunionEstadoEus {
    const map: Record<ReunionEstado, ReunionEstadoEus> = {
      pendiente: 'onartzeke',
      aceptada: 'onartuta',
      denegada: 'ezeztatuta',
      conflicto: 'gatazka',
    };
    return map[estado];
  }
}
