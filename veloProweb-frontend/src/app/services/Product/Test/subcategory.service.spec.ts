import { TestBed } from '@angular/core/testing';

import { SubcategoryService } from '../subcategory.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Subcategory } from '../../../models/Entity/Product/subcategory';

describe('SubcategoryService', () => {
  let service: SubcategoryService;
  let httpMock: HttpTestingController;
  
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [
          SubcategoryService,
          provideHttpClient(),
          provideHttpClientTesting()
        ]
      });
      service = TestBed.inject(SubcategoryService);
      httpMock = TestBed.inject(HttpTestingController);
    });
  
    afterEach(() => {
      httpMock.verify();
    });
  
    it('Debería ser creado el servicio de Subcategory', () => {
      expect(service).toBeTruthy();
    });
  
    //Pruebas API para obtener todas las subcategoría por ID de categoría
    it('Debería traer una lista de subcategoría por ID de categoría, "getSubCategoriesByCategory(id: number): Observable<Subcategory[]>"', () => {
      const id = 1;
      const mockSubcategory: Subcategory[] = [
        { id: 1, name: 'Arroz', category: {id: 1, name: 'Comida'} },
        { id: 1, name: 'Pasta', category: {id: 1, name: 'Comida'} }
      ];
  
      service.getSubCategoriesByCategory(id).subscribe(subcategories => {
        expect(subcategories.length).toBe(2);
        expect(subcategories).toEqual(mockSubcategory);
      });
  
      const req = httpMock.expectOne(`http://localhost:8080/subcategoria/${id}`);

      expect(req.request.method).toBe('GET');
      req.flush(mockSubcategory);
    });
  
    //Pruebas API para crear nueva subcategoría
    it('Debería crear una nueva subcategoría, "createSubcategory(subcategory: Subcategory): Observable<{message: string}>"', () => {
      const mockSubcategory: Subcategory = {
        id: 1, name: 'agua', category: {id: 1, name: 'liquido'}
      };
      const responseMessage = { message: "Subcategoría agregada correctamente!" };
      service.createSubcategory(mockSubcategory).subscribe(response => {
        expect(response.message).toEqual("Subcategoría agregada correctamente!");
      });
      const request = httpMock.expectOne('http://localhost:8080/subcategoria');
      expect(request.request.method).toBe('POST');
      request.flush(responseMessage);
    });
    it('Debería manejar error al crear una nueva subcategoría', () => {
      const mockSubcategory: Subcategory = {
        id: 1, name: 'Arroz', category: {id: 1, name: 'Comida'}
      };
      const errorMessage = { error: "Subcategoría Existente: Hay registro de esta subcategoría." };
  
      service.createSubcategory(mockSubcategory).subscribe(
        response => fail('debería haber fallado'),
        error => {
          expect(error.status).toBe(400);
          expect(error.error.error).toEqual("Subcategoría Existente: Hay registro de esta subcategoría.");
        }
      );
  
      const request = httpMock.expectOne('http://localhost:8080/subcategoria');
      expect(request.request.method).toBe('POST');
      request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
    });
});
