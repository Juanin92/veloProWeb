import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Message } from '../../models/Entity/message';
import { TaskComponent } from "../setting/task/task.component";
import { AlertComponent } from "../user/alert/alert.component";
import { AlertModel } from '../../models/Entity/alert-model';
import { DispatchComponent } from "../sale/dispatch/dispatch.component";
import { Dispatch } from '../../models/Entity/Sale/dispatch';
import { MessageComponent } from "../user/message/message.component";
import { TaskRequestDTO } from '../../models/DTO/task-request-dto';
import { RoleService } from '../../services/User/role.service';
import { Role } from '../../models/enum/role';

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
  role = Role;
  expanded = {
    notification: false,
    dispatch: false,
    task: false,
    alert: false
  };

  constructor(protected roleService: RoleService) { }

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
