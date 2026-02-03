// Entidad Reunion - Tabla reuniones
import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, JoinColumn } from 'typeorm';
import { User } from './User';

export type ReunionEstado = 'pendiente' | 'aceptada' | 'denegada' | 'conflicto';
export type ReunionEstadoEus = 'onartzeke' | 'onartuta' | 'ezeztatuta' | 'gatazka';

@Entity('reuniones')
export class Reunion {
  @PrimaryGeneratedColumn()
  id_reunion: number;

  @Column({
    type: 'enum',
    enum: ['pendiente', 'aceptada', 'denegada', 'conflicto'],
    default: 'pendiente',
  })
  estado: ReunionEstado;

  @Column({
    type: 'enum',
    enum: ['onartzeke', 'onartuta', 'ezeztatuta', 'gatazka'],
    nullable: true,
  })
  estado_eus: ReunionEstadoEus;

  @Column({ nullable: true })
  profesor_id: number;

  @Column({ nullable: true })
  alumno_id: number;

  @Column({ length: 20, default: '15112' })
  id_centro: string;

  @Column({ length: 150, nullable: true })
  titulo: string;

  @Column({ length: 200, nullable: true })
  asunto: string;

  @Column({ length: 20, nullable: true })
  aula: string;

  @Column({ type: 'datetime', nullable: true })
  fecha: Date;

  @Column({ type: 'datetime', default: () => 'CURRENT_TIMESTAMP' })
  created_at: Date;

  @Column({ type: 'datetime', default: () => 'CURRENT_TIMESTAMP', onUpdate: 'CURRENT_TIMESTAMP' })
  updated_at: Date;

  @ManyToOne(() => User, (user) => user.reunionesAsProfesor)
  @JoinColumn({ name: 'profesor_id' })
  profesor: User;

  @ManyToOne(() => User, (user) => user.reunionesAsAlumno)
  @JoinColumn({ name: 'alumno_id' })
  alumno: User;
}
