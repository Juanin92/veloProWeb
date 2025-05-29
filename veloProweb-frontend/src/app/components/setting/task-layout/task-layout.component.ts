import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TaskRequestDTO } from '../../../models/DTO/task-request-dto';
import { TaskService } from '../../../services/communication/task.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { UserDTO } from '../../../models/DTO/user-dto';
import { UserService } from '../../../services/User/user.service';
import * as bootstrap from 'bootstrap';
import { SettingPermissionsService } from '../../../services/Permissions/setting-permissions.service';
import { UserResponse } from '../../../models/Entity/user/user-response';

@Component({
  selector: 'app-task-layout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-layout.component.html',
  styleUrl: './task-layout.component.css'
})
export class TaskLayoutComponent implements OnInit, AfterViewInit {

  @ViewChild('taskTableFilter') dropdownButton!: ElementRef;
  taskList: TaskRequestDTO[] = [];
  filteredTaskList: TaskRequestDTO[] = [];
  userList: UserResponse[] = [];
  task: TaskRequestDTO;
  textFilter: string = '';
  dropdownInstance!: bootstrap.Dropdown;
  sortDate: boolean = true;
  sortName: boolean = true;
  sortStatus: boolean = true;
  sortPosition: boolean = true;

  constructor(
    private taskService: TaskService,
    private userService: UserService,
    private notification: NotificationService,
    protected permission: SettingPermissionsService) {
    this.task = this.initializeTask();
  }

  ngAfterViewInit(): void {
    if (this.dropdownButton) {
      this.dropdownInstance = new bootstrap.Dropdown(this.dropdownButton.nativeElement);
    }
  }

  ngOnInit(): void {
    this.getUsers();
    this.getTasks();
    this.resetTask();
  }

  getUsers(): void {
    this.userService.getListUsers().subscribe({
      next: (list) => {
        this.userList = list;
      }, error: (error) => {
        console.log('Error al obtener la lista ', error);
      }
    });
  }

  getTasks(): void {
    this.taskService.getAllTasks().subscribe({
      next: (list) => {
        this.taskList = list;
        this.filteredTaskList = list;
      }, error: (error) => {
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
      }, error: (error) => {
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
        (this.textFilter === 'abierta' && task.status === true) ||
        (this.textFilter === 'cerrada' && task.status === false)
      );
    }
  }

  resetTask(): void {
    this.task.description = '';
    this.task.user = '';
  }

  toggleDropdown() {
    if (this.dropdownInstance) {
      this.dropdownInstance.toggle();
    }
  }

  toggleSortDate() {
    this.sortDate = !this.sortDate;
    this.filteredTaskList.sort((a, b) => {
      const dateA = new Date(a.created).getTime();
      const dateB = new Date(b.created).getTime();
      return this.sortDate ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortName() {
    this.sortName = !this.sortName;
    this.filteredTaskList.sort((a, b) => {
      const nameA = a.user.toLowerCase();
      const nameB = b.user.toLowerCase();
      if (this.sortName) {
        return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
      } else {
        return nameA > nameB ? -1 : nameA < nameB ? 1 : 0;
      }
    });
  }

  toggleSortStatus() {
    this.sortStatus = !this.sortStatus;
    this.filteredTaskList = this.taskList.filter(task => task.status === this.sortStatus);
  }

  toggleSortPosition() {
    this.filteredTaskList.reverse();
    this.sortPosition = !this.sortPosition;
  }

  private initializeTask(): TaskRequestDTO {
    return {
      id: 0,
      description: '',
      status: true,
      created: '',
      user: '',
    }
  }
}
