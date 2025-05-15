import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subcategory } from '../../models/Entity/Product/subcategory';
import { Observable } from 'rxjs';
import { AuthService } from '../User/auth.service';

@Injectable({
  providedIn: 'root',
})
export class SubcategoryService {
  private apiUrl = 'http://localhost:8080/subcategoria';

  constructor(private httpClient: HttpClient, private auth: AuthService) {}

  /**
   * Obtiene una lista de todas las subcategorías dependiendo de la categoría seleccionada 
   * desde el endpoint
   * @param id - Identificador de la categoría
   * @returns - observable que emite una lista de subcategorías
   */
  getSubCategoriesByCategory(id: number): Observable<Subcategory[]> {
    return this.httpClient.get<Subcategory[]>(`${this.apiUrl}/${id}`);
  }

  /**
   * Crea una nueva subcategoría
   * @param subcategory - subcategoría para agregar
   * @returns - Observable que emite un mensaje de confirmación o error
   */
  createSubcategory(subcategory: Subcategory): Observable<{ message: string }> {
    return this.httpClient.post<{ message: string }>(
      `${this.apiUrl}`,
      subcategory,
      { headers: this.auth.getAuthHeaders() }
    );
  }
}
