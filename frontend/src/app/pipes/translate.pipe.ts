// Pipe de traduccion - Usa TranslationService para i18n
import { Pipe, PipeTransform, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { TranslationService } from '../services/translation';
import { Subscription } from 'rxjs';

@Pipe({
  name: 'translate',
  standalone: true,
  pure: false // Importante: se actualiza cuando cambia el idioma
})
export class TranslatePipe implements PipeTransform, OnDestroy {
  private langSubscription: Subscription;
  private currentLang = '';

  constructor(
    private translationService: TranslationService,
    private cdr: ChangeDetectorRef
  ) {
    // Se suscribe a cambios de idioma
    this.langSubscription = this.translationService.currentLang$.subscribe(lang => {
      this.currentLang = lang;
      this.cdr.markForCheck();
    });
  }

  // Traduce la clave (ej: 'HOME.WELCOME')
  transform(key: string): string {
    if (!key) return '';
    return this.translationService.traducir(key);
  }

  // Limpia la suscripcion al destruirse
  ngOnDestroy() {
    if (this.langSubscription) {
      this.langSubscription.unsubscribe();
    }
  }
}
