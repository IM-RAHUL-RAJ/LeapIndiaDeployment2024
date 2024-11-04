export class Order {
    constructor(
        public instrumentId: string,
        public quantity: number,
        public targetPrice: number,
        public direction: string,
        public clientId : string,
        public orderId: string,
        public token: number
    ) {}
}
