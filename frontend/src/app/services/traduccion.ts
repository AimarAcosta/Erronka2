// Servicio de traduccion (i18n) - Carga JSON de idiomas
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

export type Idioma = 'eu' | 'es' | 'en';

@Injectable({
  providedIn: 'root',
})
export class ServicioTraduccion {
  private idiomaActual = new BehaviorSubject<Idioma>('eu');
  private traducciones: { [key: string]: any } = {};
  idiomaActual$ = this.idiomaActual.asObservable();

  constructor(private http: HttpClient) {
    this.cargarTraducciones('eu');
  }

  get idioma(): Idioma {
    return this.idiomaActual.value;
  }

  // Cambia el idioma y carga el JSON
  cambiarIdioma(idioma: Idioma) {
    if (idioma === this.idiomaActual.value) return;
    this.http.get<any>(`/assets/i18n/${idioma}.json`).subscribe({
      next: (datos) => {
        this.traducciones = datos;
        this.idiomaActual.next(idioma);
      },
      error: () => {},
    });
  }

  // Carga traducciones (uso interno)
  private cargarTraducciones(idioma: Idioma) {
    this.http.get<any>(`/assets/i18n/${idioma}.json`).subscribe({
      next: (datos) => {
        this.traducciones = datos;
        this.idiomaActual.next(idioma);
      },
      error: () => {},
    });
  }

  // Traduce una clave (ej: 'HOME.TITLE')
  traducir(clave: string): string {
    const claves = clave.split('.');
    let resultado: any = this.traducciones;

    for (const c of claves) {
      if (resultado && resultado[c] !== undefined) {
        resultado = resultado[c];
      } else {
        return clave;
      }
    }

    return typeof resultado === 'string' ? resultado : clave;
  }

  // Alias de traducir
  instant(clave: string): string {
    return this.traducir(clave);
  }
}
