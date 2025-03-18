import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LocalDataService } from '../../services/local-data.service';
import { LocalData } from '../../models/Entity/local-data';
import { CashierMovementsComponent } from "./cashier-movements/cashier-movements.component";
import { DispatchLayoutComponent } from "../sale/dispatch-layout/dispatch-layout.component";
import { RegisterComponent } from "./register/register.component";
import { TaskLayoutComponent } from "./task-layout/task-layout.component";
import { SettingPermissionsService } from '../../services/Permissions/setting-permissions.service';

@Component({
  selector: 'app-setting',
  standalone: true,
  imports: [CommonModule, FormsModule, CashierMovementsComponent, DispatchLayoutComponent, RegisterComponent, TaskLayoutComponent],
  templateUrl: './setting.component.html',
  styleUrl: './setting.component.css'
})
export class SettingComponent{

  data: LocalData = {
    id: 0,
    name: '',
    phone: '',
    email: '',
    emailSecurityApp: '',
    address: ''
  };
  access: boolean = false;
  pass: string = '';

  constructor(private localDataService: LocalDataService,
    protected permission: SettingPermissionsService){}

  getData(): void{
    this.localDataService.getData().subscribe({
      next:(list)=>{
        if(list.length === 1){
          this.data = list[0];
        }
      }
    });
  }

  getAccessHistory(): void{
    const key = 1234;
    if (this.pass === key.toString()) {
      this.access = true;
    }else{
      console.log('Acceso denegado');
    }
  }
}
