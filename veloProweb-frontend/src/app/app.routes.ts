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
import { LoginComponent } from './components/login/login.component';
import { AuthGuardService } from './utils/auth-guard.service';
import { MainComponent } from './components/main/main.component';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' }, 
    { path: 'login', component: LoginComponent },
    { path: 'main', component: MainComponent, canActivate: [AuthGuardService], children: [
        { path: 'clientes', component: CustomerComponent, canActivate: [AuthGuardService]},
        { path: 'stock', component: StockComponent, canActivate: [AuthGuardService]},
        { path: 'proveedores', component: SupplierComponent, canActivate: [AuthGuardService]},
        { path: 'compras', component: PurchaseComponent, canActivate: [AuthGuardService]},
        { path: 'reportes', component: ReportComponent, canActivate: [AuthGuardService]},
        { path: 'ventas', component: SaleComponent, canActivate: [AuthGuardService]},
        { path: 'ventas-reporte', component: SaleReportComponent, canActivate: [AuthGuardService]},
        { path: 'compras-reporte', component: PurchaseReportComponent, canActivate: [AuthGuardService]},
        { path: 'registro-producto', component: KardexComponent, canActivate: [AuthGuardService]},
        { path: 'configuraciones', component: SettingComponent, canActivate: [AuthGuardService]},
        { path: 'usuario', component: UserComponent, canActivate: [AuthGuardService]},
        { path: 'home', component: HomeComponent, canActivate: [AuthGuardService]}
    ]}
];
