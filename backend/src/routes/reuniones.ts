// Rutas de reuniones - CRUD de reuniones profesor-alumno
import { Router } from 'express';
import { AppDataSource } from '../data-source';
import { Reunion } from '../entities/Reunion';
import { Between, MoreThanOrEqual, LessThanOrEqual } from 'typeorm';

const router = Router();
const reunionRepository = () => AppDataSource.getRepository(Reunion);

router.get('/today/count', async (req, res) => {
  try {
    const today = new Date();
    const startOfDay = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 0, 0, 0);
    const endOfDay = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 23, 59, 59);

    const count = await reunionRepository().count({
      where: {
        fecha: Between(startOfDay, endOfDay),
      },
    });
    res.json({ count });
  } catch (error) {
    res.status(500).json({ error: 'Error al contar reuniones de hoy' });
  }
});

router.get('/', async (req, res) => {
  try {
    const reuniones = await reunionRepository().find({
      relations: ['profesor', 'alumno'],
      order: { fecha: 'DESC' },
    });
    res.json(reuniones);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener reuniones' });
  }
});

router.get('/profesor/:id', async (req, res) => {
  try {
    const reuniones = await reunionRepository().find({
      where: { profesor_id: parseInt(req.params.id) },
      relations: ['profesor', 'alumno'],
      order: { fecha: 'DESC' },
    });
    res.json(reuniones);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener reuniones del profesor' });
  }
});

router.get('/alumno/:id', async (req, res) => {
  try {
    const reuniones = await reunionRepository().find({
      where: { alumno_id: parseInt(req.params.id) },
      relations: ['profesor', 'alumno'],
      order: { fecha: 'DESC' },
    });
    res.json(reuniones);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener reuniones del alumno' });
  }
});

router.get('/:id', async (req, res) => {
  try {
    const reunion = await reunionRepository().findOne({
      where: { id_reunion: parseInt(req.params.id) },
      relations: ['profesor', 'alumno'],
    });

    if (!reunion) {
      return res.status(404).json({ error: 'Reunión no encontrada' });
    }

    res.json(reunion);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener reunión' });
  }
});

router.post('/', async (req, res) => {
  try {
    const { titulo, asunto, fecha, profesor_id, alumno_id, id_centro, aula, estado } = req.body;

    const newReunion = reunionRepository().create({
      titulo,
      asunto,
      fecha: new Date(fecha),
      profesor_id,
      alumno_id,
      id_centro: id_centro || '15112',
      aula,
      estado: estado || 'pendiente',
    });

    await reunionRepository().save(newReunion);
    res.status(201).json(newReunion);
  } catch (error) {
    res.status(500).json({ error: 'Error al crear reunión' });
  }
});

router.put('/:id', async (req, res) => {
  try {
    const reunion = await reunionRepository().findOne({
      where: { id_reunion: parseInt(req.params.id) },
    });

    if (!reunion) {
      return res.status(404).json({ error: 'Reunión no encontrada' });
    }

    const { titulo, asunto, fecha, estado, estado_eus, aula, id_centro } = req.body;

    if (titulo) reunion.titulo = titulo;
    if (asunto) reunion.asunto = asunto;
    if (fecha) reunion.fecha = new Date(fecha);
    if (estado) reunion.estado = estado;
    if (estado_eus) reunion.estado_eus = estado_eus;
    if (aula) reunion.aula = aula;
    if (id_centro) reunion.id_centro = id_centro;

    await reunionRepository().save(reunion);
    res.json(reunion);
  } catch (error) {
    res.status(500).json({ error: 'Error al actualizar reunión' });
  }
});

router.delete('/:id', async (req, res) => {
  try {
    const reunion = await reunionRepository().findOne({
      where: { id_reunion: parseInt(req.params.id) },
    });

    if (!reunion) {
      return res.status(404).json({ error: 'Reunión no encontrada' });
    }

    await reunionRepository().remove(reunion);
    res.json({ message: 'Reunión eliminada correctamente' });
  } catch (error) {
    res.status(500).json({ error: 'Error al eliminar reunión' });
  }
});

export default router;
