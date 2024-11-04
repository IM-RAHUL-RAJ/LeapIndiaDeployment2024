export class Instrument {
    constructor(
        public instrumentId: string,
        public externalIdType: string,
        public externalId: string,
        public categoryId: string,
        public instrumentDescription: string,
        public maxQuantity: number,
        public minQuantity: number
    ) {}
}
