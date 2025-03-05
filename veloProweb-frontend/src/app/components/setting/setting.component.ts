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
import { TaskService } from '../../services/User/task.service';
import { Task } from '../../models/Entity/task';
import { NotificationService } from '../../utils/notification-service.service';
import { DispatchService } from '../../services/Sale/dispatch.service';
import { Dispatch } from '../../models/Entity/Sale/dispatch';

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
  dispatchList: Dispatch[] = [];
  taskList: Task[] = [];
  task: Task;
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
    private localDataService: LocalDataService,
    private taskService: TaskService,
    private dispatchService: DispatchService,
    private notification: NotificationService){
      this.task = this.initializeTask();
    }

  ngOnInit(): void {
    this.getRecords();
    this.getCashRegisters();
    this.getData();
    this.getUsers();
    this.getDispatches();
    this.getTasks();
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

  getDispatches(): void{
    this.dispatchService.getDispatches().subscribe({
      next:(list)=>{
        this.dispatchList = list;
      },
      error: (error)=>{
        console.log('No se encontró información sobre los despachos registrados');
      }
    });
  }

  getTasks(): void{
    // this.taskService.getTasks().subscribe({
    //   next:(list)=>{
    //     this.taskList = list;
    //   },
    //   error: (error)=>{
    //     console.log('No se encontró información sobre los tareas asignadas');
    //   }
    // });
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

  createNewTask(task: Task): void {
    console.log('Tarea:', task);
    if (!task.description || !task.user) {
      this.notification.showWarningToast('Faltan datos', 'top', 3000);
      return;
    }
    this.taskService.createTask(task).subscribe({
      next: (response) => {
        const message = response.message;
        this.notification.showSuccessToast(message, 'top', 3000);
        this.resetTask();
      },
      error: (error) => {
        const message = error.error?.message || error.error?.error;
        console.error('Error: ', message); 
        this.notification.showErrorToast(message, 'top', 3000);
      }
    });
  }

  resetTask(): void{
    this.task.description = '';
    this.task.user = null;
  }

  private initializeTask(): Task{
    return {
      id: 0,
      description: '',
      status: true,
      created: '',
      user: null,
    }
  }
}
