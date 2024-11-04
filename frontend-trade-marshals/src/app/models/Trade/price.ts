import { Instrument } from "./instrument";

export class Price {
    constructor(
        public askPrice: number,
        public bidPrice: number,
        public priceTimestamp: string,
        public instrument: Instrument
    ) {}
}
