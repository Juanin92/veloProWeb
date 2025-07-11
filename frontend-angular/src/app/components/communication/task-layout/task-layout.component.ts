import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TaskForm } from '../../../models/entity/communication/task-form';
import { NotificationService } from '../../../utils/notification-service.service';
import * as bootstrap from 'bootstrap';
import { SettingPermissionsService } from '../../../services/permissions/setting-permissions.service';
import { UserResponse } from '../../../models/entity/user/user-response';
import { TaskService } from '../../../services/communication/task.service';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { Task } from '../../../models/entity/communication/task';
import { UserService } from '../../../services/user/user.service';

@Component({
  selector: 'app-task-layout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-layout.component.html',
  styleUrl: './task-layout.component.css'
})
export class TaskLayoutComponent implements OnInit, AfterViewInit {

  @ViewChild('taskTableFilter') dropdownButton!: ElementRef;
  taskList: Task[] = [];
  filteredTaskList: Task[] = [];
  userList: UserResponse[] = [];
  taskForm: TaskForm;
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
    private errorMessage: ErrorMessageService,
    protected permission: SettingPermissionsService) {
    this.taskForm = this.initializeTask();
  }

  ngAfterViewInit(): void {
    if (this.dropdownButton) {
      this.dropdownInstance = new bootstrap.Dropdown(this.dropdownButton.nativeElement);
    }
  }

  ngOnInit(): void {
    this.loadUserData();
    this.loadTasks();
    this.resetTask();
  }

  loadUserData(): void {
    this.userService.getListUsers().subscribe({
      next: (list) => {
        this.userList = list;
      }
    });
  }

  loadTasks(): void {
    this.taskService.getAllTasks().subscribe({
      next: (list) => {
        this.taskList = list;
        this.filteredTaskList = list;
      }
    });
  }

  createNewTask(task: TaskForm): void {
    if (!task.description || !task.user) {
      this.notification.showWarningToast('Faltan datos', 'top', 3000);
      return;
    }
    this.taskService.createTask(task).subscribe({
      next: (response) => {
        this.notification.showSuccessToast(response.message, 'top', 3000);
        this.resetTask();
        this.loadTasks();
      }, error: (error) => {
        const message = this.errorMessage.errorMessageExtractor(error);
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
    this.taskForm.description = '';
    this.taskForm.user = '';
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

  private initializeTask(): TaskForm {
    return {
      description: '',
      user: '',
    }
  }
}
