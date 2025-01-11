import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import * as bootstrap from 'bootstrap';
import { TooltipService } from '../../utils/tooltip.service';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements AfterViewInit{
  @Input() isActive: boolean = true;

  constructor(private tooltipService: TooltipService){}

  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }
}
