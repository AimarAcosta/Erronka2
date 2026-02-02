import 'reflect-metadata';
import { DataSource } from 'typeorm';
import { User } from './entities/User';
import { Tipo } from './entities/Tipo';
import { Ciclo } from './entities/Ciclo';
import { Modulo } from './entities/Modulo';
import { Horario } from './entities/Horario';
import { Reunion } from './entities/Reunion';
import { Matriculacion } from './entities/Matriculacion';
import dotenv from 'dotenv';

dotenv.config();

export const AppDataSource = new DataSource({
  type: 'mysql',
  host: '10.5.104.124',
  port: 3306,
  username: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  database: process.env.DB_NAME || 'eduelorrieta',
  synchronize: false, // La BD ya existe, no sincronizar
  logging: true,
  entities: [User, Tipo, Ciclo, Modulo, Horario, Reunion, Matriculacion],
  migrations: [],
  subscribers: [],
});
