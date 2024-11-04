import { TestBed } from '@angular/core/testing';

import { RoboAdvisorService } from './robo-advisor.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('RoboAdvisorService', () => {
  let service: RoboAdvisorService;
  let httpTestingController: HttpTestingController

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(RoboAdvisorService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
