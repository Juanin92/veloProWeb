import { Component, OnInit } from '@angular/core';
import { RecordService } from '../../services/record.service';
import { Record } from '../../models/Entity/record';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CashRegister } from '../../models/Entity/cash-register';
import { CashRegisterService } from '../../services/Sale/cash-register.service';
import { LocalDataService } from '../../services/local-data.service';
import { LocalData } from '../../models/Entity/local-data';
import { UserService } from '../../services/User/user.service';
import { User } from '../../models/Entity/user';

@Component({
  selector: 'app-setting',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './setting.component.html',
  styleUrl: './setting.component.css'
})
export class SettingComponent implements OnInit{

  recordList: Record[] = [];
  cashRegistersList: CashRegister[] = [];
  userList: User[] = [];
  data: LocalData = {
    id: 0,
    name: '',
    phone: '',
    email: '',
    emailSecurityApp: '',
    address: ''
  };
  access: boolean = false;
  pass: string = '';

  constructor(
    private recordService: RecordService,
    private userService: UserService,
    private cashRegisterService: CashRegisterService,
    private localDataService: LocalDataService){}

  ngOnInit(): void {
    this.getRecords();
    this.getCashRegisters();
    this.getData();
    this.getUsers();
  }

  getUsers(): void{
    this.userService.getListUsers().subscribe({
      next: (list) =>{ 
        this.userList = list;
      }, error: (error)=>{
        console.log('Error al obtener la lista ', error);
      }
    });
  }

  getRecords(): void{
    this.recordService.getRecords().subscribe({
      next:(list)=>{
        this.recordList = list;
      },
      error: (error)=>{
        console.log('No se encontró información sobre los registros');
      }
    });
  }

  getCashRegisters(): void{
    this.cashRegisterService.getCashRegisters().subscribe({
      next:(list)=>{
        this.cashRegistersList = list;
      },
      error: (error)=>{
        console.log('No se encontró información sobre los registros de caja')
      }
    });
  }

  getData(): void{
    this.localDataService.getData().subscribe({
      next:(list)=>{
        if(list.length === 1){
          this.data = list[0];
        }
      }
    });
  }

  getAccessHistory(): void{
    const key = 1234;
    if (this.pass === key.toString()) {
      this.access = true;
    }else{
      console.log('Acceso denegado');
    }
  }
}
