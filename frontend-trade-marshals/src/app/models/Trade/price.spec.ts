import { Instrument } from './instrument';
import { Price } from './price';

describe('Price', () => {
  it('should create an instance', () => {
    let instrument = new Instrument('', '', '', '', '', 0, 0)
    expect(new Price(0,0,'',instrument)).toBeTruthy();
  });
});
