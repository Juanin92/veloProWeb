import { Routes } from '@angular/router';
import { StockComponent } from './components/product/stock/stock.component';
import { CustomerComponent } from './components/customer/customer.component';
import { SupplierComponent } from './components/purchase/supplier/supplier.component';
import { PurchaseComponent } from './components/purchase/purchase/purchase.component';
import { ReportComponent } from './components/Report/report/report.component';
import { SaleComponent } from './components/sale/sale/sale.component';
import { SaleReportComponent } from './components/Report/Sale/sale-report.component';
import { PurchaseReportComponent } from './components/Report/Purchase/purchase-report.component';
import { KardexComponent } from './components/Report/kardex/kardex.component';
import { SettingComponent } from './components/setting/setting.component';
import { UserComponent } from './components/user/user.component';
import { HomeComponent } from './components/Home/home.component';

export const routes: Routes = [
    {path: 'clientes', component: CustomerComponent},
    {path: 'stock', component: StockComponent},
    {path: 'proveedores', component: SupplierComponent},
    {path: 'compras', component: PurchaseComponent},
    {path: 'reportes', component: ReportComponent},
    {path: 'ventas', component: SaleComponent},
    {path: 'ventas-reporte', component: SaleReportComponent},
    {path: 'compras-reporte', component: PurchaseReportComponent},
    {path: 'registro-producto', component: KardexComponent},
    {path: 'configuraciones', component: SettingComponent},
    {path: 'usuario', component: UserComponent},
    {path: 'home', component: HomeComponent},
];
