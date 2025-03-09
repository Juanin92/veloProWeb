import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { UpdateUserModalComponent } from "../user/update-user-modal/update-user-modal.component";
import { MessageModalComponent } from "../user/message-modal/message-modal.component";
import { MenuComponent } from "../menu/menu.component";
import { LocalData } from '../../models/Entity/local-data';
import { CommonModule, DatePipe } from '@angular/common';
import { LocalDataService } from '../../services/local-data.service';
import { Router, RouterOutlet } from '@angular/router';
import * as bootstrap from 'bootstrap';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [RouterOutlet, UpdateUserModalComponent, MenuComponent, CommonModule, MessageModalComponent],
  providers: [DatePipe],
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent implements AfterViewInit {
  isMenuActive: boolean = true;
  currentDate: string = '';
  localData: LocalData | null = null;
  @ViewChild('userDropdown') dropdownButton!: ElementRef;
  dropdownInstance!: bootstrap.Dropdown;

  constructor(
    private datePipe: DatePipe,
    private localDataService: LocalDataService,
    private router: Router) {
    this.getDate();
    this.loadDataFromLocalStorage();
  }

  ngAfterViewInit(): void {
    if (this.dropdownButton) {
      this.dropdownInstance = new bootstrap.Dropdown(this.dropdownButton.nativeElement);
    }
  }

  isLoginPage(): boolean {
    return this.router.url === '/main/home';
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
