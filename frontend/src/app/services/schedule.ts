// Servicio de horarios - Gestiona horarios de profesores y alumnos
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from './users';

// Dias de la semana
export type WeekDay = 'LUNES' | 'MARTES' | 'MIERCOLES' | 'JUEVES' | 'VIERNES';

export interface Modulo {
  id: number;
  nombre: string;
  nombre_eus?: string;
  horas: number;
  ciclo_id: number;
  curso: number;
}

export interface Ciclo {
  id: number;
  nombre: string;
}

export interface Horario {
  id: number;
  dia: WeekDay;
  hora: number;
  profe_id: number;
  modulo_id: number;
  aula?: string;
  observaciones?: string;
  created_at?: Date;
  updated_at?: Date;
  profesor?: User;
  modulo?: Modulo;
}

@Injectable({
  providedIn: 'root',
})
export class HorariosService {
  private apiUrl = `${environment.apiUrl}/horarios`;
  private modulosUrl = `${environment.apiUrl}/modulos`;
  private ciclosUrl = `${environment.apiUrl}/ciclos`;

  constructor(private http: HttpClient) {}

  // Obtiene todos los horarios
  obtenerHorarios(): Observable<Horario[]> {
    return this.http.get<Horario[]>(this.apiUrl).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene horario por ID
  obtenerHorarioPorId(id: number): Observable<Horario | undefined> {
    return this.http.get<Horario>(`${this.apiUrl}/${id}`).pipe(
      catchError(() => of(undefined))
    );
  }

  // Obtiene horario de un profesor
  obtenerHorarioProfesor(profesorId: number): Observable<Horario[]> {
    return this.http.get<Horario[]>(`${this.apiUrl}/profesor/${profesorId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene horario de un aula
  obtenerHorarioAula(aula: string): Observable<Horario[]> {
    return this.http.get<Horario[]>(`${this.apiUrl}/aula/${aula}`).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene horario de un ciclo (para alumnos)
  obtenerHorarioCiclo(cicloId: number): Observable<Horario[]> {
    return this.http.get<Horario[]>(`${this.apiUrl}/ciclo/${cicloId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Crea una entrada de horario
  crearHorario(horario: Partial<Horario>): Observable<Horario | undefined> {
    return this.http.post<Horario>(this.apiUrl, horario).pipe(
      catchError(() => of(undefined))
    );
  }

  // Actualiza una entrada de horario
  actualizarHorario(id: number, data: Partial<Horario>): Observable<Horario | undefined> {
    return this.http.put<Horario>(`${this.apiUrl}/${id}`, data).pipe(
      catchError(() => of(undefined))
    );
  }

  // Elimina una entrada de horario
  eliminarHorario(id: number): Observable<boolean> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  // Obtiene todos los modulos
  obtenerModulos(): Observable<Modulo[]> {
    return this.http.get<Modulo[]>(this.modulosUrl).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene modulo por ID
  obtenerModuloPorId(id: number): Observable<Modulo | undefined> {
    return this.http.get<Modulo>(`${this.modulosUrl}/${id}`).pipe(
      catchError(() => of(undefined))
    );
  }

  // Obtiene modulos de un ciclo
  obtenerModulosPorCiclo(cicloId: number): Observable<Modulo[]> {
    return this.http.get<Modulo[]>(`${this.modulosUrl}/ciclo/${cicloId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene todos los ciclos
  obtenerCiclos(): Observable<Ciclo[]> {
    return this.http.get<Ciclo[]>(this.ciclosUrl).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene ciclo por ID
  obtenerCicloPorId(id: number): Observable<Ciclo | undefined> {
    return this.http.get<Ciclo>(`${this.ciclosUrl}/${id}`).pipe(
      catchError(() => of(undefined))
    );
  }

  // Traduce dia a euskera
  obtenerDiaEus(dia: WeekDay): string {
    const map: Record<WeekDay, string> = {
      'LUNES': 'ASTELEHENA',
      'MARTES': 'ASTEARTEA',
      'MIERCOLES': 'ASTEAZKENA',
      'JUEVES': 'OSTEGUNA',
      'VIERNES': 'OSTIRALA'
    };
    return map[dia];
  }
}
