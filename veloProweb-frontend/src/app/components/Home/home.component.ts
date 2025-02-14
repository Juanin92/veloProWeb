import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../services/User/task.service';
import { Task } from '../../models/Entity/task';
import { MessageModalComponent } from "../user/message-modal/message-modal.component";
import { Message } from '../../models/Entity/message';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, MessageModalComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  taskList: Task[] = [];
  messageList: Message[] = [];
  expanded = {
    notification: false,
    dispatch: false,
    task: false,
    alert: false
  };

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    this.getTasks();
  }

  getTasks(): void {
    this.taskService.getTasks(1).subscribe({
      next: (list) => {
        this.taskList = list;
      }, error: (error) => {
        console.log('Error obtener tareas, ', error);
      }
    });
  }

  getMessagesUpdated(messages: Message[]): void{
    this.messageList = messages;
  }

  completeTask(task: Task) {
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
    }, 3000);
  }

  toggleCardExpand(section: 'notification' | 'dispatch' | 'task' | 'alert'): void {
    this.expanded[section] = !this.expanded[section];
  }
}
