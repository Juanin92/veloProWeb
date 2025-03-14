import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TaskRequestDTO } from '../../../models/DTO/task-request-dto';
import { TaskService } from '../../../services/User/task.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { UserDTO } from '../../../models/DTO/user-dto';
import { UserService } from '../../../services/User/user.service';

@Component({
  selector: 'app-task-layout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-layout.component.html',
  styleUrl: './task-layout.component.css'
})
export class TaskLayoutComponent implements OnInit{

  taskList: TaskRequestDTO[] = [];
  filteredTaskList: TaskRequestDTO[] = [];
  userList: UserDTO[] = [];
  task: TaskRequestDTO;
  textFilter: string = '';

  constructor(
    private taskService: TaskService,
    private userService: UserService,
    private notification: NotificationService){
    this.task = this.initializeTask();
  }

  ngOnInit(): void {
    this.getUsers();
    this.getTasks();
    this.resetTask();
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

  getTasks(): void{
    this.taskService.getAllTasks().subscribe({
      next:(list)=>{
        this.taskList = list;
        this.filteredTaskList = list;
      },error: (error)=>{
        console.log('No se encontró información sobre los tareas asignadas');
      }
    });
  }

  createNewTask(task: TaskRequestDTO): void {
    if (!task.description || !task.user) {
      this.notification.showWarningToast('Faltan datos', 'top', 3000);
      return;
    }
    this.taskService.createTask(task).subscribe({
      next: (response) => {
        const message = response.message;
        this.notification.showSuccessToast(message, 'top', 3000);
        this.resetTask();
        this.getTasks();
      },error: (error) => {
        const message = error.error?.message || error.error?.error;
        console.error('Error: ', message); 
        this.notification.showErrorToast(message, 'top', 3000);
      }
    });
  }

  searchFilterTasks(): void {
    if (this.textFilter.trim() === '') {
      this.filteredTaskList = this.taskList;
    } else {
      this.filteredTaskList = this.taskList.filter(task =>
        task.user.toLowerCase().includes(this.textFilter.toLowerCase()) || 
        (this.textFilter === 'abierta' && task.status === true)  || 
        (this.textFilter === 'cerrada' && task.status === false)
      );
    }
  }

  resetTask(): void{
    this.task.description = '';
    this.task.user = '';
  }

  private initializeTask(): TaskRequestDTO{
    return {
      id: 0,
      description: '',
      status: true,
      created: '',
      user: '',
    }
  }
}
