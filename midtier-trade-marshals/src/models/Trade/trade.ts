import { Order } from "./order";

export interface Trade {
    instrumentId: string,
    quantity: number,
    executionPrice: number,
    direction: string,
    clientId: string,
    order: Partial<Order>,
    tradeId: string,
    cashValue: number,
    executedAt: Date
}
