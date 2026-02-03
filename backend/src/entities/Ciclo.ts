// Entidad Ciclo - Tabla ciclos (DAW, DAM, ASIR, etc)
import { Entity, PrimaryGeneratedColumn, Column, OneToMany } from 'typeorm';
import { Modulo } from './Modulo';
import { Matriculacion } from './Matriculacion';

@Entity('ciclos')
export class Ciclo {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ length: 200 })
  nombre: string;

  @OneToMany(() => Modulo, (modulo) => modulo.ciclo)
  modulos: Modulo[];

  @OneToMany(() => Matriculacion, (matriculacion) => matriculacion.ciclo)
  matriculaciones: Matriculacion[];
}
