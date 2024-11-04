import { Router } from 'express';

const router = Router();

// Define health check route
router.get('/health', (req, res) => {
    res.json({ Message: 'Midtier Node is Working' });
});

export default router;