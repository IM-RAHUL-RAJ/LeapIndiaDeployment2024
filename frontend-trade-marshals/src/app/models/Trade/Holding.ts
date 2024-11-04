export class Holding{
    constructor(
        public categoryId: string,
        public instrumentId: string,
        public instrumentDesc: string,
        public quantity: number,
        public avgPrice: number
    ) {}
 }