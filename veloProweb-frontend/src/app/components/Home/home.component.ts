import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../services/User/task.service';
import { Task } from '../../models/Entity/task';
import { MessageModalComponent } from "../user/message-modal/message-modal.component";
import { Message } from '../../models/Entity/message';
import { TaskComponent } from "../setting/task/task.component";
import { AlertComponent } from "../user/alert/alert.component";
import { AlertModel } from '../../models/Entity/alert-model';
import { DispatchComponent } from "../sale/dispatch/dispatch.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, MessageModalComponent, TaskComponent, AlertComponent, DispatchComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent{

  taskList: Task[] = [];
  messageList: Message[] = [];
  alertList: AlertModel[] = [];
  expanded = {
    notification: false,
    dispatch: false,
    task: false,
    alert: false
  };

  constructor(private taskService: TaskService) { }

  getMessagesUpdated(messages: Message[]): void{
    this.messageList = messages;
  }

  getTasksUpdated(tasks: Task[]): void{
    this.taskList = tasks;
  }

  getAlertUpdated(alerts: AlertModel[]): void{
    this.alertList = alerts;
  }

  toggleCardExpand(section: 'notification' | 'dispatch' | 'task' | 'alert'): void {
    this.expanded[section] = !this.expanded[section];
  }
}
