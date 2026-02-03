// Panel del God (superadmin) - Puede crear, editar y eliminar cualquier usuario
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
import { UsersService, User, Tipo } from '../../services/users';
import { ReunionesService } from '../../services/meetings';
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
  studentCount: number = 0;
  teacherCount: number = 0;
  todayMeetings: number = 0;

  // Lista de usuarios
  users: User[] = [];
  filteredUsers: User[] = [];
  tipos: Tipo[] = [];
  searchTerm: string = '';

  // Control del formulario
  showForm: boolean = false; 
  isEditing: boolean = false; 
  userForm: Partial<User> = this.obtenerUsuarioVacio(); 

  constructor(
    private usersService: UsersService,
    private reunionesService: ReunionesService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.cargarDatos();
    this.cargarTipos();
  }

  // Carga usuarios y contadores
  cargarDatos() {
    this.usersService.obtenerUsuarios().subscribe(users => {
      this.users = users;
      this.filtrarUsuarios(); 
      this.studentCount = this.users.filter(u => u.tipo_id === 4).length;
      this.teacherCount = this.users.filter(u => u.tipo_id === 3).length;
      this.cdr.detectChanges();
    });
    
    this.reunionesService.contarReunionesHoy().subscribe(count => {
      this.todayMeetings = count;
      this.cdr.detectChanges();
    });
  }

  // Carga los tipos de usuario
  cargarTipos() {
    this.usersService.obtenerTipos().subscribe(tipos => {
      this.tipos = tipos;
      this.cdr.detectChanges();
    });
  }

  // Abre formulario para crear usuario
  abrirFormularioCrear() {
    this.isEditing = false;
    this.userForm = this.obtenerUsuarioVacio(); 
    this.showForm = true;
  }

  // Abre formulario para editar usuario
  abrirFormularioEditar(user: User) {
    this.isEditing = true;
    this.userForm = { ...user };
    this.showForm = true;
  }

  // Envía el formulario
  onSubmit() {
    if (this.isEditing && this.userForm.id) {
      this.usersService.actualizarUsuario(this.userForm.id, this.userForm).subscribe(() => {
        this.showForm = false;
        this.cargarDatos();
      });
    } else {
      this.usersService.crearUsuario(this.userForm).subscribe(() => {
        this.showForm = false;
        this.cargarDatos();
      });
    }
  }

  // Cierra el formulario
  cancelarFormulario() {
    this.showForm = false;
  }

  // Devuelve un usuario vacío
  obtenerUsuarioVacio(): Partial<User> {
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
    const user = this.users.find(u => u.id === id);
    if (user && user.tipo_id === 1) {
      alert('No se puede eliminar al God');
      return;
    }
    
    if (confirm('¿Eliminar este usuario?')) {
      this.usersService.eliminarUsuario(id).subscribe(success => {
        if (success) this.cargarDatos();
        else alert('Error al eliminar');
      });
    }
  }

  // Filtra usuarios por búsqueda
  filtrarUsuarios() {
    this.filteredUsers = this.users.filter(user => 
      (user.nombre?.toLowerCase() || '').includes(this.searchTerm.toLowerCase()) ||
      (user.apellidos?.toLowerCase() || '').includes(this.searchTerm.toLowerCase()) ||
      (user.username?.toLowerCase() || '').includes(this.searchTerm.toLowerCase())
    );
  }

  // Devuelve el nombre del rol
  obtenerNombreRol(tipoId: number): string {
    return this.usersService.obtenerNombreRol(tipoId);
  }
}