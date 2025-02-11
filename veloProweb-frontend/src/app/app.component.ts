import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MenuComponent } from "./components/menu/menu.component";
import { DatePipe } from '@angular/common';
import { LocalDataService } from './services/local-data.service';
import { LocalData } from './models/Entity/local-data';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MenuComponent],
  providers: [DatePipe],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent{
  
  title = 'veloProweb-frontend';
  isMenuActive: boolean = true;
  currentDate: string = '';
  localData: LocalData | null = null;

  constructor(
    private datePipe: DatePipe, 
    private localDataService: LocalDataService){
    this.getDate();
    this.loadDataFromLocalStorage();
  }

  toggleMenu(): void {
    this.isMenuActive = !this.isMenuActive;
  }

  getDate(): void{
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
