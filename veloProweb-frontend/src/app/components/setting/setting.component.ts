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
import { NotificationService } from '../../utils/notification-service.service';

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
    protected permission: SettingPermissionsService,
    private notification: NotificationService){}

  getData(): void{
    this.localDataService.getData().subscribe({
      next:(list)=>{
        if(list.length === 1){
          this.data = list[0];
          const simplifiedData = {
            name: list[0].name,
            phone: list[0].phone,
            email: list[0].email,
            address: list[0].address
          };
          this.modifyLocalDataStorage(simplifiedData);
        }
      }
    });
  }

  updateLocalData(): void{
    if(this.data){
      this.localDataService.updateData(this.data).subscribe({
        next: (response)=>{
          this.notification.showSuccessToast(response.message, 'top', 3000);
          this.getData();
        }, error: (error)=>{
          const message = error.error?.message || error.error?.error || error?.error;
          this.notification.showErrorToast(message, 'top', 3000);
        }
      });
    }
  }

  getAccessHistory(): void{
    const key = 1234;
    if (this.pass === key.toString()) {
      this.access = true;
    }else{
      console.log('Acceso denegado');
    }
  }

  private modifyLocalDataStorage(data: { name: string, phone: string, email: string, address: string }): void{
    sessionStorage.setItem('companyData', JSON.stringify(data));
  }
}
