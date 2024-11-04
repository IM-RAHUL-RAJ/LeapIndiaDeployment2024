import { Client } from "./Client";

export interface ClientProfile {
    client: Client | null,
    token: number | undefined
}