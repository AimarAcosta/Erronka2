import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface Ciclo {
  id: number;
  nombre: string;
  nombre_eus?: string;
  codigo?: string;
  nivel?: string;
}

@Injectable({
  providedIn: 'root',
})
export class CiclosService {
  private apiUrl = `${environment.apiUrl}/ciclos`;

  constructor(private http: HttpClient) {}

  getCiclos(): Observable<Ciclo[]> {
    return this.http.get<Ciclo[]>(this.apiUrl).pipe(
      catchError(() => of([]))
    );
  }

  getCiclo(id: number): Observable<Ciclo | null> {
    return this.http.get<Ciclo>(`${this.apiUrl}/${id}`).pipe(
      catchError(() => of(null))
    );
  }
}
