import { TestBed } from '@angular/core/testing';

import { PurchaseService } from '../purchase.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { PurchaseRequestDTO } from '../../../models/DTO/purchase-request-dto';
import { Purchase } from '../../../models/Entity/Purchase/purchase';

describe('PurchaseService', () => {
  let service: PurchaseService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PurchaseService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(PurchaseService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('Debería ser creado el servicio de Purchase', () => {
    expect(service).toBeTruthy();
  });

  //Prueba API para crear una compra
  it('Debería crear una compra, "createPurchase(purchaseRequest: PurchaseRequestDTO): Observable<{message: string}>"', () =>{
    const mockPurchaseDTO: PurchaseRequestDTO = {
      id: 1, date: '2025-01-10', document: 'A203', documentType: 'Factura', idSupplier: 1, tax: 2000, total: 20000, detailList: []
    };

    const responseMessage = {message: "Compra agregada correctamente!"};
    service.createPurchase(mockPurchaseDTO).subscribe(response => {
      expect(response.message).toEqual("Compra agregada correctamente!");
    });

    const request = httpMock.expectOne('http://localhost:8080/compras/crear');
    expect(request.request.method).toBe('POST');
    request.flush(responseMessage);
  });

  //Prueba API para obtener el total de compras
  it('Debería obtener el numero total de compras registradas, "getTotalPurchase(): Observable<number>"',() =>{
    const mockPurchases: Purchase[] = [
      {id: 1, date: '2025-01-10', document: 'A203', documentType: 'Factura', tax: 2000, purchaseTotal: 20000, supplier: null},
      {id: 2, date: '2025-02-10', document: 'A253', documentType: 'Factura', tax: 2000, purchaseTotal: 20000, supplier: null}
    ];

    const total = mockPurchases.length;
    service.getTotalPurchase().subscribe(number =>{
      expect(number).toEqual(total);
    });

    const request = httpMock.expectOne('http://localhost:8080/compras/total_compras');
    expect(request.request.method).toBe('GET');
    request.flush(total);
  });
});
