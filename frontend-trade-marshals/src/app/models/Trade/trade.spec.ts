import { Order } from './order';
import { Trade } from './trade';

describe('Trade', () => {
  it('should create an instance', () => {
    let partial = new Order('', -1, -1, '', '', '', -1)
    expect(new Trade('', 0, 0, '', '', partial ,'', 0,new Date())).toBeTruthy();
  });
});
