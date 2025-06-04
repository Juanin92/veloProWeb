import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskForm } from '../../../models/entity/communication/task-form';
import { TaskService } from '../../../services/communication/task.service';
import { Task } from '../../../models/entity/communication/task';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { NotificationService } from '../../../utils/notification-service.service';

@Component({
  selector: 'app-task',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task.component.html',
  styleUrl: './task.component.css'
})
export class TaskComponent implements OnInit{

  @Output() taskUpdated = new EventEmitter<TaskForm[]>();
  taskList: Task[] = [];

  constructor(private taskService: TaskService, 
    private errorMessage: ErrorMessageService,
    private notification: NotificationService){}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getTasks().subscribe({
      next: (list) => {
        this.taskList = list;
        this.taskUpdated.emit(this.taskList);
      }
    });
  }

  completeTask(task: Task) {
    task.status = false;
    this.taskService.completeTask(task.id).subscribe({
       error: (error)=>{
        const message = this.errorMessage.errorMessageExtractor(error);
        this.notification.showErrorToast(message, 'top', 3000);
      }
    });
    setTimeout(() => {
      this.taskList = this.taskList.filter(t => t !== task);
      this.taskUpdated.emit(this.taskList);
    }, 3000);
  }
}
