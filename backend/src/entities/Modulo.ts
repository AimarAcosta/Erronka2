// Entidad Modulo - Tabla modulos (asignaturas)
import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, JoinColumn, OneToMany } from 'typeorm';
import { Ciclo } from './Ciclo';
import { Horario } from './Horario';

@Entity('modulos')
export class Modulo {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ length: 200 })
  nombre: string;

  @Column({ length: 200, nullable: true })
  nombre_eus: string;

  @Column({ type: 'int' })
  horas: number;

  @Column()
  ciclo_id: number;

  @Column({ type: 'tinyint' })
  curso: number;

  @ManyToOne(() => Ciclo, (ciclo) => ciclo.modulos)
  @JoinColumn({ name: 'ciclo_id' })
  ciclo: Ciclo;

  @OneToMany(() => Horario, (horario) => horario.modulo)
  horarios: Horario[];
}
