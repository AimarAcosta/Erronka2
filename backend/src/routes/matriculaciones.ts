// Rutas de matriculaciones - Relacion alumno-ciclo
import { Router } from 'express';
import { AppDataSource } from '../data-source';
import { Matriculacion } from '../entities/Matriculacion';

const router = Router();
const matriculacionRepository = () => AppDataSource.getRepository(Matriculacion);

router.get('/', async (req, res) => {
  try {
    const matriculaciones = await matriculacionRepository().find({
      relations: ['alumno', 'ciclo'],
      order: { fecha: 'DESC' },
    });
    res.json(matriculaciones);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener matriculaciones' });
  }
});

router.get('/alumno/:id', async (req, res) => {
  try {
    const matriculaciones = await matriculacionRepository().find({
      where: { alum_id: parseInt(req.params.id) },
      relations: ['ciclo'],
      order: { fecha: 'DESC' },
    });
    res.json(matriculaciones);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener matriculaciones del alumno' });
  }
});

router.get('/ciclo/:id', async (req, res) => {
  try {
    const matriculaciones = await matriculacionRepository().find({
      where: { ciclo_id: parseInt(req.params.id) },
      relations: ['alumno', 'ciclo'],
      order: { fecha: 'DESC' },
    });
    res.json(matriculaciones);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener matriculaciones del ciclo' });
  }
});

router.get('/:id', async (req, res) => {
  try {
    const matriculacion = await matriculacionRepository().findOne({
      where: { id: parseInt(req.params.id) },
      relations: ['alumno', 'ciclo'],
    });

    if (!matriculacion) {
      return res.status(404).json({ error: 'Matriculación no encontrada' });
    }

    res.json(matriculacion);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener matriculación' });
  }
});

router.post('/', async (req, res) => {
  try {
    const { alum_id, ciclo_id, curso, fecha } = req.body;

    const newMatriculacion = matriculacionRepository().create({
      alum_id,
      ciclo_id,
      curso,
      fecha: new Date(fecha),
    });

    await matriculacionRepository().save(newMatriculacion);
    res.status(201).json(newMatriculacion);
  } catch (error) {
    res.status(500).json({ error: 'Error al crear matriculación' });
  }
});

router.delete('/:id', async (req, res) => {
  try {
    const matriculacion = await matriculacionRepository().findOne({
      where: { id: parseInt(req.params.id) },
    });

    if (!matriculacion) {
      return res.status(404).json({ error: 'Matriculación no encontrada' });
    }

    await matriculacionRepository().remove(matriculacion);
    res.json({ message: 'Matriculación eliminada correctamente' });
  } catch (error) {
    res.status(500).json({ error: 'Error al eliminar matriculación' });
  }
});

export default router;
