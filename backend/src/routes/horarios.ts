// Rutas de horarios - Consulta de horarios por profesor, aula o ciclo
import { Router } from 'express';
import { AppDataSource } from '../data-source';
import { Horario } from '../entities/Horario';

const router = Router();
const horarioRepository = () => AppDataSource.getRepository(Horario);

router.get('/', async (req, res) => {
  try {
    const horarios = await horarioRepository().find({
      relations: ['profesor', 'modulo', 'modulo.ciclo'],
      order: { dia: 'ASC', hora: 'ASC' },
    });
    res.json(horarios);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener horarios' });
  }
});

router.get('/profesor/:id', async (req, res) => {
  try {
    const horarios = await horarioRepository().find({
      where: { profe_id: parseInt(req.params.id) },
      relations: ['modulo', 'modulo.ciclo'],
      order: { dia: 'ASC', hora: 'ASC' },
    });
    res.json(horarios);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener horario del profesor' });
  }
});

router.get('/aula/:aula', async (req, res) => {
  try {
    const horarios = await horarioRepository().find({
      where: { aula: req.params.aula },
      relations: ['profesor', 'modulo'],
      order: { dia: 'ASC', hora: 'ASC' },
    });
    res.json(horarios);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener horario del aula' });
  }
});

router.get('/ciclo/:id', async (req, res) => {
  try {
    const horarios = await horarioRepository().find({
      relations: ['profesor', 'modulo', 'modulo.ciclo'],
      order: { dia: 'ASC', hora: 'ASC' },
    });
    const cicloId = parseInt(req.params.id);
    const filteredHorarios = horarios.filter(h => h.modulo?.ciclo_id === cicloId);
    res.json(filteredHorarios);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener horarios del ciclo' });
  }
});

router.get('/:id', async (req, res) => {
  try {
    const horario = await horarioRepository().findOne({
      where: { id: parseInt(req.params.id) },
      relations: ['profesor', 'modulo'],
    });

    if (!horario) {
      return res.status(404).json({ error: 'Horario no encontrado' });
    }

    res.json(horario);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener horario' });
  }
});

router.post('/', async (req, res) => {
  try {
    const { dia, hora, profe_id, modulo_id, aula, observaciones } = req.body;

    const newHorario = horarioRepository().create({
      dia,
      hora,
      profe_id,
      modulo_id,
      aula,
      observaciones,
    });

    await horarioRepository().save(newHorario);
    res.status(201).json(newHorario);
  } catch (error) {
    res.status(500).json({ error: 'Error al crear horario' });
  }
});

router.put('/:id', async (req, res) => {
  try {
    const horario = await horarioRepository().findOne({
      where: { id: parseInt(req.params.id) },
    });

    if (!horario) {
      return res.status(404).json({ error: 'Horario no encontrado' });
    }

    const { dia, hora, profe_id, modulo_id, aula, observaciones } = req.body;

    if (dia) horario.dia = dia;
    if (hora) horario.hora = hora;
    if (profe_id) horario.profe_id = profe_id;
    if (modulo_id) horario.modulo_id = modulo_id;
    if (aula !== undefined) horario.aula = aula;
    if (observaciones !== undefined) horario.observaciones = observaciones;

    await horarioRepository().save(horario);
    res.json(horario);
  } catch (error) {
    res.status(500).json({ error: 'Error al actualizar horario' });
  }
});

router.delete('/:id', async (req, res) => {
  try {
    const horario = await horarioRepository().findOne({
      where: { id: parseInt(req.params.id) },
    });

    if (!horario) {
      return res.status(404).json({ error: 'Horario no encontrado' });
    }

    await horarioRepository().remove(horario);
    res.json({ message: 'Horario eliminado correctamente' });
  } catch (error) {
    res.status(500).json({ error: 'Error al eliminar horario' });
  }
});

export default router;
