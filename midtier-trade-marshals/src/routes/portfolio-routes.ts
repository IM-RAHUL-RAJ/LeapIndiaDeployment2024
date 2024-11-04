import { Router } from "express";
import { ClientPortfolioRestController } from "../controllers/client-portfolio-rest-controller";
 
const router = Router();
const clientPortfolioRestController = new ClientPortfolioRestController();
 
// Define client-activity-report-specific routes
router.get('/client/:clientId', async (req, res) => {
    await clientPortfolioRestController.getClientPortfolio(req, res);
});
 
export default router;