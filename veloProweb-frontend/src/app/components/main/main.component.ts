import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UpdateUserModalComponent } from "../user/update-user-modal/update-user-modal.component";
import { MessageModalComponent } from "../user/message-modal/message-modal.component";
import { MenuComponent } from "../menu/menu.component";
import { LocalData } from '../../models/Entity/local-data';
import { CommonModule, DatePipe } from '@angular/common';
import { LocalDataService } from '../../services/local-data.service';
import { Router, RouterOutlet } from '@angular/router';
import * as bootstrap from 'bootstrap';
import { AuthService } from '../../services/User/auth.service';
import { CashierComponent } from "../setting/cashier/cashier.component";
import { MenuPermissionsService } from '../../services/Permissions/menu-permissions.service';
import { NotificationService } from '../../utils/notification-service.service';
import { CashRegisterService } from '../../services/Sale/cash-register.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [RouterOutlet, UpdateUserModalComponent, MenuComponent, CommonModule, MessageModalComponent, CashierComponent],
  providers: [DatePipe],
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent implements AfterViewInit, OnInit {
  isMenuActive: boolean = true;
  currentDate: string = '';
  localData: LocalData | null = null;
  isOpen: boolean = false;
  isOpeningRegister: boolean = false;
  @ViewChild('userDropdown') dropdownButton!: ElementRef;
  dropdownInstance!: bootstrap.Dropdown;

  constructor(
    private auth: AuthService,
    private cashRegisterService: CashRegisterService,
    private datePipe: DatePipe,
    private localDataService: LocalDataService,
    private router: Router,
    protected permission: MenuPermissionsService,
    private notification: NotificationService) {
    this.getDate();
    this.loadDataFromLocalStorage();
  }

  ngOnInit(): void {
    const isOpenString = sessionStorage.getItem('isOpen');
    this.isOpen = isOpenString !== null;
  }

  ngAfterViewInit(): void {
    if (this.dropdownButton) {
      this.dropdownInstance = new bootstrap.Dropdown(this.dropdownButton.nativeElement);
    }
  }

  isLoginPage(): boolean {
    return this.router.url === '/main/home';
  }

  async logout(): Promise<void> {
    const hasOpenerRegister = await this.validateOpeningCashierRegister();
    if(hasOpenerRegister) {
      this.notification.showErrorToast('Debes registrar el cierre de caja para cerrar sesión', 'center', 3000);
      return;
    }
    this.auth.logout().subscribe({
      next:(response) =>{
        console.log(response.message);
        this.auth.removeToken();
        sessionStorage.clear();
        localStorage.clear();
        this.router.navigate(['/login']);
      }, error: (error) =>{
        const message = error.error?.error || error.error?.message || error?.error;
        console.log('Error: ', message);
      }
    });
  }

  async validateOpeningCashierRegister(): Promise<boolean> {
    try {
      return await firstValueFrom(this.cashRegisterService.hasOpenRegisterOnDate());
    } catch (error) {
      console.error("Error en la solicitud:", error);
      return false;
    }
  }

  toggleDropdown() {
    if (this.dropdownInstance) {
      this.dropdownInstance.toggle();
    }
  }

  toggleMenu(): void {
    this.isMenuActive = !this.isMenuActive;
  }

  getDate(): void {
    const now = new Date();
    const formatDate = this.datePipe.transform(now, 'dd-MM-yyyy  HH:mm');
    this.currentDate = formatDate ? formatDate : '';
  }

  loadDataFromLocalStorage(): void {
    const savedData = sessionStorage.getItem('companyData');
    if (savedData) {
      this.localData = JSON.parse(savedData);
    } else {
      this.localDataService.getData().subscribe({
        next: (list) => {
          if (list.length === 1) {
            const simplifiedData = {
              name: list[0].name,
              phone: list[0].phone,
              email: list[0].email,
              address: list[0].address
            };
            this.saveDataToLocalStorage(simplifiedData);
          }
        }
      });
    }
  }

  saveDataToLocalStorage(data: { name: string, phone: string, email: string, address: string }): void {
    sessionStorage.setItem('companyData', JSON.stringify(data));
  }
}
