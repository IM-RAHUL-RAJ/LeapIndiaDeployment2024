import { Holding } from "../Trade/Holding";

export interface ClientPortfolio {
    clientId: string | undefined,
    currBalance: number,
    holdings: Holding[]
}