import { Order } from "./order";

export class Trade {
    constructor(
        public instrumentId: string,
        public quantity: number,
        public executionPrice: number,
        public direction: string,
        public clientId: string,
        public order: Partial<Order>,
        public tradeId: string,
        public cashValue: number,
        public executedAt: Date
    ) {}
}

// {
// 	"instrumentId": "N123456",
// 	"quantity": 10,
// 	"executionPrice": 104.25,
// 	"direction": "S",
// 	"clientId": "123",
// 	"order": {
// 		"instrumentId": "N123456",
// 		"quantity": 10,
// 		"targetPrice": 104,
// 		"direction": "S",
// 		"clientId": "123",
// 		"token": 920141621
// 	},
// 	"tradeId": "hgmdut8fdkl-9mc1zs9t54k-pm12o2amhzj",
// 	"cashValue": 1052.925
// }


