import { Router } from "express";
import { ClientActivityReportRestController } from "../controllers/client-activity-report-rest-controller";
 
const router = Router();
const clientActivityReportRestController = new ClientActivityReportRestController();
 
// Define client-activity-report-specific routes
router.get('/holdings/:clientId', async (req, res) => {
    await clientActivityReportRestController.getClientHoldingsReport(req, res);
});
router.get('/trades/:clientId', async (req, res) => {
    await clientActivityReportRestController.getClientTradesReport(req, res);
});
router.get('/pl/:clientId', async (req, res) => {
    await clientActivityReportRestController.getClientPLReport(req, res);
});
 
export default router;