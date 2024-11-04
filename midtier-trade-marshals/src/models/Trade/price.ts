import { Instrument } from "./instrument";

export interface Price {
    askPrice: number,
    bidPrice: number,
    priceTimestamp: string,
    instrument: Instrument
}
