// Entidad Usuario - Tabla users
import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, OneToMany, JoinColumn } from 'typeorm';
import { Tipo } from './Tipo';
import { Horario } from './Horario';
import { Reunion } from './Reunion';
import { Matriculacion } from './Matriculacion';

@Entity('users')
export class User {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ unique: true, length: 100 })
  email: string;

  @Column({ unique: true, length: 50 })
  username: string;

  @Column({ length: 255 })
  password: string;

  @Column({ length: 50, nullable: true })
  nombre: string;

  @Column({ length: 100, nullable: true })
  apellidos: string;

  @Column({ length: 20, nullable: true })
  dni: string;

  @Column({ length: 255, nullable: true })
  direccion: string;

  @Column({ length: 20, nullable: true })
  telefono1: string;

  @Column({ length: 20, nullable: true })
  telefono2: string;

  @Column()
  tipo_id: number;

  @Column({ length: 255, nullable: true })
  argazkia_url: string;

  @Column({ type: 'datetime', default: () => 'CURRENT_TIMESTAMP' })
  created_at: Date;

  @Column({ type: 'datetime', default: () => 'CURRENT_TIMESTAMP', onUpdate: 'CURRENT_TIMESTAMP' })
  updated_at: Date;

  @ManyToOne(() => Tipo, (tipo) => tipo.users)
  @JoinColumn({ name: 'tipo_id' })
  tipo: Tipo;

  @OneToMany(() => Horario, (horario) => horario.profesor)
  horarios: Horario[];

  @OneToMany(() => Reunion, (reunion) => reunion.profesor)
  reunionesAsProfesor: Reunion[];

  @OneToMany(() => Reunion, (reunion) => reunion.alumno)
  reunionesAsAlumno: Reunion[];

  @OneToMany(() => Matriculacion, (matriculacion) => matriculacion.alumno)
  matriculaciones: Matriculacion[];
}
