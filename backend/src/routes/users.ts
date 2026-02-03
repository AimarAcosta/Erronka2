// Rutas de usuarios - Login, CRUD y busquedas
import { Router } from 'express';
import { AppDataSource } from '../data-source';
import { User } from '../entities/User';
import { Tipo } from '../entities/Tipo';

const router = Router();
const userRepository = () => AppDataSource.getRepository(User);
const tipoRepository = () => AppDataSource.getRepository(Tipo);

router.get('/tipos/all', async (req, res) => {
  try {
    const tipos = await tipoRepository().find({ order: { id: 'ASC' } });
    res.json(tipos);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener tipos' });
  }
});

router.get('/count/students', async (req, res) => {
  try {
    const count = await userRepository().count({ where: { tipo_id: 4 } });
    res.json({ count });
  } catch (error) {
    res.status(500).json({ error: 'Error al contar estudiantes' });
  }
});

router.get('/count/teachers', async (req, res) => {
  try {
    const count = await userRepository().count({ where: { tipo_id: 3 } });
    res.json({ count });
  } catch (error) {
    res.status(500).json({ error: 'Error al contar profesores' });
  }
});

router.get('/count/all', async (req, res) => {
  try {
    const students = await userRepository().count({ where: { tipo_id: 4 } });
    const teachers = await userRepository().count({ where: { tipo_id: 3 } });
    const admins = await userRepository().count({ where: { tipo_id: 2 } });
    const total = await userRepository().count();
    res.json({ students, teachers, admins, total });
  } catch (error) {
    res.status(500).json({ error: 'Error al contar usuarios' });
  }
});

router.get('/', async (req, res) => {
  try {
    const users = await userRepository().find({
      relations: ['tipo'],
      order: { id: 'ASC' },
    });
    res.json(users);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener usuarios' });
  }
});

router.get('/role/:tipoId', async (req, res) => {
  try {
    const users = await userRepository().find({
      where: { tipo_id: parseInt(req.params.tipoId) },
      relations: ['tipo'],
      order: { nombre: 'ASC' },
    });
    res.json(users);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener usuarios por tipo' });
  }
});

router.get('/search/:query', async (req, res) => {
  try {
    const query = req.params.query.toLowerCase();
    const users = await userRepository()
      .createQueryBuilder('user')
      .leftJoinAndSelect('user.tipo', 'tipo')
      .where('LOWER(user.nombre) LIKE :query', { query: `%${query}%` })
      .orWhere('LOWER(user.apellidos) LIKE :query', { query: `%${query}%` })
      .orWhere('LOWER(user.username) LIKE :query', { query: `%${query}%` })
      .orWhere('LOWER(user.email) LIKE :query', { query: `%${query}%` })
      .getMany();
    res.json(users);
  } catch (error) {
    res.status(500).json({ error: 'Error al buscar usuarios' });
  }
});

router.get('/students/search', async (req, res) => {
  try {
    const { nombre, apellidos, dni, ciclo } = req.query;
    
    let queryBuilder = userRepository()
      .createQueryBuilder('user')
      .leftJoinAndSelect('user.tipo', 'tipo')
      .leftJoinAndSelect('user.matriculaciones', 'matriculacion')
      .leftJoinAndSelect('matriculacion.ciclo', 'ciclo')
      .where('user.tipo_id = :tipoId', { tipoId: 4 });

    if (nombre) {
      queryBuilder = queryBuilder.andWhere('LOWER(user.nombre) LIKE :nombre', { nombre: `%${(nombre as string).toLowerCase()}%` });
    }
    if (apellidos) {
      queryBuilder = queryBuilder.andWhere('LOWER(user.apellidos) LIKE :apellidos', { apellidos: `%${(apellidos as string).toLowerCase()}%` });
    }
    if (dni) {
      queryBuilder = queryBuilder.andWhere('LOWER(user.dni) LIKE :dni', { dni: `%${(dni as string).toLowerCase()}%` });
    }
    if (ciclo) {
      queryBuilder = queryBuilder.andWhere('ciclo.id = :cicloId', { cicloId: parseInt(ciclo as string) });
    }

    const students = await queryBuilder.orderBy('user.apellidos', 'ASC').getMany();
    res.json(students);
  } catch (error) {
    console.error('Error en búsqueda de estudiantes:', error);
    res.status(500).json({ error: 'Error al buscar estudiantes' });
  }
});

router.get('/:id', async (req, res) => {
  try {
    const user = await userRepository().findOne({
      where: { id: parseInt(req.params.id) },
      relations: ['tipo'],
    });

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    res.json(user);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener usuario' });
  }
});

router.post('/login', async (req, res) => {
  try {
    const { username, password } = req.body;

    const user = await userRepository().findOne({
      where: { username },
      relations: ['tipo'],
    });

    if (!user) {
      return res.status(401).json({ error: 'Usuario no encontrado' });
    }

    if (user.password !== password) {
      return res.status(401).json({ error: 'Contraseña incorrecta' });
    }

    const { password: _, ...userWithoutPassword } = user;
    res.json(userWithoutPassword);
  } catch (error) {
    res.status(500).json({ error: 'Error en el login' });
  }
});

router.post('/', async (req, res) => {
  try {
    const { email, username, password, nombre, apellidos, dni, direccion, telefono1, telefono2, tipo_id } = req.body;

    const existingUser = await userRepository().findOne({
      where: [{ username }, { email }],
    });

    if (existingUser) {
      return res.status(400).json({ error: 'El usuario o email ya existe' });
    }

    const newUser = userRepository().create({
      email,
      username,
      password,
      nombre,
      apellidos,
      dni,
      direccion,
      telefono1,
      telefono2,
      tipo_id: tipo_id || 4,
    });

    await userRepository().save(newUser);

    const { password: _, ...userWithoutPassword } = newUser;
    res.status(201).json(userWithoutPassword);
  } catch (error) {
    res.status(500).json({ error: 'Error al crear usuario' });
  }
});

router.put('/:id', async (req, res) => {
  try {
    const user = await userRepository().findOne({
      where: { id: parseInt(req.params.id) },
    });

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    const { email, nombre, apellidos, dni, direccion, telefono1, telefono2, tipo_id, password } = req.body;

    if (email) user.email = email;
    if (nombre) user.nombre = nombre;
    if (apellidos) user.apellidos = apellidos;
    if (dni) user.dni = dni;
    if (direccion) user.direccion = direccion;
    if (telefono1) user.telefono1 = telefono1;
    if (telefono2) user.telefono2 = telefono2;
    if (tipo_id) user.tipo_id = tipo_id;
    if (password) user.password = password;

    await userRepository().save(user);

    const { password: _, ...userWithoutPassword } = user;
    res.json(userWithoutPassword);
  } catch (error) {
    res.status(500).json({ error: 'Error al actualizar usuario' });
  }
});

router.delete('/:id', async (req, res) => {
  try {
    const user = await userRepository().findOne({
      where: { id: parseInt(req.params.id) },
      relations: ['tipo'],
    });

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    if (user.tipo_id === 1) {
      return res.status(403).json({ error: 'No se puede eliminar al usuario God' });
    }

    await userRepository().remove(user);
    res.json({ message: 'Usuario eliminado correctamente' });
  } catch (error) {
    res.status(500).json({ error: 'Error al eliminar usuario' });
  }
});

export default router;
