import { AfterViewInit, Component, Input} from '@angular/core';
import { RouterModule } from '@angular/router';
import { TooltipService } from '../../utils/tooltip.service';
import { AuthService } from '../../services/User/auth.service';
import { Role } from '../../models/enum/role';

import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements AfterViewInit{
  @Input() isActive: boolean = true;
  Role = Role;

  constructor(private tooltipService: TooltipService, public auth: AuthService){}

  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }
}
