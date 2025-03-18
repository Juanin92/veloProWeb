import { AfterViewInit, Component, Input} from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { TooltipService } from '../../utils/tooltip.service';
import { CommonModule } from '@angular/common';
import { MenuPermissionsService } from '../../services/Permissions/menu-permissions.service';
import { AuthService } from '../../services/User/auth.service';
import { Role } from '../../models/enum/role';

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
    private router: Router){}

  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }

  isSellerRole(): void{
    if(this.auth.getRole() === Role.SELLER){
      this.router.navigate(['/main/ventas-reporte']);
    } 
  }
}
