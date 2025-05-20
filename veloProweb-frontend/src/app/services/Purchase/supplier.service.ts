import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Supplier } from '../../models/Entity/Purchase/supplier';
import { AuthService } from '../User/auth.service';
import { SupplierForm } from '../../models/Entity/Purchase/supplier-form';

@Injectable({
  providedIn: 'root'
})
export class SupplierService {

  private apiUrl = 'http://localhost:8080/proveedores';

  constructor(private http: HttpClient, private auth: AuthService) { }

  /**
   * Obtiene una lista de proveedores desde la API
   * @returns - Observable que emite una lista de proveedores
   */
  getSuppliers(): Observable<Supplier[]>{
    return this.http.get<Supplier[]>(this.apiUrl);
  }

  /**
   * Crea un nuevo proveedor realizando una petición POST a la API
   * @param supplier - Proveedor por agregar
   * @returns - Observable emite un mensaje de confirmación o error
   */
  createSupplier(supplier: SupplierForm): Observable<{message: string}>{
    return this.http.post<{message: string}>(`${this.apiUrl}`, supplier, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Actualizar un proveedor realizando una petición PUT a la API
   * @param supplier - Proveedor por actualizar
   * @returns - Observable emite un mensaje de confirmación o error
   */
  updateSupplier(supplier: SupplierForm): Observable<{message: string}>{
    return this.http.put<{message: string}>(`${this.apiUrl}`, supplier, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Obtiene un proveedor especifico haciendo una petición GET
   * @param id - Identificador del proveedor
   * @returns - Observable emite un objeto supplier
   */
  getSupplier(rut: string): Observable<Supplier>{
    return this.http.get<Supplier>(`${this.apiUrl}/buscar`, {params:{rut: rut.toString()}});
  } 
}
