import { TestBed } from '@angular/core/testing';

import { SupplierService } from '../supplier.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Supplier } from '../../../models/entity/purchase/supplier';

describe('SupplierService', () => {
  let service: SupplierService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SupplierService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(SupplierService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('Debería ser creado el servicio de Supplier', () => {
    expect(service).toBeTruthy();
  });

  //Prueba API para obtener todos los proveedores
  it('Debería traer una lista de proveedores, "getSuppliers(): Observable<Supplier[]>', () => {
    const mockSuppliers: Supplier[] = [
      {id: 1, name: 'Prueba', rut:'12234657-7', phone:'12345678', email:'xx@xx.com'},
      {id:1 ,name: 'Test', rut:'10234657-7', phone:'12345378', email:'xx@xx.com'}
    ];

    service.getSuppliers().subscribe(supplierList =>{
      expect(supplierList.length).toBe(2);
      expect(supplierList).toEqual(mockSuppliers);
    });

    const request = httpMock.expectOne('http://localhost:8080/proveedores');
    expect(request.request.method).toBe('GET');
    request.flush(mockSuppliers);
  });

  //Prueba para crear un nuevo proveedor
  it('Debería crear un nuevo proveedor, "createSupplier(supplier: Supplier): Observable<{message: string}>"', () =>{
    const mockSupplier: Supplier = {
      id: 1, name: 'Prueba', rut:'12234657-7', phone:'12345678', email:'xx@xx.com'
    };

    const responseMessage = { message: "Proveedor agregado correctamente!"};
    service.createSupplier(mockSupplier).subscribe(response => {
      expect(response.message).toEqual("Proveedor agregado correctamente!");
    });

    const request = httpMock.expectOne('http://localhost:8080/proveedores');
    expect(request.request.method).toBe('POST');
    request.flush(responseMessage);
  });
  it('Debería manejar un error al crear un nuevo proveedor, "createSupplier(supplier: Supplier): Observable<{message: string}>"', () =>{
    const mockSupplier: Supplier = {
      id: 1, name: 'Prueba', rut:'12234657-7', phone:'12345678', email:'xx@xx.com'
    };

    const errorMessage = { error: "Proveedor Existente: Hay registro de esta proveedor."};
    service.createSupplier(mockSupplier).subscribe(
      response => fail('Falla'),
      error => {
      expect(error.status).toBe(400);
      expect(error.error.error).toEqual("Proveedor Existente: Hay registro de esta proveedor.");
    });

    const request = httpMock.expectOne('http://localhost:8080/proveedores');
    expect(request.request.method).toBe('POST');
    request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
  });

  //Prueba API para actualizar un proveedor
  it('Debería actualizar los datos de un proveedor, "updateSupplier(supplier: Supplier): Observable<{message: string}>"', () => {
    const mockSupplier: Supplier = {
      id: 1, name: 'Prueba', rut:'12234657-7', phone:'12345678', email:'xx@xx.com'
    };

    const responseMessage = { message: "Proveedor actualizado correctamente!"};
    service.updateSupplier(mockSupplier).subscribe(response => {
      expect(response.message).toEqual("Proveedor actualizado correctamente!");
    });

    const request = httpMock.expectOne('http://localhost:8080/proveedores');
    expect(request.request.method).toBe('PUT');
    request.flush(responseMessage);
  });
});
