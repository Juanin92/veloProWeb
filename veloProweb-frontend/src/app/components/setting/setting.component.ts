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
import { AuthService } from '../../services/User/auth.service';
import { LoginRequest } from '../../models/DTO/login-request';
import { EncryptionService } from '../../security/encryption.service';
import { AuthRequestDTO } from '../../models/DTO/auth-request-dto';

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
  encryptedCode: string = '';

  constructor(
    private localDataService: LocalDataService,
    private authService: AuthService,
    private encryptionService: EncryptionService,
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

  getEncryptedKey(): void{
    this.authService.getEncryptionKey().subscribe({
      next: (key) => { this.encryptedCode = key;},
      error: (error) => {
        const message = error.error?.error || error?.error;
        console.log('Error: ', message);
      }
    });
  }

  getAccessHistory(): void{
    if(this.encryptedCode && this.pass.trim() !== ''){
      const authRequest: AuthRequestDTO = {identifier: '',
        token: this.encryptionService.encryptPassword(this.pass, this.encryptedCode)};
      this.authService.getAuthAccess(authRequest).subscribe({
        next:(response)=>{ this.access = response; console.log('response: ', response);},
        error:(error)=>{
          const message = error.error?.error || error?.error;
          console.log('Error: ', message);
        }
      });
    }
  }

  private modifyLocalDataStorage(data: { name: string, phone: string, email: string, address: string }): void{
    sessionStorage.setItem('companyData', JSON.stringify(data));
  }
}
