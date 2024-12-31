import { TestBed } from '@angular/core/testing';

import { CategoryService } from '../category.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Category } from '../../../models/Entity/Product/category';

describe('CategoryService', () => {
  let service: CategoryService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CategoryService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CategoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('Debería ser creado el servicio de Category', () => {
    expect(service).toBeTruthy();
  });

  //Pruebas API para obtener todas las categoría
  it('Debería traer una lista de categoría, "getCategories(): Observable<Category[]>"', () => {
    const mockCategories: Category[] = [
      { id: 1, name: 'Comida' },
      { id: 2, name: 'Tech' }
    ];

    service.getCategories().subscribe(categories => {
      expect(categories.length).toBe(2);
      expect(categories).toEqual(mockCategories);
    });

    const req = httpMock.expectOne('http://localhost:8080/categoria');
    expect(req.request.method).toBe('GET');
    req.flush(mockCategories);
  });

  //Pruebas API para crear nueva categoría
  it('Debería crear una nueva categoría, "createCategory(category: Category): Observable<{message: string}>"', () => {
    const mockCategory: Category = {
      id: 1, name: 'liquido'
    };
    const responseMessage = { message: "Categoría agregada correctamente!" };
    service.createCategory(mockCategory).subscribe(response => {
      expect(response.message).toEqual("Categoría agregada correctamente!");
    });
    const request = httpMock.expectOne('http://localhost:8080/categoria');
    expect(request.request.method).toBe('POST');
    request.flush(responseMessage);
  });
  it('Debería manejar error al crear una nueva categoría', () => {
    const mockCategory: Category = {
      id: 1, name: 'Comida'
    };
    const errorMessage = { error: "Categoría Existente: Hay registro de esta categoría." };

    service.createCategory(mockCategory).subscribe(
      response => fail('debería haber fallado'),
      error => {
        expect(error.status).toBe(400);
        expect(error.error.error).toEqual("Categoría Existente: Hay registro de esta categoría.");
      }
    );

    const request = httpMock.expectOne('http://localhost:8080/categoria');
    expect(request.request.method).toBe('POST');
    request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
  });
});
