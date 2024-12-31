import { TestBed } from '@angular/core/testing';

import { BrandService } from '../brand.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Brand } from '../../../models/Entity/Product/brand';

describe('BrandService', () => {
  let service: BrandService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BrandService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(BrandService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('Debería ser creado el servicio de Brand', () => {
    expect(service).toBeTruthy();
  });

  //Pruebas API para obtener todas las marcas
  it('Debería traer una lista de marcas, "getBrands(): Observable<Brand[]>"', () => {
    const mockBrands: Brand[] = [
      { id: 1, name: 'Samsung' },
      { id: 2, name: 'Apple' }
    ];

    service.getBrands().subscribe(brands => {
      expect(brands.length).toBe(2);
      expect(brands).toEqual(mockBrands);
    });

    const req = httpMock.expectOne('http://localhost:8080/marcas');
    expect(req.request.method).toBe('GET');
    req.flush(mockBrands);
  });

  //Pruebas API para crear nueva marca
  it('Debería crear una nueva marca, "createBrand(brand: Brand): Observable<{message: string}>"', () => {
    const mockBrand: Brand = {
      id: 1, name: 'Asus'
    };
    const responseMessage = { message: "Marca agregada correctamente!" };
    service.createBrand(mockBrand).subscribe(response => {
      expect(response.message).toEqual("Marca agregada correctamente!");
    });
    const request = httpMock.expectOne('http://localhost:8080/marcas');
    expect(request.request.method).toBe('POST');
    request.flush(responseMessage);
  });
  it('Debería manejar error al crear una nueva marca', () => {
    const mockBrand: Brand = {
      id: 1, name: 'Apple'
    };
    const errorMessage = { error: "Marca Existente: Hay registro de esta marca." };

    service.createBrand(mockBrand).subscribe(
      response => fail('debería haber fallado'),
      error => {
        expect(error.status).toBe(400);
        expect(error.error.error).toEqual("Marca Existente: Hay registro de esta marca.");
      }
    );

    const request = httpMock.expectOne('http://localhost:8080/marcas');
    expect(request.request.method).toBe('POST');
    request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
  });
});
