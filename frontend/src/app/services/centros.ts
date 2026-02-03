// Servicio de centros educativos - Carga datos de OpenData Euskadi
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, of, catchError } from 'rxjs';

// Formato del JSON original
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

// Formato normalizado para la app
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

  // Transforma datos del JSON al formato de la app
  // NOTA: En el JSON LATITUD y LONGITUD estan intercambiados
  private transformarCentro(raw: CentroRaw): Centro {
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
      LAT: raw.LONGITUD,
      LON: raw.LATITUD
    };
  }

  // Obtiene todos los centros (con cache)
  obtenerCentros(): Observable<Centro[]> {
    if (this.centros.length > 0) {
      return of(this.centros);
    }
    
    return this.http.get<any>(this.dataUrl).pipe(
      map(response => {
        let centrosArray: CentroRaw[] = [];
        
        if (response && response.CENTROS && Array.isArray(response.CENTROS)) {
          centrosArray = response.CENTROS;
        } else if (Array.isArray(response)) {
          centrosArray = response;
        } else {
          return [];
        }
        
        this.centros = centrosArray.map((raw: CentroRaw) => this.transformarCentro(raw));
        return this.centros;
      }),
      catchError(() => of([]))
    );
  }

  // Obtiene centro por codigo
  obtenerCentroPorId(codigo: string): Observable<Centro | undefined> {
    return this.obtenerCentros().pipe(
      map(centros => centros.find(c => c.CCODIGO === codigo))
    );
  }

  // Filtra por tipo de centro
  filtrarPorTipo(tipo: string): Observable<Centro[]> {
    return this.obtenerCentros().pipe(
      map(centros => tipo ? centros.filter(c => c.TIPO_CENTRO === tipo) : centros)
    );
  }

  // Filtra por territorio
  filtrarPorTerritorio(territorio: string): Observable<Centro[]> {
    return this.obtenerCentros().pipe(
      map(centros => territorio ? centros.filter(c => c.DTERRE === territorio) : centros)
    );
  }

  // Filtra por municipio
  filtrarPorMunicipio(municipio: string): Observable<Centro[]> {
    return this.obtenerCentros().pipe(
      map(centros => municipio ? centros.filter(c => c.DMUNI === municipio) : centros)
    );
  }

  // Filtra con multiples criterios
  filtrarCentros(filters: { tipo?: string; territorio?: string; municipio?: string }): Observable<Centro[]> {
    return this.obtenerCentros().pipe(
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

  // Para dropdown de tipos
  obtenerTipos(): Observable<string[]> {
    return this.obtenerCentros().pipe(
      map(centros => [...new Set(centros.map(c => c.TIPO_CENTRO))].filter(t => t).sort())
    );
  }

  // Para dropdown de territorios
  obtenerTerritorios(): Observable<string[]> {
    return this.obtenerCentros().pipe(
      map(centros => [...new Set(centros.map(c => c.DTERRE))])
    );
  }

  // Para dropdown de municipios (filtrable por territorio)
  obtenerMunicipios(territorio?: string): Observable<string[]> {
    return this.obtenerCentros().pipe(
      map(centros => {
        const filtered = territorio 
          ? centros.filter(c => c.DTERRE === territorio)
          : centros;
        return [...new Set(filtered.map(c => c.DMUNI))].sort();
      })
    );
  }

  // Centro por defecto: Elorrieta-Erreka Mari
  obtenerCentroDefecto(): Observable<Centro | undefined> {
    return this.obtenerCentroPorId('15112');
  }
}
