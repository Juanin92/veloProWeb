import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Message } from '../../models/Entity/communication/message';
import { TaskComponent } from "../setting/task/task.component";
import { AlertComponent } from "../communication/alert/alert.component";
import { AlertModel } from '../../models/Entity/alert-model';
import { DispatchComponent } from "../sale/dispatch/dispatch.component";
import { Dispatch } from '../../models/Entity/Sale/dispatch';
import { MessageComponent } from "../communication/message/message.component";
import { TaskRequestDTO } from '../../models/DTO/task-request-dto';
import { DispatchPermissionsService } from '../../services/Permissions/dispatch-permissions.service';
import { UserPermissionsService } from '../../services/Permissions/user-permissions.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, TaskComponent, AlertComponent, DispatchComponent, MessageComponent],
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

  constructor(protected permissionDispatch: DispatchPermissionsService, 
    protected permissionAlert: UserPermissionsService) { }

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
