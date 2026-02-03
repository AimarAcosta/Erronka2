// Servicio de matriculaciones - Vincula alumnos con ciclos
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from './users';

export interface Ciclo {
  id: number;
  nombre: string;
}

export interface Matriculacion {
  id: number;
  alum_id: number;
  ciclo_id: number;
  curso: number;
  fecha: Date;
  alumno?: User;
  ciclo?: Ciclo;
}

@Injectable({
  providedIn: 'root',
})
export class MatriculacionesService {
  private apiUrl = `${environment.apiUrl}/matriculaciones`;

  constructor(private http: HttpClient) {}

  // Obtiene todas las matriculaciones
  obtenerMatriculaciones(): Observable<Matriculacion[]> {
    return this.http.get<Matriculacion[]>(this.apiUrl).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene matriculacion por ID
  obtenerMatriculacionPorId(id: number): Observable<Matriculacion | undefined> {
    return this.http.get<Matriculacion>(`${this.apiUrl}/${id}`).pipe(
      catchError(() => of(undefined))
    );
  }

  // Obtiene matriculaciones de un alumno
  obtenerMatriculacionesAlumno(alumnoId: number): Observable<Matriculacion[]> {
    return this.http.get<Matriculacion[]>(`${this.apiUrl}/alumno/${alumnoId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene matriculaciones de un ciclo
  obtenerMatriculacionesCiclo(cicloId: number): Observable<Matriculacion[]> {
    return this.http.get<Matriculacion[]>(`${this.apiUrl}/ciclo/${cicloId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Crea una matriculacion
  crearMatriculacion(matriculacion: Partial<Matriculacion>): Observable<Matriculacion | undefined> {
    return this.http.post<Matriculacion>(this.apiUrl, matriculacion).pipe(
      catchError(() => of(undefined))
    );
  }

  // Elimina una matriculacion
  eliminarMatriculacion(id: number): Observable<boolean> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }
}
