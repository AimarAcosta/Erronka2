// Panel del estudiante - Solo puede ver su horario y reuniones (no puede crear)
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServicioAutenticacion } from '../../services/autenticacion';
import { ServicioHorarios, Horario, DiaSemana } from '../../services/horarios';
import { ServicioReuniones, Reunion } from '../../services/reuniones';
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
  tablaHorario: any[][] = [];
  diasEus = ['ASTELEHENA', 'ASTEARTEA', 'ASTEAZKENA', 'OSTEGUNA', 'OSTIRALA'];
  dias: DiaSemana[] = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES'];
  usuarioActual: any;

  // Datos de reuniones
  misReuniones: Reunion[] = [];
  miMatriculacion: Matriculacion | null = null;

  // Contadores
  contadorPendientes: number = 0;
  contadorAceptadas: number = 0;

  constructor(
    private servicioAuth: ServicioAutenticacion,
    private servicioHorarios: ServicioHorarios,
    private servicioReuniones: ServicioReuniones,
    private servicioMatriculaciones: MatriculacionesService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.usuarioActual = this.servicioAuth.obtenerUsuario();
    this.inicializarTablaVacia();
    this.cargarMatriculacion();

    if (this.usuarioActual && this.usuarioActual.id) {
      this.cargarMisReuniones();
    }
  }

  // Carga la matrícula y horario del estudiante
  cargarMatriculacion() {
    this.servicioMatriculaciones.obtenerMatriculacionesAlumno(this.usuarioActual.id).subscribe(matriculaciones => {
      if (matriculaciones.length > 0) {
        this.miMatriculacion = matriculaciones[0];
        this.cargarHorarioPorCiclo(this.miMatriculacion.ciclo_id);
      } else {
        this.cargarHorario();
      }
    });
  }

  // Carga las reuniones del estudiante
  cargarMisReuniones() {
    this.servicioReuniones.obtenerReunionesAlumno(this.usuarioActual.id).subscribe(reuniones => {
      this.misReuniones = reuniones;
      this.contadorPendientes = reuniones.filter(r => r.estado === 'pendiente').length;
      this.contadorAceptadas = reuniones.filter(r => r.estado === 'aceptada').length;
      this.cdr.detectChanges();
    });
  }

  // Crea la tabla vacía del horario
  inicializarTablaVacia() {
    for (let h = 0; h < 6; h++) {
      this.tablaHorario[h] = [];
      for (let d = 0; d < 5; d++) {
        this.tablaHorario[h][d] = { text: '', colorClass: '' };
      }
    }
  }

  // Carga el horario general
  cargarHorario() {
    this.servicioHorarios.obtenerHorarios().subscribe(horarios => {
      this.rellenarTablaHorario(horarios);
      this.cdr.detectChanges();
    });
  }

  // Carga el horario de un ciclo específico
  cargarHorarioPorCiclo(cicloId: number) {
    this.servicioHorarios.obtenerHorarioCiclo(cicloId).subscribe(horarios => {
      this.rellenarTablaHorario(horarios);
      this.cdr.detectChanges();
    });
  }

  // Rellena la tabla con los datos del horario
  rellenarTablaHorario(horarios: Horario[]) {
    horarios.forEach(horario => {
      const indiceDia = this.dias.indexOf(horario.dia);
      const indiceHora = horario.hora - 1;
      
      if (indiceDia >= 0 && indiceHora >= 0 && indiceHora < 6) {
        const nombreModulo = horario.modulo?.nombre_eus || horario.modulo?.nombre || 'Modulua';
        this.tablaHorario[indiceHora][indiceDia] = {
          text: nombreModulo,
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