import { Component, Input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import * as bootstrap from 'bootstrap';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit{
  @Input() isActive: boolean = false;

  ngOnInit(): void {
    this.initializeTooltip();
  }

  initializeTooltip(): void{
    const toolTipElementList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    toolTipElementList.forEach((element) => {
      new bootstrap.Tooltip(element);
    });
  }
}
