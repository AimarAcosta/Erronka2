// Entidad Horario - Tabla horarios
import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, JoinColumn } from 'typeorm';
import { User } from './User';
import { Modulo } from './Modulo';

export type WeekDay = 'LUNES' | 'MARTES' | 'MIERCOLES' | 'JUEVES' | 'VIERNES';

@Entity('horarios')
export class Horario {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({
    type: 'enum',
    enum: ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES'],
  })
  dia: WeekDay;

  @Column({ type: 'tinyint' })
  hora: number;

  @Column()
  profe_id: number;

  @Column()
  modulo_id: number;

  @Column({ length: 50, nullable: true })
  aula: string;

  @Column({ length: 255, nullable: true })
  observaciones: string;

  @Column({ type: 'datetime', default: () => 'CURRENT_TIMESTAMP' })
  created_at: Date;

  @Column({ type: 'datetime', default: () => 'CURRENT_TIMESTAMP', onUpdate: 'CURRENT_TIMESTAMP' })
  updated_at: Date;

  @ManyToOne(() => User, (user) => user.horarios)
  @JoinColumn({ name: 'profe_id' })
  profesor: User;

  @ManyToOne(() => Modulo, (modulo) => modulo.horarios)
  @JoinColumn({ name: 'modulo_id' })
  modulo: Modulo;
}
