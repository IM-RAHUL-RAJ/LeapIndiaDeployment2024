import { Router } from 'express';
import { ClientRestController } from '../controllers/client-rest-controller';

const router = Router();
const clientRestController = new ClientRestController();

// Define client-specific routes
router.get('/verify-email/:email', async (req, res) => {
    await clientRestController.verifyClientEmail(req, res);
});
router.post('/register', async (req, res) => {
    await clientRestController.registerNewClient(req, res);
});
router.get('', async (req, res) => {
    await clientRestController.loginExistingClient(req, res);
});

export default router;