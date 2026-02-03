// Panel del Profesor - Ve su horario, gestiona reuniones y busca alumnos
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';
import { HorariosService, Horario, WeekDay } from '../../services/schedule';
import { ReunionesService, Reunion } from '../../services/meetings';
import { UsersService, User } from '../../services/users';
import { CiclosService, Ciclo } from '../../services/ciclos';
import { TranslatePipe } from '../../pipes/translate.pipe';
import { CentroSelector } from '../../shared/centro-selector/centro-selector';
import { Centro } from '../../services/centros';

@Component({
  selector: 'app-home-teacher',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe, CentroSelector],
  templateUrl: './home-teacher.html',
  styleUrls: ['./home-teacher.css'],
})
export class HomeTeacher implements OnInit {
  
  // Horario
  timeTable: any[][] = [];
  days: WeekDay[] = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES'];
  daysEus = ['ASTELEHENA', 'ASTEARTEA', 'ASTEAZKENA', 'OSTEGUNA', 'OSTIRALA'];
  hours = [1, 2, 3, 4, 5, 6];
  currentUser: any;

  // Reuniones
  myReuniones: Reunion[] = [];
  pendingReuniones: Reunion[] = [];

  // Estadisticas
  totalReuniones: number = 0;
  pendingCount: number = 0;
  acceptedCount: number = 0;
  classCount: number = 0;

  // Creacion de reuniones
  students: User[] = [];
  showRequestForm = false;
  showCentroSelector = false;
  selectedCentro: Centro | null = null;
  newReunion = {
    alumno_id: 0,
    titulo: '',
    asunto: '',
    id_centro: '15112'
  };

  // Busqueda de alumnos
  searchFilters = {
    nombre: '',
    apellidos: '',
    dni: '',
    ciclo: 0
  };
  ciclos: Ciclo[] = [];
  searchResults: User[] = [];
  isSearching = false;
  hasSearched = false;

  constructor(
    private authService: AuthService,
    private horariosService: HorariosService,
    private reunionesService: ReunionesService,
    private usersService: UsersService,
    private ciclosService: CiclosService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.obtenerUsuario();
    this.inicializarTablaVacia();
    this.cargarCiclos();
    this.cargarAlumnos();
    
    if (this.currentUser && this.currentUser.id) {
      this.cargarHorario(this.currentUser.id);
      this.cargarReuniones(this.currentUser.id);
    } else {
      this.cargarTodosHorarios();
    }
  }

  // Carga todos los alumnos para el dropdown de crear reunion
  cargarAlumnos() {
    this.usersService.obtenerUsuarios().subscribe((users) => {
      this.students = users.filter(u => u.tipo?.name === 'alumno');
    });
  }

  // Carga ciclos para el dropdown de busqueda
  cargarCiclos() {
    this.ciclosService.obtenerCiclos().subscribe((ciclos) => {
      this.ciclos = ciclos;
    });
  }

  cargarTodosHorarios() {
    this.horariosService.obtenerHorarios().subscribe((horarios) => {
      this.classCount = horarios.length;
    });
  }

  // Crea tabla vacia 6x5
  inicializarTablaVacia() {
    for (let h = 0; h < 6; h++) {
      this.timeTable[h] = [];
      for (let d = 0; d < 5; d++) {
        this.timeTable[h][d] = {
          text: '',
          subtext: '',
          type: 'free',
          colorClass: '',
        };
      }
    }
  }

  // Carga horario del profesor
  cargarHorario(userId: number) {
    this.horariosService.obtenerHorarioProfesor(userId).subscribe((horarios) => {
      this.classCount = horarios.length;
      horarios.forEach((horario) => {
        const dayIndex = this.days.indexOf(horario.dia);
        const hourIndex = horario.hora - 1;

        if (dayIndex >= 0 && hourIndex >= 0 && hourIndex < 6) {
          const moduloNombre = horario.modulo?.nombre_eus || horario.modulo?.nombre || 'Modulua';
          this.timeTable[hourIndex][dayIndex] = {
            text: moduloNombre,
            subtext: horario.aula || '',
            type: 'class',
            colorClass: 'bg-light',
          };
        }
      });
      this.cdr.detectChanges();
    });
  }

  // Carga reuniones y las marca en el horario
  cargarReuniones(profesorId: number) {
    this.reunionesService.obtenerReunionesProfesor(profesorId).subscribe((reuniones) => {
      this.myReuniones = reuniones;
      this.totalReuniones = reuniones.length;
      this.pendingReuniones = reuniones.filter(r => r.estado === 'pendiente');
      this.pendingCount = this.pendingReuniones.length;
      this.acceptedCount = reuniones.filter(r => r.estado === 'aceptada').length;
      this.cdr.detectChanges();
      
      // Marcar reuniones en el horario
      reuniones.forEach((reunion) => {
        if (reunion.fecha) {
          const fecha = new Date(reunion.fecha);
          const dayOfWeek = fecha.getDay();
          const hour = fecha.getHours();
          
          if (dayOfWeek >= 1 && dayOfWeek <= 5) {
            const dayIndex = dayOfWeek - 1;
            const hourIndex = hour - 8;
            
            if (hourIndex >= 0 && hourIndex < 6) {
              let color = '';
              switch (reunion.estado) {
                case 'pendiente': color = 'estado-pendiente'; break;
                case 'aceptada': color = 'estado-aceptada'; break;
                case 'denegada': color = 'estado-rechazada'; break;
                case 'conflicto': color = 'estado-conflicto'; break;
              }

              this.timeTable[hourIndex][dayIndex] = {
                text: 'BILERA', 
                subtext: reunion.titulo || 'Sin titulo',
                type: 'meeting',
                colorClass: color,
              };
            }
          }
        }
      });
    });
  }

  // Acepta una reunion
  aceptarReunion(reunion: Reunion) {
    this.reunionesService.actualizarReunion(reunion.id_reunion, { estado: 'aceptada' }).subscribe(() => {
      this.cargarReuniones(this.currentUser.id);
      alert('Reunion aceptada');
    });
  }

  // Rechaza una reunion
  rechazarReunion(reunion: Reunion) {
    this.reunionesService.actualizarReunion(reunion.id_reunion, { estado: 'denegada' }).subscribe(() => {
      this.cargarReuniones(this.currentUser.id);
      alert('Reunion rechazada');
    });
  }

  obtenerEstadoEus(estado: string): string {
    switch (estado) {
      case 'pendiente': return 'Onartzeke';
      case 'aceptada': return 'Onartuta';
      case 'denegada': return 'Ezeztatuta';
      case 'conflicto': return 'Gatazka';
      default: return estado;
    }
  }

  // Busca alumnos por filtros
  buscarAlumnos() {
    this.isSearching = true;
    this.hasSearched = true;
    
    const filters: any = {};
    if (this.searchFilters.nombre.trim()) filters.nombre = this.searchFilters.nombre.trim();
    if (this.searchFilters.apellidos.trim()) filters.apellidos = this.searchFilters.apellidos.trim();
    if (this.searchFilters.dni.trim()) filters.dni = this.searchFilters.dni.trim();
    if (this.searchFilters.ciclo > 0) filters.ciclo = this.searchFilters.ciclo;
    
    this.usersService.buscarAlumnos(filters).subscribe({
      next: (students) => {
        this.searchResults = students;
        this.isSearching = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.searchResults = [];
        this.isSearching = false;
      }
    });
  }

  // Limpia la busqueda
  limpiarBusqueda() {
    this.searchFilters = { nombre: '', apellidos: '', dni: '', ciclo: 0 };
    this.searchResults = [];
    this.hasSearched = false;
  }

  obtenerFotoAlumno(user: User): string {
    return this.usersService.obtenerFotoConFallback(user);
  }

  onErrorFoto(event: any) {
    event.target.src = this.usersService.obtenerUrlFotoDefecto();
  }

  // ===== METODOS PARA CREAR REUNIONES =====

  // Muestra u oculta el formulario de crear reunion
  mostrarFormulario() {
    this.showRequestForm = !this.showRequestForm;
    if (!this.showRequestForm) {
      this.showCentroSelector = false;
    }
  }

  // Muestra u oculta el selector de centro
  mostrarSelectorCentro() {
    this.showCentroSelector = !this.showCentroSelector;
  }

  // Cuando se selecciona un centro en el mapa
  onCentroSeleccionado(centro: Centro) {
    this.selectedCentro = centro;
    this.newReunion.id_centro = centro.CCODIGO;
    this.showCentroSelector = false;
  }

  // Envia la solicitud de reunion
  enviarSolicitud() {
    if (!this.newReunion.alumno_id || !this.newReunion.titulo) {
      alert('Por favor, selecciona un alumno y escribe un titulo');
      return;
    }

    const reunionData: Partial<Reunion> = {
      alumno_id: this.newReunion.alumno_id,
      profesor_id: this.currentUser.id,
      titulo: this.newReunion.titulo,
      asunto: this.newReunion.asunto,
      id_centro: this.newReunion.id_centro,
      fecha: new Date(),
      estado: 'pendiente' as const
    };

    this.reunionesService.crearReunion(reunionData).subscribe({
      next: () => {
        alert('Reunion creada correctamente');
        this.cargarReuniones(this.currentUser.id);
        this.showRequestForm = false;
        this.newReunion = { alumno_id: 0, titulo: '', asunto: '', id_centro: '15112' };
        this.selectedCentro = null;
      },
      error: (err) => {
        alert('Error al crear la reunion');
      }
    });
  }
}
