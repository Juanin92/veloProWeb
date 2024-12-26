import { Component } from '@angular/core';
import { BrandService } from '../../../services/Product/brand.service';
import { CategoryService } from '../../../services/Product/category.service';
import { SubcategoryService } from '../../../services/Product/subcategory.service';
import { UnitService } from '../../../services/Product/unit.service';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.css'
})
export class CategoriesComponent {

  constructor(
    private brandService: BrandService,
    private categoryService: CategoryService,
    private subcategoryService: SubcategoryService,
    private unitService: UnitService
  ){}
}
