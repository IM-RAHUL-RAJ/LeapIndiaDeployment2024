import { Router } from "express";
import { ClientPreferencesRestController } from "../controllers/client-preferences-rest-controller";

const router = Router();
const clientPreferencesRestController = new ClientPreferencesRestController()

router.get('/:id', async(req, res) => {
    await clientPreferencesRestController.getClientPreferencesById(req, res);
})

router.post('', async(req, res) => {
    await clientPreferencesRestController.addClientPreferences(req,res);
})

router.put('', async(req, res) => {
    await clientPreferencesRestController.updateClientPreferences(req,res);
})

export default router;