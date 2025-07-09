import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Category } from '../../models/entity/product/category';
import { AuthService } from '../user/auth.service';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private apiUrl = 'http://localhost:8080/categoria';

  constructor(private httpClient: HttpClient, private auth: AuthService) {}

  /**
   * Obtiene una lista de todas las categorías desde el endpoint
   * @returns observable que emite una lista de categorías
   */
  getCategories(): Observable<Category[]> {
    return this.httpClient.get<Category[]>(this.apiUrl);
  }

  /**
   * Crea una nueva categoría
   * @param category - categoría para agregar
   * @returns - Observable que emite un mensaje de confirmación o error
   */
  createCategory(category: Category): Observable<{ message: string }> {
    return this.httpClient.post<{ message: string }>(
      `${this.apiUrl}`,
      category,
      { headers: this.auth.getAuthHeaders() }
    );
  }
}
