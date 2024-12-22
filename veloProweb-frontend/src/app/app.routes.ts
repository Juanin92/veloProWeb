import { Routes } from '@angular/router';
import { CustomerComponent } from './components/customerView/customer.component';
import { StockComponent } from './components/product/stock/stock.component';

export const routes: Routes = [
    {path: 'clientes', component: CustomerComponent},
    {path: 'stock', component: StockComponent}
];
