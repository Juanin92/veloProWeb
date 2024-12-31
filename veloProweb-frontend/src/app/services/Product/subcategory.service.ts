import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subcategory } from '../../models/Entity/Product/subcategory';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SubcategoryService {
  
  private apiUrl = 'http://localhost:8080/subcategoria';

  constructor(private httpClient: HttpClient) { }

  getSubCategoriesByCategory(id: number): Observable<Subcategory[]> {
    return this.httpClient.get<Subcategory[]>(`${this.apiUrl}/${id}`);
  }

  createSubcategory(subcategory: Subcategory): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}`,subcategory);
  }
}
