// Entidad Tipo - Tabla tipos (God, Admin, Teacher, Student)
import { Entity, PrimaryGeneratedColumn, Column, OneToMany } from "typeorm";
import { User } from "./User";

@Entity("tipos")
export class Tipo {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ length: 50, unique: true })
  name: string;

  @Column({ length: 50, nullable: true })
  name_eu: string;

  @OneToMany(() => User, (user) => user.tipo)
  users: User[];
}
