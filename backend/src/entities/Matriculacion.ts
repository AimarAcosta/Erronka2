// Entidad Matriculacion - Tabla matriculaciones (alumno-ciclo)
import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, JoinColumn } from 'typeorm';
import { User } from './User';
import { Ciclo } from './Ciclo';

@Entity('matriculaciones')
export class Matriculacion {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  alum_id: number;

  @Column()
  ciclo_id: number;

  @Column({ type: 'tinyint' })
  curso: number;

  @Column({ type: 'date' })
  fecha: Date;

  @ManyToOne(() => User, (user) => user.matriculaciones)
  @JoinColumn({ name: 'alum_id' })
  alumno: User;

  @ManyToOne(() => Ciclo, (ciclo) => ciclo.matriculaciones)
  @JoinColumn({ name: 'ciclo_id' })
  ciclo: Ciclo;
}
