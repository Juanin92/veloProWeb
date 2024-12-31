import { TestBed } from '@angular/core/testing';

import { ProductService } from '../product.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Product } from '../../../models/Entity/Product/product.model';
import { StatusProduct } from '../../../models/enum/status-product';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;
  
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [
          ProductService,
          provideHttpClient(),
          provideHttpClientTesting()
        ]
      });
      service = TestBed.inject(ProductService);
      httpMock = TestBed.inject(HttpTestingController);
    });
  
    afterEach(() => {
      httpMock.verify();
    });
  
    it('Debería ser creado el servicio de Product', () => {
      expect(service).toBeTruthy();
    });
  
    //Pruebas API para obtener todos los productos
    it('Debería traer una lista de productos, "getProducts(): Observable<Product[]>"', () => {
      const mockProducts: Product[] = [
        { id: 1, description: 'Samsung', salePrice: 0, buyPrice:0, stock:0, status: false, statusProduct: StatusProduct.UNAVAILABLE, brand: {id:1, name: 'Samsung'}, unit:{id:1, nameUnit: '1 UN'}, category:{id:1, name: 'Tech'}, subcategoryProduct:{id:1, name: 'Phone', category: {id:1, name: 'Tech'}} },
        { id: 1, description: 'Apple', salePrice: 0, buyPrice:0, stock:0, status: false, statusProduct: StatusProduct.UNAVAILABLE, brand: {id:2, name: 'Apple'}, unit:{id:1, nameUnit: '1 UN'}, category:{id:1, name: 'Tech'}, subcategoryProduct:{id:1, name: 'Phone', category: {id:1, name: 'Tech'}} },
      ];
  
      service.getProducts().subscribe(products => {
        expect(products.length).toBe(2);
        expect(products).toEqual(mockProducts);
      });
  
      const req = httpMock.expectOne('http://localhost:8080/stock');
      expect(req.request.method).toBe('GET');
      req.flush(mockProducts);
    });
  
    //Pruebas API para crear un nuevo producto
    it('Debería crear un nuevo producto, "createProduct(product: Product): Observable<{message: string}>"', () => {
      const mockProduct: Product = {
        id: 1, description: 'Asus', salePrice: 0, buyPrice:0, stock:0, status: false, statusProduct: StatusProduct.UNAVAILABLE, brand: {id:3, name: 'Asus'}, unit:{id:1, nameUnit: '1 UN'}, category:{id:1, name: 'Tech'}, subcategoryProduct:{id:1, name: 'PC', category: {id:1, name: 'Tech'}}
      };
      const responseMessage = { message: "Producto agregada correctamente!" };
      service.createProduct(mockProduct).subscribe(response => {
        expect(response.message).toEqual("Producto agregada correctamente!");
      });
      const request = httpMock.expectOne('http://localhost:8080/stock');
      expect(request.request.method).toBe('POST');
      request.flush(responseMessage);
    });
    it('Debería manejar error al crear una nueva categoría', () => {
      const mockProduct: Product = {
        id: 1, description: 'Apple', salePrice: 0, buyPrice:0, stock:0, status: false, statusProduct: StatusProduct.UNAVAILABLE, brand: {id:2, name: 'Apple'}, unit:{id:1, nameUnit: '1 UN'}, category:{id:1, name: 'Tech'}, subcategoryProduct:{id:1, name: 'Phone', category: {id:1, name: 'Tech'}}
      };
      const errorMessage = { error: "Producto Existente: Hay registro de esta categoría." };
  
      service.createProduct(mockProduct).subscribe(
        response => fail('debería haber fallado'),
        error => {
          expect(error.status).toBe(400);
          expect(error.error.error).toEqual("Producto Existente: Hay registro de esta categoría.");
        }
      );
  
      const request = httpMock.expectOne('http://localhost:8080/stock');
      expect(request.request.method).toBe('POST');
      request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
    });
});
