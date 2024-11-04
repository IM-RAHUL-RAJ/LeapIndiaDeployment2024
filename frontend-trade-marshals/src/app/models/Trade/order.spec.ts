import { Order } from './order';

describe('Order', () => {
  it('should create an instance', () => {
    expect(new Order('', -1, -1, '', '', '', -1)).toBeTruthy();
  });
});
