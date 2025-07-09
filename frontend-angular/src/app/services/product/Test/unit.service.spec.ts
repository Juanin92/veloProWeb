import { TestBed } from '@angular/core/testing';

import { UnitService } from '../unit.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { UnitProduct } from '../../../models/entity/product/unit-product';

describe('UnitService', () => {
  let service: UnitService;
  let httpMock: HttpTestingController;
  
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [
          UnitService,
          provideHttpClient(),
          provideHttpClientTesting()
        ]
      });
      service = TestBed.inject(UnitService);
      httpMock = TestBed.inject(HttpTestingController);
    });
  
    afterEach(() => {
      httpMock.verify();
    });
  
    it('Debería ser creado el servicio de Unit', () => {
      expect(service).toBeTruthy();
    });
  
    //Pruebas API para obtener todas las categoría
    it('Debería traer una lista de categoría, "getUnits(): Observable<UnitProductModel[]>"', () => {
      const mockUnits: UnitProduct[] = [
        { id: 1, nameUnit: '10 KG' },
        { id: 2, nameUnit: '20 KG' }
      ];
  
      service.getUnits().subscribe(units => {
        expect(units.length).toBe(2);
        expect(units).toEqual(mockUnits);
      });
  
      const req = httpMock.expectOne('http://localhost:8080/unidad');
      expect(req.request.method).toBe('GET');
      req.flush(mockUnits);
    });
  
    //Pruebas API para crear nueva categoría
    it('Debería crear una nueva categoría, "createUnit(unit: UnitProductModel): Observable<{ message: string }>"', () => {
      const mockUnit: UnitProduct = {
        id: 1, nameUnit: '5 KG'
      };
      const responseMessage = { message: "Unidad de medida agregada correctamente!" };
      service.createUnit(mockUnit).subscribe(response => {
        expect(response.message).toEqual("Unidad de medida agregada correctamente!");
      });
      const request = httpMock.expectOne('http://localhost:8080/unidad');
      expect(request.request.method).toBe('POST');
      request.flush(responseMessage);
    });
    it('Debería manejar error al crear una nueva categoría', () => {
      const mockUnit: UnitProduct = {
        id: 1, nameUnit: '10 KG'
      };
      const errorMessage = { error: "Unidad de medida Existente: Hay registro de esta categoría." };
  
      service.createUnit(mockUnit).subscribe(
        response => fail('debería haber fallado'),
        error => {
          expect(error.status).toBe(400);
          expect(error.error.error).toEqual("Unidad de medida Existente: Hay registro de esta categoría.");
        }
      );
  
      const request = httpMock.expectOne('http://localhost:8080/unidad');
      expect(request.request.method).toBe('POST');
      request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
    });
});
