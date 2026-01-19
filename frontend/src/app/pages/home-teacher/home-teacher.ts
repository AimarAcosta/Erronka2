import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';
import { HorariosService, Horario, WeekDay } from '../../services/schedule';
import { ReunionesService, Reunion } from '../../services/meetings';
import { UsersService, User } from '../../services/users';
import { CiclosService, Ciclo } from '../../services/ciclos';
import { TranslatePipe } from '../../pipes/translate.pipe';

@Component({
  selector: 'app-home-teacher',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe],
  templateUrl: './home-teacher.html',
  styleUrls: ['./home-teacher.css'],
})
export class HomeTeacher implements OnInit {
  timeTable: any[][] = [];

  days: WeekDay[] = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES'];

  daysEus = ['ASTELEHENA', 'ASTEARTEA', 'ASTEAZKENA', 'OSTEGUNA', 'OSTIRALA'];

  hours = [1, 2, 3, 4, 5, 6];
  currentUser: any;
  myReuniones: Reunion[] = [];
  pendingReuniones: Reunion[] = [];

  totalReuniones: number = 0;
  pendingCount: number = 0;
  acceptedCount: number = 0;
  classCount: number = 0;

  // Búsqueda de estudiantes
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
    this.currentUser = this.authService.getUser();
    this.initializeEmptyTable();
    this.loadCiclos();
    
    if (this.currentUser && this.currentUser.id) {
      this.loadScheduleData(this.currentUser.id);
      this.loadReuniones(this.currentUser.id);
    } else {
      this.loadAllHorarios();
    }
  }

  loadCiclos() {
    this.ciclosService.getCiclos().subscribe((ciclos) => {
      this.ciclos = ciclos;
    });
  }

  loadAllHorarios() {
    this.horariosService.getHorarios().subscribe((horarios) => {
      this.classCount = horarios.length;
    });
  }

  initializeEmptyTable() {
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

  loadScheduleData(userId: number) {
    this.horariosService.getHorarioProfesor(userId).subscribe((horarios) => {
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

  loadReuniones(profesorId: number) {
    this.reunionesService.getReunionesProfesor(profesorId).subscribe((reuniones) => {
      this.myReuniones = reuniones;
      this.totalReuniones = reuniones.length;
      this.pendingReuniones = reuniones.filter(r => r.estado === 'pendiente');
      this.pendingCount = this.pendingReuniones.length;
      this.acceptedCount = reuniones.filter(r => r.estado === 'aceptada').length;
      this.cdr.detectChanges();
      
      reuniones.forEach((reunion) => {
        if (reunion.fecha) {
          const fecha = new Date(reunion.fecha);
          const dayOfWeek = fecha.getDay(); // 0=Sunday, 1=Monday...
          const hour = fecha.getHours();
          
          if (dayOfWeek >= 1 && dayOfWeek <= 5) {
            const dayIndex = dayOfWeek - 1;
            const hourIndex = hour - 8;
            
            if (hourIndex >= 0 && hourIndex < 6) {
              let color = '';
              switch (reunion.estado) {
                case 'pendiente':
                  color = 'status-pending';
                  break;
                case 'aceptada':
                  color = 'status-approved';
                  break;
                case 'denegada':
                  color = 'status-rejected';
                  break;
                case 'conflicto':
                  color = 'status-conflict';
                  break;
              }

              this.timeTable[hourIndex][dayIndex] = {
                text: 'BILERA', 
                subtext: reunion.titulo || 'Izenbururik gabe',
                type: 'meeting',
                colorClass: color,
              };
            }
          }
        }
      });
    });
  }

  acceptReunion(reunion: Reunion) {
    this.reunionesService.updateReunion(reunion.id_reunion, { estado: 'aceptada' }).subscribe(() => {
      this.loadReuniones(this.currentUser.id);
      alert('Bilera onartuta! (Reunión aceptada)');
    });
  }

  rejectReunion(reunion: Reunion) {
    this.reunionesService.updateReunion(reunion.id_reunion, { estado: 'denegada' }).subscribe(() => {
      this.loadReuniones(this.currentUser.id);
      alert('Bilera ezeztatuta (Reunión rechazada)');
    });
  }

  getEstadoEus(estado: string): string {
    switch (estado) {
      case 'pendiente': return 'Onartzeke';
      case 'aceptada': return 'Onartuta';
      case 'denegada': return 'Ezeztatuta';
      case 'conflicto': return 'Gatazka';
      default: return estado;
    }
  }

  // Métodos de búsqueda de estudiantes
  searchStudents() {
    this.isSearching = true;
    this.hasSearched = true;
    
    const filters: any = {};
    if (this.searchFilters.nombre.trim()) filters.nombre = this.searchFilters.nombre.trim();
    if (this.searchFilters.apellidos.trim()) filters.apellidos = this.searchFilters.apellidos.trim();
    if (this.searchFilters.dni.trim()) filters.dni = this.searchFilters.dni.trim();
    if (this.searchFilters.ciclo > 0) filters.ciclo = this.searchFilters.ciclo;
    
    this.usersService.searchStudents(filters).subscribe({
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

  clearSearch() {
    this.searchFilters = {
      nombre: '',
      apellidos: '',
      dni: '',
      ciclo: 0
    };
    this.searchResults = [];
    this.hasSearched = false;
  }

  getStudentPhoto(user: User): string {
    return this.usersService.getPhotoWithFallback(user);
  }

  onPhotoError(event: any) {
    event.target.src = this.usersService.getDefaultPhotoUrl();
  }
}
