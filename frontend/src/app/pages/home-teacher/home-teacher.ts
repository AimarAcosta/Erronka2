// Panel del Profesor - Ve su horario, gestiona reuniones y busca alumnos
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ServicioAutenticacion } from '../../services/autenticacion';
import { ServicioHorarios, Horario, DiaSemana } from '../../services/horarios';
import { ServicioReuniones, Reunion } from '../../services/reuniones';
import { ServicioUsuarios, Usuario } from '../../services/usuarios';
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
  tablaHorario: any[][] = [];
  dias: DiaSemana[] = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES'];
  diasEus = ['ASTELEHENA', 'ASTEARTEA', 'ASTEAZKENA', 'OSTEGUNA', 'OSTIRALA'];
  horas = [1, 2, 3, 4, 5, 6];
  usuarioActual: any;

  // Reuniones
  misReuniones: Reunion[] = [];
  reunionesPendientes: Reunion[] = [];

  // Estadisticas
  totalReuniones: number = 0;
  contadorPendientes: number = 0;
  contadorAceptadas: number = 0;
  contadorClases: number = 0;

  // Creacion de reuniones
  alumnos: Usuario[] = [];
  mostrarFormularioReunion = false;
  mostrarSelectorCentroFlag = false;
  centroSeleccionado: Centro | null = null;
  nuevaReunion = {
    alumno_id: 0,
    titulo: '',
    asunto: '',
    id_centro: '15112'
  };

  // Busqueda de alumnos
  filtrosBusqueda = {
    nombre: '',
    apellidos: '',
    dni: '',
    ciclo: 0
  };
  ciclos: Ciclo[] = [];
  resultadosBusqueda: Usuario[] = [];
  estaBuscando = false;
  haBuscado = false;

  constructor(
    private servicioAuth: ServicioAutenticacion,
    private servicioHorarios: ServicioHorarios,
    private servicioReuniones: ServicioReuniones,
    private servicioUsuarios: ServicioUsuarios,
    private servicioCiclos: CiclosService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.usuarioActual = this.servicioAuth.obtenerUsuario();
    this.inicializarTablaVacia();
    this.cargarCiclos();
    this.cargarAlumnos();
    
    if (this.usuarioActual && this.usuarioActual.id) {
      this.cargarHorario(this.usuarioActual.id);
      this.cargarReuniones(this.usuarioActual.id);
    } else {
      this.cargarTodosHorarios();
    }
  }

  // Carga todos los alumnos para el dropdown de crear reunion
  cargarAlumnos() {
    this.servicioUsuarios.obtenerUsuarios().subscribe((usuarios) => {
      this.alumnos = usuarios.filter(u => u.tipo_id === 4);
    });
  }

  // Carga ciclos para el dropdown de busqueda
  cargarCiclos() {
    this.servicioCiclos.obtenerCiclos().subscribe((ciclos) => {
      this.ciclos = ciclos;
    });
  }

  cargarTodosHorarios() {
    this.servicioHorarios.obtenerHorarios().subscribe((horarios) => {
      this.contadorClases = horarios.length;
    });
  }

  // Crea tabla vacia 6x5
  inicializarTablaVacia() {
    for (let h = 0; h < 6; h++) {
      this.tablaHorario[h] = [];
      for (let d = 0; d < 5; d++) {
        this.tablaHorario[h][d] = {
          text: '',
          subtext: '',
          type: 'free',
          colorClass: '',
        };
      }
    }
  }

  // Carga horario del profesor
  cargarHorario(usuarioId: number) {
    this.servicioHorarios.obtenerHorarioProfesor(usuarioId).subscribe((horarios) => {
      this.contadorClases = horarios.length;
      horarios.forEach((horario) => {
        const indiceDia = this.dias.indexOf(horario.dia);
        const indiceHora = horario.hora - 1;

        if (indiceDia >= 0 && indiceHora >= 0 && indiceHora < 6) {
          const nombreModulo = horario.modulo?.nombre_eus || horario.modulo?.nombre || 'Modulua';
          this.tablaHorario[indiceHora][indiceDia] = {
            text: nombreModulo,
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
    this.servicioReuniones.obtenerReunionesProfesor(profesorId).subscribe((reuniones) => {
      this.misReuniones = reuniones;
      this.totalReuniones = reuniones.length;
      this.reunionesPendientes = reuniones.filter(r => r.estado === 'pendiente');
      this.contadorPendientes = this.reunionesPendientes.length;
      this.contadorAceptadas = reuniones.filter(r => r.estado === 'aceptada').length;
      this.cdr.detectChanges();
      
      // Marcar reuniones en el horario
      reuniones.forEach((reunion) => {
        if (reunion.fecha) {
          const fecha = new Date(reunion.fecha);
          const diaSemana = fecha.getDay();
          const hora = fecha.getHours();
          
          if (diaSemana >= 1 && diaSemana <= 5) {
            const indiceDia = diaSemana - 1;
            const indiceHora = hora - 8;
            
            if (indiceHora >= 0 && indiceHora < 6) {
              let color = '';
              switch (reunion.estado) {
                case 'pendiente': color = 'estado-pendiente'; break;
                case 'aceptada': color = 'estado-aceptada'; break;
                case 'denegada': color = 'estado-rechazada'; break;
                case 'conflicto': color = 'estado-conflicto'; break;
              }

              this.tablaHorario[indiceHora][indiceDia] = {
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
    this.servicioReuniones.actualizarReunion(reunion.id_reunion, { estado: 'aceptada' }).subscribe(() => {
      this.cargarReuniones(this.usuarioActual.id);
      alert('Reunion aceptada');
    });
  }

  // Rechaza una reunion
  rechazarReunion(reunion: Reunion) {
    this.servicioReuniones.actualizarReunion(reunion.id_reunion, { estado: 'denegada' }).subscribe(() => {
      this.cargarReuniones(this.usuarioActual.id);
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
    this.estaBuscando = true;
    this.haBuscado = true;
    
    const filtros: any = {};
    if (this.filtrosBusqueda.nombre.trim()) filtros.nombre = this.filtrosBusqueda.nombre.trim();
    if (this.filtrosBusqueda.apellidos.trim()) filtros.apellidos = this.filtrosBusqueda.apellidos.trim();
    if (this.filtrosBusqueda.dni.trim()) filtros.dni = this.filtrosBusqueda.dni.trim();
    if (this.filtrosBusqueda.ciclo > 0) filtros.ciclo = this.filtrosBusqueda.ciclo;
    
    this.servicioUsuarios.buscarAlumnos(filtros).subscribe({
      next: (alumnos) => {
        this.resultadosBusqueda = alumnos;
        this.estaBuscando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.resultadosBusqueda = [];
        this.estaBuscando = false;
      }
    });
  }

  // Limpia la busqueda
  limpiarBusqueda() {
    this.filtrosBusqueda = { nombre: '', apellidos: '', dni: '', ciclo: 0 };
    this.resultadosBusqueda = [];
    this.haBuscado = false;
  }

  obtenerFotoAlumno(usuario: Usuario): string {
    return this.servicioUsuarios.obtenerFotoConFallback(usuario);
  }

  onErrorFoto(evento: any) {
    evento.target.src = this.servicioUsuarios.obtenerUrlFotoDefecto();
  }

  // ===== METODOS PARA CREAR REUNIONES =====

  // Muestra u oculta el formulario de crear reunion
  mostrarFormulario() {
    this.mostrarFormularioReunion = !this.mostrarFormularioReunion;
    if (!this.mostrarFormularioReunion) {
      this.mostrarSelectorCentroFlag = false;
    }
  }

  // Muestra u oculta el selector de centro
  mostrarSelectorCentro() {
    this.mostrarSelectorCentroFlag = !this.mostrarSelectorCentroFlag;
  }

  // Cuando se selecciona un centro en el mapa
  onCentroSeleccionado(centro: Centro) {
    this.centroSeleccionado = centro;
    this.nuevaReunion.id_centro = centro.CCODIGO;
    this.mostrarSelectorCentroFlag = false;
  }

  // Envia la solicitud de reunion
  enviarSolicitud() {
    if (!this.nuevaReunion.alumno_id || !this.nuevaReunion.titulo) {
      alert('Por favor, selecciona un alumno y escribe un titulo');
      return;
    }

    const datosReunion: Partial<Reunion> = {
      alumno_id: this.nuevaReunion.alumno_id,
      profesor_id: this.usuarioActual.id,
      titulo: this.nuevaReunion.titulo,
      asunto: this.nuevaReunion.asunto,
      id_centro: this.nuevaReunion.id_centro,
      fecha: new Date(),
      estado: 'pendiente' as const
    };

    this.servicioReuniones.crearReunion(datosReunion).subscribe({
      next: () => {
        alert('Reunion creada correctamente');
        this.cargarReuniones(this.usuarioActual.id);
        this.mostrarFormularioReunion = false;
        this.nuevaReunion = { alumno_id: 0, titulo: '', asunto: '', id_centro: '15112' };
        this.centroSeleccionado = null;
      },
      error: (err) => {
        alert('Error al crear la reunion');
      }
    });
  }
}
