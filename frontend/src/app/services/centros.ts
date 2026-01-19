import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, of, catchError } from 'rxjs';

export interface CentroRaw {
  CCEN: number;
  NOM: string;
  NOME: string;
  DGENRC: string;
  DGENRE: string;
  GENR: string;
  MUNI: number;
  DMUNIC: string;
  DMUNIE: string;
  DTERRC: string;
  DTERRE: string;
  DEPE: number;
  DTITUC: string;
  DTITUE: string;
  DOMI: string;
  CPOS: number;
  TEL1: number;
  TFAX: number;
  EMAIL: string;
  PAGINA: string;
  COOR_X: string;
  COOR_Y: string;
  LATITUD: number;
  LONGITUD: number;
}

export interface CentrosResponse {
  CENTROS: CentroRaw[];
}

export interface Centro {
  CCODIGO: string;
  DNOMBRE: string;
  DTITUC: string;
  DTERRE: string;
  DMUNI: string;
  DDOMICILIO: string;
  CPOSTAL: string;
  TELEFONO: string;
  EMAIL: string;
  WEB: string;
  TIPO_CENTRO: string;
  LAT: number;
  LON: number;
}

@Injectable({
  providedIn: 'root'
})
export class CentrosService {
  private centros: Centro[] = [];
  private dataUrl = '/assets/data/centros.json';

  constructor(private http: HttpClient) {}

  private transformCentro(raw: CentroRaw): Centro {
    return {
      CCODIGO: raw.CCEN.toString(),
      DNOMBRE: raw.NOME || raw.NOM,
      DTITUC: raw.DTITUE || raw.DTITUC,
      DTERRE: raw.DTERRE || raw.DTERRC,
      DMUNI: raw.DMUNIE || raw.DMUNIC,
      DDOMICILIO: raw.DOMI,
      CPOSTAL: raw.CPOS.toString().padStart(5, '0'),
      TELEFONO: raw.TEL1?.toString() || '',
      EMAIL: raw.EMAIL,
      WEB: raw.PAGINA?.trim() || '',
      TIPO_CENTRO: raw.DGENRE || raw.DGENRC,
      LAT: raw.LONGITUD,  // En el JSON LONGITUD es latitud (eje Y)
      LON: raw.LATITUD    // En el JSON LATITUD es longitud (eje X)
    };
  }

  getCentros(): Observable<Centro[]> {
    if (this.centros.length > 0) {
      return of(this.centros);
    }
    return this.http.get<any>(this.dataUrl).pipe(
      map(response => {
        console.log('Centros response type:', typeof response, response);
        
        // Extraer el array de centros según la estructura
        let centrosArray: CentroRaw[] = [];
        
        if (response && response.CENTROS && Array.isArray(response.CENTROS)) {
          centrosArray = response.CENTROS;
        } else if (Array.isArray(response)) {
          centrosArray = response;
        } else {
          console.error('Formato de centros no reconocido:', response);
          return [];
        }
        
        console.log('Centros count:', centrosArray.length);
        this.centros = centrosArray.map((raw: CentroRaw) => this.transformCentro(raw));
        return this.centros;
      }),
      catchError(error => {
        console.error('Error cargando centros:', error);
        return of([]);
      })
    );
  }

  getCentroById(codigo: string): Observable<Centro | undefined> {
    return this.getCentros().pipe(
      map(centros => centros.find(c => c.CCODIGO === codigo))
    );
  }

  filterByTipo(tipo: string): Observable<Centro[]> {
    return this.getCentros().pipe(
      map(centros => tipo ? centros.filter(c => c.TIPO_CENTRO === tipo) : centros)
    );
  }

  filterByTerritorio(territorio: string): Observable<Centro[]> {
    return this.getCentros().pipe(
      map(centros => territorio ? centros.filter(c => c.DTERRE === territorio) : centros)
    );
  }

  filterByMunicipio(municipio: string): Observable<Centro[]> {
    return this.getCentros().pipe(
      map(centros => municipio ? centros.filter(c => c.DMUNI === municipio) : centros)
    );
  }

  filterCentros(filters: { tipo?: string; territorio?: string; municipio?: string }): Observable<Centro[]> {
    return this.getCentros().pipe(
      map(centros => {
        let result = centros;
        if (filters.tipo) {
          result = result.filter(c => c.TIPO_CENTRO === filters.tipo);
        }
        if (filters.territorio) {
          result = result.filter(c => c.DTERRE === filters.territorio);
        }
        if (filters.municipio) {
          result = result.filter(c => c.DMUNI === filters.municipio);
        }
        return result;
      })
    );
  }

  getTipos(): Observable<string[]> {
    return this.getCentros().pipe(
      map(centros => [...new Set(centros.map(c => c.TIPO_CENTRO))].filter(t => t).sort())
    );
  }

  getTerritorios(): Observable<string[]> {
    return this.getCentros().pipe(
      map(centros => [...new Set(centros.map(c => c.DTERRE))])
    );
  }

  getMunicipios(territorio?: string): Observable<string[]> {
    return this.getCentros().pipe(
      map(centros => {
        const filtered = territorio 
          ? centros.filter(c => c.DTERRE === territorio)
          : centros;
        return [...new Set(filtered.map(c => c.DMUNI))].sort();
      })
    );
  }

  getDefaultCentro(): Observable<Centro | undefined> {
    return this.getCentroById('15112');
  }
}
