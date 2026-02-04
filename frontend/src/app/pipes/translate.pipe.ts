// Pipe de traduccion - Usa TranslationService para i18n
import { Pipe, PipeTransform, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { ServicioTraduccion } from '../services/traduccion';
import { Subscription } from 'rxjs';

@Pipe({
  name: 'translate',
  standalone: true,
  pure: false // Importante: se actualiza cuando cambia el idioma
})
export class TranslatePipe implements PipeTransform, OnDestroy {
  private suscripcionIdioma: Subscription;
  private idiomaActual = '';

  constructor(
    private servicioTraduccion: ServicioTraduccion,
    private cdr: ChangeDetectorRef
  ) {
    // Se suscribe a cambios de idioma
    this.suscripcionIdioma = this.servicioTraduccion.idiomaActual$.subscribe(idioma => {
      this.idiomaActual = idioma;
      this.cdr.markForCheck();
    });
  }

  // Traduce la clave (ej: 'HOME.WELCOME')
  transform(clave: string): string {
    if (!clave) return '';
    return this.servicioTraduccion.traducir(clave);
  }

  // Limpia la suscripcion al destruirse
  ngOnDestroy() {
    if (this.suscripcionIdioma) {
      this.suscripcionIdioma.unsubscribe();
    }
  }
}
