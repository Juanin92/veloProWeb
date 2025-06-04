import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Message } from '../../models/entity/communication/message';
import { TaskComponent } from "../communication/task/task.component";
import { AlertComponent } from "../communication/alert/alert.component";
import { Alert } from '../../models/entity/communication/alert';
import { DispatchComponent } from "../sale/dispatch/dispatch.component";
import { Dispatch } from '../../models/entity/sale/dispatch';
import { MessageComponent } from "../communication/message/message.component";
import { TaskForm } from '../../models/entity/communication/task-form';
import { DispatchPermissionsService } from '../../services/permissions/dispatch-permissions.service';
import { UserPermissionsService } from '../../services/permissions/user-permissions.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, TaskComponent, AlertComponent, DispatchComponent, MessageComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent{

  taskList: TaskForm[] = [];
  messageList: Message[] = [];
  alertList: Alert[] = [];
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

  getTasksUpdated(tasks: TaskForm[]): void{
    this.taskList = tasks;
  }

  getAlertUpdated(alerts: Alert[]): void{
    this.alertList = alerts;
  }

  getDispatchUpdated(dispatches: Dispatch[]): void{
    this.dispatchList = dispatches;
  }

  toggleCardExpand(section: 'notification' | 'dispatch' | 'task' | 'alert'): void {
    this.expanded[section] = !this.expanded[section];
  }
}
