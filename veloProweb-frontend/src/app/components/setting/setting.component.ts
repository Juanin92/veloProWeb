import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LocalDataService } from '../../services/data/local-data.service';
import { LocalData } from '../../models/entity/data/local-data';
import { CashierMovementsComponent } from "./cashier-movements/cashier-movements.component";
import { DispatchLayoutComponent } from "../sale/dispatch-layout/dispatch-layout.component";
import { TaskLayoutComponent } from "../communication/task-layout/task-layout.component";
import { SettingPermissionsService } from '../../services/permissions/setting-permissions.service';
import { NotificationService } from '../../utils/notification-service.service';
import { AuthService } from '../../services/user/auth.service';
import { EncryptionService } from '../../security/encryption.service';
import { AuthRequestDTO } from '../../models/DTO/auth-request-dto';
import { ErrorMessageService } from '../../utils/error-message.service';
import { RegisterComponent } from './register/register.component';

@Component({
  selector: 'app-setting',
  standalone: true,
  imports: [CommonModule, FormsModule, CashierMovementsComponent, DispatchLayoutComponent, RegisterComponent, TaskLayoutComponent],
  templateUrl: './setting.component.html',
  styleUrl: './setting.component.css'
})
export class SettingComponent{

  data: LocalData;
  access: boolean = false;
  pass: string = '';
  encryptedCode: string = '';

  constructor(
    private localDataService: LocalDataService,
    private authService: AuthService,
    private encryptionService: EncryptionService,
    protected permission: SettingPermissionsService,
    private errorMessage: ErrorMessageService,
    private notification: NotificationService){
      this.data = this.initializeLocalData();
    }

  loadData(): void{
    this.localDataService.getData().subscribe({
      next:(data)=>{
        data = data;
        this.modifyLocalDataStorage(data);
      }
    });
  }

  updateLocalData(): void{
    if(this.data){
      this.localDataService.updateData(this.data).subscribe({
        next: (response)=>{
          this.notification.showSuccessToast(response.message, 'top', 3000);
          this.loadData();
        }, error: (error)=>{
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(message, 'top', 3000);
        }
      });
    }
  }

  getEncryptedKey(): void{
    this.authService.getEncryptionKey().subscribe({
      next: (key) => { this.encryptedCode = key;},
      error: (error) => {
        const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(message, 'top', 3000);
      }
    });
  }

  getAccessHistory(): void{
    if(this.encryptedCode && this.pass.trim() !== ''){
      const authRequest: AuthRequestDTO = {identifier: '',
        token: this.encryptionService.encryptPassword(this.pass, this.encryptedCode)};
      this.authService.getAuthAccess(authRequest).subscribe({
        next:(response)=>{ this.access = response;},
        error:(error)=>{
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(message, 'top', 3000);
        }
      });
    }
  }

  private modifyLocalDataStorage(data: { name: string, phone: string, email: string, address: string }): void{
    sessionStorage.setItem('companyData', JSON.stringify(data));
  }

  private initializeLocalData(): LocalData{
    return {
      name: '',
      phone: '',
      email: '',
      emailSecurityApp: '',
      address: ''
    }
  }
}
