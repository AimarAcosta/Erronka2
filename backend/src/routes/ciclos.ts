// Rutas de ciclos - Lista de ciclos formativos (DAW, DAM, etc)
import { Router } from 'express';
import { AppDataSource } from '../data-source';
import { Ciclo } from '../entities/Ciclo';

const router = Router();
const cicloRepository = () => AppDataSource.getRepository(Ciclo);

router.get('/', async (req, res) => {
  try {
    const ciclos = await cicloRepository().find({
      order: { nombre: 'ASC' },
    });
    res.json(ciclos);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener ciclos' });
  }
});

router.get('/:id', async (req, res) => {
  try {
    const ciclo = await cicloRepository().findOne({
      where: { id: parseInt(req.params.id) },
      relations: ['modulos'],
    });

    if (!ciclo) {
      return res.status(404).json({ error: 'Ciclo no encontrado' });
    }

    res.json(ciclo);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener ciclo' });
  }
});

export default router;
