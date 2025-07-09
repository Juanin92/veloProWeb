import { AfterViewInit, Component, Input} from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { TooltipService } from '../../utils/tooltip.service';
import { CommonModule } from '@angular/common';
import { MenuPermissionsService } from '../../services/permissions/menu-permissions.service';
import { AuthService } from '../../services/user/auth.service';
import { Role } from '../../models/enum/role';
import { NotificationService } from '../../utils/notification-service.service';
import { firstValueFrom } from 'rxjs';
import { CashRegisterService } from '../../services/sale/cash-register.service';
import { ErrorMessageService } from '../../utils/error-message.service';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements AfterViewInit{
  @Input() isActive: boolean = true;

  constructor(private tooltipService: TooltipService, 
    protected permission: MenuPermissionsService,
    private auth: AuthService,
    private cashRegisterService: CashRegisterService,
    private router: Router,
    private notification: NotificationService,
    private errorMessage: ErrorMessageService){}

  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }

  isSellerRole(): void{
    if(this.auth.getRole() === Role.SELLER){
      this.router.navigate(['/main/ventas-reporte']);
    } 
  }

  async logout(): Promise<void> {
      const hasOpenerRegister = await this.validateOpeningCashierRegister();
      if(hasOpenerRegister) {
        this.notification.showErrorToast('Debes registrar el cierre de caja para cerrar sesión', 'center', 3000);
        return;
      }
      this.auth.logout().subscribe({
        next:() =>{
          this.auth.removeToken();
          sessionStorage.clear();
          localStorage.clear();
          this.router.navigate(['/login']);
        }, error: (error) =>{
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(message, 'center', 3000);
        }
      });
    }
  
    async validateOpeningCashierRegister(): Promise<boolean> {
      try {
        return await firstValueFrom(this.cashRegisterService.hasOpenRegisterOnDate());
      } catch (error) {
        return false;
      }
    }
}
