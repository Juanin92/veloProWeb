import { Routes } from '@angular/router';
import { StockComponent } from './components/product/stock/stock.component';
import { CustomerComponent } from './components/customer/customer.component';
import { SupplierComponent } from './components/purchase/supplier/supplier.component';

export const routes: Routes = [
    {path: 'clientes', component: CustomerComponent},
    {path: 'stock', component: StockComponent},
    {path: 'proveedores', component: SupplierComponent}
];
