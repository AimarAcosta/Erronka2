import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CentrosService, Centro } from '../../services/centros';
import { TranslatePipe } from '../../pipes/translate.pipe';
declare var maplibregl: any;

@Component({
  selector: 'app-centro-selector',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe],
  templateUrl: './centro-selector.html',
  styleUrl: './centro-selector.css'
})
export class CentroSelector implements OnInit, AfterViewInit, OnDestroy {
  @Input() selectedCentroId: string = '15112';
  @Output() centroSelected = new EventEmitter<Centro>();

  centros: Centro[] = [];
  filteredCentros: Centro[] = [];
  selectedCentro: Centro | null = null;
  isLoading = true;

  tipos: string[] = [];
  territorios: string[] = [];
  municipios: string[] = [];

  filterTipo: string = '';
  filterTerritorio: string = '';
  filterMunicipio: string = '';

  private map: any;
  private markers: any[] = [];
  private mapInitialized = false;

  constructor(
    private centrosService: CentrosService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadCentros();
    this.loadFilterOptions();
  }

  ngAfterViewInit() {
  }

  private tryInitMap(attempts = 0) {
    if (this.mapInitialized) return;
    
    if (attempts > 10) return;
    
    const container = document.getElementById('map-container');
    
    if (container && container.clientWidth > 0) {
      this.initMap();
    } else {
      setTimeout(() => this.tryInitMap(attempts + 1), 200);
    }
  }

  ngOnDestroy() {
    if (this.map) {
      this.map.remove();
    }
  }

  loadCentros() {
    this.isLoading = true;
    this.centrosService.getCentros().subscribe({
      next: (centros) => {
        this.centros = centros;
        this.filteredCentros = centros;
        this.isLoading = false;
        
        if (this.selectedCentroId) {
          this.selectedCentro = centros.find(c => c.CCODIGO === this.selectedCentroId) || null;
        }
        
        this.cdr.detectChanges();
        
        setTimeout(() => {
          this.tryInitMap();
        }, 100);
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadFilterOptions() {
    this.centrosService.getTipos().subscribe(tipos => {
      this.tipos = tipos;
      this.cdr.detectChanges();
    });
    
    this.centrosService.getTerritorios().subscribe(territorios => {
      this.territorios = territorios;
      this.cdr.detectChanges();
    });
  }

  onTerritorioChange() {
    this.filterMunicipio = '';
    this.centrosService.getMunicipios(this.filterTerritorio).subscribe(municipios => {
      this.municipios = municipios;
      this.cdr.detectChanges();
    });
    this.applyFilters();
  }

  applyFilters() {
    this.centrosService.filterCentros({
      tipo: this.filterTipo,
      territorio: this.filterTerritorio,
      municipio: this.filterMunicipio
    }).subscribe(centros => {
      this.filteredCentros = centros;
      this.cdr.detectChanges();
      this.updateMarkers();
    });
  }

  clearFilters() {
    this.filterTipo = '';
    this.filterTerritorio = '';
    this.filterMunicipio = '';
    this.municipios = [];
    this.filteredCentros = this.centros;
    this.cdr.detectChanges();
    this.updateMarkers();
  }

  selectCentro(centro: Centro) {
    this.selectedCentro = centro;
    this.selectedCentroId = centro.CCODIGO;
    this.centroSelected.emit(centro);
    
    if (this.map && this.mapInitialized) {
      this.map.flyTo({ center: [centro.LON, centro.LAT], zoom: 14 });
    }
    
    this.updateMarkers();
  }

  private initMap() {
    const container = document.getElementById('map-container');
    
    if (!container) return;

    if (typeof maplibregl === 'undefined') return;

    try {
      this.map = new maplibregl.Map({
        container: 'map-container',
        style: 'https://basemaps.cartocdn.com/gl/positron-gl-style/style.json',
        center: [-2.935, 43.263],
        zoom: 10
      });

      this.map.addControl(new maplibregl.NavigationControl());

      this.map.on('load', () => {
        this.mapInitialized = true;
        this.updateMarkers();
      });

    } catch (error) {
      console.error('Error initializing MapLibre:', error);
    }
  }

  private updateMarkers() {
    if (!this.map || !this.mapInitialized) return;

    this.markers.forEach(marker => marker.remove());
    this.markers = [];

    this.filteredCentros.forEach(centro => {
      const isSelected = centro.CCODIGO === this.selectedCentroId;
      
      const el = document.createElement('div');
      el.className = 'mapbox-marker';
      el.style.cssText = `
        width: 28px;
        height: 28px;
        background-color: ${isSelected ? '#c8102e' : '#1e3a5f'};
        border-radius: 50%;
        border: 3px solid white;
        box-shadow: 0 2px 6px rgba(0,0,0,0.3);
        cursor: pointer;
      `;

      const popup = new maplibregl.Popup({ offset: 25 }).setHTML(`
        <div style="font-family: 'Poppins', sans-serif; padding: 8px;">
          <strong style="color: #c8102e;">${centro.DNOMBRE}</strong><br>
          <small style="color: #666;">${centro.TIPO_CENTRO} - ${centro.DTITUC}</small><br>
          <small>${centro.DDOMICILIO || ''}</small><br>
          <small>${centro.DMUNI}, ${centro.DTERRE}</small>
        </div>
      `);

      const marker = new maplibregl.Marker({ element: el })
        .setLngLat([centro.LON, centro.LAT])
        .setPopup(popup)
        .addTo(this.map);

      el.addEventListener('click', () => {
        this.selectCentro(centro);
      });

      this.markers.push(marker);
    });

    if (this.filteredCentros.length > 0) {
      const bounds = new maplibregl.LngLatBounds();
      this.filteredCentros.forEach(c => bounds.extend([c.LON, c.LAT]));
      this.map.fitBounds(bounds, { padding: 50 });
    }
  }
}
