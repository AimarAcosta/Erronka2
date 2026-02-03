// Selector de Centro Educativo - Mapa con filtros para elegir ikastetxe
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
  
  @Input() selectedCentroId: string = '15112'; // Elorrieta por defecto
  @Output() centroSelected = new EventEmitter<Centro>();

  // Datos de centros
  centros: Centro[] = [];
  filteredCentros: Centro[] = [];
  selectedCentro: Centro | null = null;
  isLoading = true;

  // Filtros
  tipos: string[] = [];
  territorios: string[] = [];
  municipios: string[] = [];
  filterTipo: string = '';
  filterTerritorio: string = '';
  filterMunicipio: string = '';

  // Paginacion
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  // Mapa
  private map: any;
  private markers: any[] = [];
  private mapInitialized = false;

  constructor(
    private centrosService: CentrosService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.cargarCentros();
    this.cargarOpcionesFiltros();
  }

  ngAfterViewInit() {
    // El mapa se inicializa desde cargarCentros
  }

  // Intenta inicializar el mapa cuando el DOM este listo
  private intentarInicializarMapa(intentos = 0) {
    if (this.mapInitialized) return;
    if (intentos > 10) return;
    
    const container = document.getElementById('map-container');
    
    if (container && container.clientWidth > 0) {
      this.inicializarMapa();
    } else {
      setTimeout(() => this.intentarInicializarMapa(intentos + 1), 200);
    }
  }

  ngOnDestroy() {
    if (this.map) this.map.remove();
  }

  // Carga todos los centros
  cargarCentros() {
    this.isLoading = true;
    this.centrosService.obtenerCentros().subscribe({
      next: (centros) => {
        this.centros = centros;
        this.filteredCentros = centros;
        this.calcularTotalPaginas();
        this.isLoading = false;
        
        if (this.selectedCentroId) {
          this.selectedCentro = centros.find(c => c.CCODIGO === this.selectedCentroId) || null;
        }
        
        this.cdr.detectChanges();
        setTimeout(() => this.intentarInicializarMapa(), 100);
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Carga opciones de los dropdowns
  cargarOpcionesFiltros() {
    this.centrosService.obtenerTipos().subscribe(tipos => {
      this.tipos = tipos;
      this.cdr.detectChanges();
    });
    
    this.centrosService.obtenerTerritorios().subscribe(territorios => {
      this.territorios = territorios;
      this.cdr.detectChanges();
    });
  }

  // Cuando cambia el territorio, actualiza municipios
  onCambiarTerritorio() {
    this.filterMunicipio = '';
    this.centrosService.obtenerMunicipios(this.filterTerritorio).subscribe(municipios => {
      this.municipios = municipios;
      this.cdr.detectChanges();
    });
    this.aplicarFiltros();
  }

  // Aplica filtros
  aplicarFiltros() {
    this.centrosService.filtrarCentros({
      tipo: this.filterTipo,
      territorio: this.filterTerritorio,
      municipio: this.filterMunicipio
    }).subscribe(centros => {
      this.filteredCentros = centros;
      this.currentPage = 1; // Reset a primera pagina al filtrar
      this.calcularTotalPaginas();
      this.cdr.detectChanges();
      this.actualizarMarcadores();
    });
  }

  // Limpia filtros
  limpiarFiltros() {
    this.filterTipo = '';
    this.filterTerritorio = '';
    this.filterMunicipio = '';
    this.municipios = [];
    this.filteredCentros = this.centros;
    this.currentPage = 1;
    this.calcularTotalPaginas();
    this.cdr.detectChanges();
    this.actualizarMarcadores();
  }

  // Selecciona un centro
  seleccionarCentro(centro: Centro) {
    this.selectedCentro = centro;
    this.selectedCentroId = centro.CCODIGO;
    this.centroSelected.emit(centro);
    
    if (this.map && this.mapInitialized) {
      this.map.flyTo({ center: [centro.LON, centro.LAT], zoom: 14 });
    }
    
    this.actualizarMarcadores();
  }

  // Inicializa el mapa MapLibre
  private inicializarMapa() {
    const container = document.getElementById('map-container');
    if (!container) return;
    if (typeof maplibregl === 'undefined') return;

    try {
      this.map = new maplibregl.Map({
        container: 'map-container',
        style: 'https://basemaps.cartocdn.com/gl/positron-gl-style/style.json',
        center: [-2.935, 43.263], // Bilbao
        zoom: 10
      });

      this.map.addControl(new maplibregl.NavigationControl());

      this.map.on('load', () => {
        this.mapInitialized = true;
        this.actualizarMarcadores();
      });

    } catch (error) {
      // Error al inicializar mapa
    }
  }

  // Actualiza los marcadores del mapa
  private actualizarMarcadores() {
    if (!this.map || !this.mapInitialized) return;

    // Elimina marcadores antiguos
    this.markers.forEach(marker => marker.remove());
    this.markers = [];

    // Crea nuevos marcadores
    this.filteredCentros.forEach(centro => {
      const isSelected = centro.CCODIGO === this.selectedCentroId;
      
      const el = document.createElement('div');
      el.className = 'marcador-mapa';
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
        <div style="padding: 8px;">
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

      el.addEventListener('click', () => this.seleccionarCentro(centro));
      this.markers.push(marker);
    });

    // Ajusta el mapa para mostrar todos los centros
    if (this.filteredCentros.length > 0) {
      const bounds = new maplibregl.LngLatBounds();
      this.filteredCentros.forEach(c => bounds.extend([c.LON, c.LAT]));
      this.map.fitBounds(bounds, { padding: 50 });
    }
  }

  // ===== PAGINACION =====

  // Getter para obtener los centros de la pagina actual
  get centrosPaginados(): Centro[] {
    const inicio = (this.currentPage - 1) * this.itemsPerPage;
    const fin = inicio + this.itemsPerPage;
    return this.filteredCentros.slice(inicio, fin);
  }

  // Calcula el total de paginas
  calcularTotalPaginas() {
    this.totalPages = Math.ceil(this.filteredCentros.length / this.itemsPerPage);
    if (this.totalPages === 0) this.totalPages = 1;
  }

  // Ir a pagina anterior
  paginaAnterior() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  // Ir a pagina siguiente
  paginaSiguiente() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  // Ir a una pagina especifica
  irAPagina(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  // Genera array de numeros de pagina para mostrar
  obtenerNumerosPaginas(): number[] {
    const pages: number[] = [];
    const maxVisible = 5;
    let start = Math.max(1, this.currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(this.totalPages, start + maxVisible - 1);
    
    if (end - start + 1 < maxVisible) {
      start = Math.max(1, end - maxVisible + 1);
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  }
}
