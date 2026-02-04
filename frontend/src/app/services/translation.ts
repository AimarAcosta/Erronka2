// Servicio de traduccion (i18n) - Carga JSON de idiomas
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

export type Language = 'eu' | 'es' | 'en';

@Injectable({
  providedIn: 'root',
})
export class TranslationService {
  private currentLang = new BehaviorSubject<Language>('eu');
  private translations: { [key: string]: any } = {};
  currentLang$ = this.currentLang.asObservable();

  constructor(private http: HttpClient) {
    this.cargarTraducciones('eu');
  }

  get lang(): Language {
    return this.currentLang.value;
  }

  // Cambia el idioma y carga el JSON
  cambiarIdioma(lang: Language) {
    if (lang === this.currentLang.value) return;
    this.http.get<any>(`/assets/i18n/${lang}.json`).subscribe({
      next: (data) => {
        this.translations = data;
        this.currentLang.next(lang);
      },
      error: () => {},
    });
  }

  // Carga traducciones 
  private cargarTraducciones(lang: Language) {
    this.http.get<any>(`/assets/i18n/${lang}.json`).subscribe({
      next: (data) => {
        this.translations = data;
        this.currentLang.next(lang);
      },
      error: () => {},
    });
  }

  // Traduce una clave 
  traducir(key: string): string {
    const keys = key.split('.');
    let result: any = this.translations;

    for (const k of keys) {
      if (result && result[k] !== undefined) {
        result = result[k];
      } else {
        return key;
      }
    }

    return typeof result === 'string' ? result : key;
  }

  // Alias de traducir
  instant(key: string): string {
    return this.traducir(key);
  }
}
