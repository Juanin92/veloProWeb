import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../../services/User/task.service';
import { TaskRequestDTO } from '../../../models/DTO/task-request-dto';

@Component({
  selector: 'app-task',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task.component.html',
  styleUrl: './task.component.css'
})
export class TaskComponent implements OnInit{

  @Output() taskUpdated = new EventEmitter<TaskRequestDTO[]>();
  taskList: TaskRequestDTO[] = [];

  constructor(private taskService: TaskService){}

  ngOnInit(): void {
    this.getTasks();
  }

  getTasks(): void {
    this.taskService.getTasks().subscribe({
      next: (list) => {
        this.taskList = list;
        this.taskUpdated.emit(this.taskList);
      }, error: (error) => {
        console.log('Error obtener tareas, ', error);
      }
    });
  }

  completeTask(task: TaskRequestDTO) {
    task.status = false;
    this.taskService.completeTask(task.id).subscribe({
      next:(response)=>{
        const message = response.message;
        console.log('Ok: ',message);
      }, error: (error)=>{
        const message = error.error?.message || error.error?.error;
        console.log('Error: ',message);
      }
    });
    setTimeout(() => {
      this.taskList = this.taskList.filter(t => t !== task);
      this.taskUpdated.emit(this.taskList);
    }, 3000);
  }
}
