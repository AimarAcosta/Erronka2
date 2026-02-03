// Panel del estudiante - Solo puede ver su horario y reuniones (no puede crear)
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth';
import { HorariosService, Horario, WeekDay } from '../../services/schedule';
import { ReunionesService, Reunion } from '../../services/meetings';
import { MatriculacionesService, Matriculacion } from '../../services/matriculaciones';
import { TranslatePipe } from '../../pipes/translate.pipe';

@Component({
  selector: 'app-home-student',
  standalone: true,
  imports: [CommonModule, TranslatePipe],
  templateUrl: './home-student.html',
  styleUrls: ['./home-student.css']
})
export class HomeStudent implements OnInit {

  // Tabla del horario
  timeTable: any[][] = [];
  daysEus = ['ASTELEHENA', 'ASTEARTEA', 'ASTEAZKENA', 'OSTEGUNA', 'OSTIRALA'];
  days: WeekDay[] = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES'];
  currentUser: any;

  // Datos de reuniones
  myReuniones: Reunion[] = [];
  myMatriculacion: Matriculacion | null = null;

  // Contadores
  pendingCount: number = 0;
  acceptedCount: number = 0;

  constructor(
    private authService: AuthService,
    private horariosService: HorariosService,
    private reunionesService: ReunionesService,
    private matriculacionesService: MatriculacionesService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.obtenerUsuario();
    this.inicializarTablaVacia();
    this.cargarMatriculacion();

    if (this.currentUser && this.currentUser.id) {
      this.cargarMisReuniones();
    }
  }

  // Carga la matrícula y horario del estudiante
  cargarMatriculacion() {
    this.matriculacionesService.obtenerMatriculacionesAlumno(this.currentUser.id).subscribe(matriculaciones => {
      if (matriculaciones.length > 0) {
        this.myMatriculacion = matriculaciones[0];
        this.cargarHorarioPorCiclo(this.myMatriculacion.ciclo_id);
      } else {
        this.cargarHorario();
      }
    });
  }

  // Carga las reuniones del estudiante
  cargarMisReuniones() {
    this.reunionesService.obtenerReunionesAlumno(this.currentUser.id).subscribe(reuniones => {
      this.myReuniones = reuniones;
      this.pendingCount = reuniones.filter(r => r.estado === 'pendiente').length;
      this.acceptedCount = reuniones.filter(r => r.estado === 'aceptada').length;
      this.cdr.detectChanges();
    });
  }

  // Crea la tabla vacía del horario
  inicializarTablaVacia() {
    for (let h = 0; h < 6; h++) {
      this.timeTable[h] = [];
      for (let d = 0; d < 5; d++) {
        this.timeTable[h][d] = { text: '', colorClass: '' };
      }
    }
  }

  // Carga el horario general
  cargarHorario() {
    this.horariosService.obtenerHorarios().subscribe(horarios => {
      this.rellenarTablaHorario(horarios);
      this.cdr.detectChanges();
    });
  }

  // Carga el horario de un ciclo específico
  cargarHorarioPorCiclo(cicloId: number) {
    this.horariosService.obtenerHorarioCiclo(cicloId).subscribe(horarios => {
      this.rellenarTablaHorario(horarios);
      this.cdr.detectChanges();
    });
  }

  // Rellena la tabla con los datos del horario
  rellenarTablaHorario(horarios: Horario[]) {
    horarios.forEach(horario => {
      const dayIndex = this.days.indexOf(horario.dia);
      const hourIndex = horario.hora - 1;
      
      if (dayIndex >= 0 && hourIndex >= 0 && hourIndex < 6) {
        const moduloNombre = horario.modulo?.nombre_eus || horario.modulo?.nombre || 'Modulua';
        this.timeTable[hourIndex][dayIndex] = {
          text: moduloNombre,
          subtext: horario.aula || '',
          colorClass: 'celda-modulo'
        };
      }
    });
    this.cdr.detectChanges();
  }

  // Traduce el estado al euskera
  obtenerEstadoEus(estado: string): string {
    switch (estado) {
      case 'pendiente': return 'Onartzeke';
      case 'aceptada': return 'Onartuta';
      case 'denegada': return 'Ezeztatuta';
      default: return estado;
    }
  }
}