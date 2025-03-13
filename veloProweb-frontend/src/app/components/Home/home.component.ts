import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { TaskService } from '../../services/User/task.service';
import { MessageModalComponent } from "../user/message-modal/message-modal.component";
import { Message } from '../../models/Entity/message';
import { TaskComponent } from "../setting/task/task.component";
import { AlertComponent } from "../user/alert/alert.component";
import { AlertModel } from '../../models/Entity/alert-model';
import { DispatchComponent } from "../sale/dispatch/dispatch.component";
import { Dispatch } from '../../models/Entity/Sale/dispatch';
import { MessageComponent } from "../user/message/message.component";
import { DispatchModalComponent } from "../sale/dispatch-modal/dispatch-modal.component";
import { TaskRequestDTO } from '../../models/DTO/task-request-dto';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, MessageModalComponent, TaskComponent, AlertComponent, DispatchComponent, MessageComponent, DispatchModalComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent{

  taskList: TaskRequestDTO[] = [];
  messageList: Message[] = [];
  alertList: AlertModel[] = [];
  dispatchList: Dispatch[] = [];
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

  getTasksUpdated(tasks: TaskRequestDTO[]): void{
    this.taskList = tasks;
  }

  getAlertUpdated(alerts: AlertModel[]): void{
    this.alertList = alerts;
  }

  getDispatchUpdated(dispatches: Dispatch[]): void{
    this.dispatchList = dispatches;
  }

  toggleCardExpand(section: 'notification' | 'dispatch' | 'task' | 'alert'): void {
    this.expanded[section] = !this.expanded[section];
  }
}
