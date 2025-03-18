import { AfterViewInit, Component, Input} from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { TooltipService } from '../../utils/tooltip.service';
import { AuthService } from '../../services/User/auth.service';
import { Role } from '../../models/enum/role';

import { CommonModule } from '@angular/common';
import { RoleService } from '../../services/User/role.service';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements AfterViewInit{
  @Input() isActive: boolean = true;
  role = Role;

  constructor(private tooltipService: TooltipService, 
    protected roleService: RoleService,
    private router: Router){}

  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }

  isSellerRole(): void{
    if(this.roleService.getRole() === this.role.SELLER){
      this.router.navigate(['/main/ventas-reporte']);
    } 
  }
}
