// Panel del God (superadmin) - Puede crear, editar y eliminar cualquier usuario
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
import { ServicioUsuarios, Usuario, Tipo } from '../../services/usuarios';
import { ServicioReuniones } from '../../services/reuniones';
import { TranslatePipe } from '../../pipes/translate.pipe';

@Component({
  selector: 'app-home-god',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe], 
  templateUrl: './home-god.html',
  styleUrls: ['./home-god.css']
})
export class HomeGod implements OnInit {
  
  // Contadores para las tarjetas
  contadorAlumnos: number = 0;
  contadorProfesores: number = 0;
  reunionesHoy: number = 0;

  // Lista de usuarios
  usuarios: Usuario[] = [];
  usuariosFiltrados: Usuario[] = [];
  tipos: Tipo[] = [];
  terminoBusqueda: string = '';

  // Control del formulario
  mostrarFormulario: boolean = false; 
  estaEditando: boolean = false; 
  formularioUsuario: Partial<Usuario> = this.obtenerUsuarioVacio(); 

  constructor(
    private servicioUsuarios: ServicioUsuarios,
    private servicioReuniones: ServicioReuniones,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.cargarDatos();
    this.cargarTipos();
  }

  // Carga usuarios y contadores
  cargarDatos() {
    this.servicioUsuarios.obtenerUsuarios().subscribe(usuarios => {
      this.usuarios = usuarios;
      this.filtrarUsuarios(); 
      this.contadorAlumnos = this.usuarios.filter(u => u.tipo_id === 4).length;
      this.contadorProfesores = this.usuarios.filter(u => u.tipo_id === 3).length;
      this.cdr.detectChanges();
    });
    
    this.servicioReuniones.contarReunionesHoy().subscribe(contador => {
      this.reunionesHoy = contador;
      this.cdr.detectChanges();
    });
  }

  // Carga los tipos de usuario
  cargarTipos() {
    this.servicioUsuarios.obtenerTipos().subscribe(tipos => {
      this.tipos = tipos;
      this.cdr.detectChanges();
    });
  }

  // Abre formulario para crear usuario
  abrirFormularioCrear() {
    this.estaEditando = false;
    this.formularioUsuario = this.obtenerUsuarioVacio(); 
    this.mostrarFormulario = true;
  }

  // Abre formulario para editar usuario
  abrirFormularioEditar(usuario: Usuario) {
    this.estaEditando = true;
    this.formularioUsuario = { ...usuario };
    this.mostrarFormulario = true;
  }

  // Envía el formulario
  onSubmit() {
    if (this.estaEditando && this.formularioUsuario.id) {
      this.servicioUsuarios.actualizarUsuario(this.formularioUsuario.id, this.formularioUsuario).subscribe(() => {
        this.mostrarFormulario = false;
        this.cargarDatos();
      });
    } else {
      this.servicioUsuarios.crearUsuario(this.formularioUsuario).subscribe(() => {
        this.mostrarFormulario = false;
        this.cargarDatos();
      });
    }
  }

  // Cierra el formulario
  cancelarFormulario() {
    this.mostrarFormulario = false;
  }

  // Devuelve un usuario vacío
  obtenerUsuarioVacio(): Partial<Usuario> {
    return {
      username: '',
      password: '123456',
      nombre: '',
      apellidos: '',
      email: '',
      tipo_id: 4
    };
  }

  // Elimina un usuario (no se puede eliminar al God)
  onEliminarUsuario(id: number) {
    const usuario = this.usuarios.find(u => u.id === id);
    if (usuario && usuario.tipo_id === 1) {
      alert('No se puede eliminar al God');
      return;
    }
    
    if (confirm('¿Eliminar este usuario?')) {
      this.servicioUsuarios.eliminarUsuario(id).subscribe(exito => {
        if (exito) this.cargarDatos();
        else alert('Error al eliminar');
      });
    }
  }

  // Filtra usuarios por búsqueda
  filtrarUsuarios() {
    this.usuariosFiltrados = this.usuarios.filter(usuario => 
      (usuario.nombre?.toLowerCase() || '').includes(this.terminoBusqueda.toLowerCase()) ||
      (usuario.apellidos?.toLowerCase() || '').includes(this.terminoBusqueda.toLowerCase()) ||
      (usuario.username?.toLowerCase() || '').includes(this.terminoBusqueda.toLowerCase())
    );
  }

  // Devuelve el nombre del rol
  obtenerNombreRol(tipoId: number): string {
    return this.servicioUsuarios.obtenerNombreRol(tipoId);
  }
}