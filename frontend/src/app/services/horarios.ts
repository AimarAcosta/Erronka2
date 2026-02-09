// Servicio de horarios - Gestiona horarios de profesores y alumnos
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Usuario } from './usuarios';

// Dias de la semana
export type DiaSemana = 'LUNES' | 'MARTES' | 'MIERCOLES' | 'JUEVES' | 'VIERNES';

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
  dia: DiaSemana;
  hora: number;
  profe_id: number;
  modulo_id: number;
  aula?: string;
  observaciones?: string;
  created_at?: Date;
  updated_at?: Date;
  profesor?: Usuario;
  modulo?: Modulo;
}

@Injectable({
  providedIn: 'root',
})
export class ServicioHorarios {
  private urlApi = `${environment.apiUrl}/horarios`;
  private urlModulos = `${environment.apiUrl}/modulos`;
  private urlCiclos = `${environment.apiUrl}/ciclos`;

  constructor(private http: HttpClient) {}

  // Obtiene todos los horarios
  obtenerHorarios(): Observable<Horario[]> {
    return this.http.get<Horario[]>(this.urlApi).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene horario por ID
  obtenerHorarioPorId(id: number): Observable<Horario | undefined> {
    return this.http.get<Horario>(`${this.urlApi}/${id}`).pipe(
      catchError(() => of(undefined))
    );
  }

  // Obtiene horario de un profesor
  obtenerHorarioProfesor(profesorId: number): Observable<Horario[]> {
    return this.http.get<Horario[]>(`${this.urlApi}/profesor/${profesorId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene horario de un aula
  obtenerHorarioAula(aula: string): Observable<Horario[]> {
    return this.http.get<Horario[]>(`${this.urlApi}/aula/${aula}`).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene horario de un ciclo (para alumnos)
  obtenerHorarioCiclo(cicloId: number): Observable<Horario[]> {
    return this.http.get<Horario[]>(`${this.urlApi}/ciclo/${cicloId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Crea una entrada de horario
  crearHorario(horario: Partial<Horario>): Observable<Horario | undefined> {
    return this.http.post<Horario>(this.urlApi, horario).pipe(
      catchError(() => of(undefined))
    );
  }

  // Actualiza una entrada de horario
  actualizarHorario(id: number, datos: Partial<Horario>): Observable<Horario | undefined> {
    return this.http.put<Horario>(`${this.urlApi}/${id}`, datos).pipe(
      catchError(() => of(undefined))
    );
  }

  // Elimina una entrada de horario
  eliminarHorario(id: number): Observable<boolean> {
    return this.http.delete<{ message: string }>(`${this.urlApi}/${id}`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  // Obtiene todos los modulos
  obtenerModulos(): Observable<Modulo[]> {
    return this.http.get<Modulo[]>(this.urlModulos).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene modulo por ID
  obtenerModuloPorId(id: number): Observable<Modulo | undefined> {
    return this.http.get<Modulo>(`${this.urlModulos}/${id}`).pipe(
      catchError(() => of(undefined))
    );
  }

  // Obtiene modulos de un ciclo
  obtenerModulosPorCiclo(cicloId: number): Observable<Modulo[]> {
    return this.http.get<Modulo[]>(`${this.urlModulos}/ciclo/${cicloId}`).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene todos los ciclos
  obtenerCiclos(): Observable<Ciclo[]> {
    return this.http.get<Ciclo[]>(this.urlCiclos).pipe(
      catchError(() => of([]))
    );
  }

  // Obtiene ciclo por ID
  obtenerCicloPorId(id: number): Observable<Ciclo | undefined> {
    return this.http.get<Ciclo>(`${this.urlCiclos}/${id}`).pipe(
      catchError(() => of(undefined))
    );
  }

  // Traduce dia a euskera
  obtenerDiaEus(dia: DiaSemana): string {
    const mapa: Record<DiaSemana, string> = {
      'LUNES': 'ASTELEHENA',
      'MARTES': 'ASTEARTEA',
      'MIERCOLES': 'ASTEAZKENA',
      'JUEVES': 'OSTEGUNA',
      'VIERNES': 'OSTIRALA'
    };
    return mapa[dia];
  }
}
