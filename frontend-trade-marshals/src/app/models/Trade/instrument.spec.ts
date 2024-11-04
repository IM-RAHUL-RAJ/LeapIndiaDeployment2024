import { Instrument } from './instrument';

describe('Instrument', () => {
  it('should create an instance', () => {
    expect(new Instrument('', '', '', '', '', 0, 0)).toBeTruthy();
  });
});
