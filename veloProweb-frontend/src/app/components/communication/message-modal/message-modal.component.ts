import { Component, OnInit } from '@angular/core';
import { Message } from '../../../models/Entity/message';
import { NotificationService } from '../../../utils/notification-service.service';
import { UserService } from '../../../services/User/user.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserResponse } from '../../../models/Entity/user/user-response';
import { MessageService } from '../../../services/communication/message.service';

@Component({
  selector: 'app-message-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './message-modal.component.html',
  styleUrl: './message-modal.component.css'
})
export class MessageModalComponent implements OnInit{

  userList: UserResponse[] = [];
  message: Message = {
    id: 0,
    context: '',
    created: '',
    read: false,
    delete: false,
    senderUser: null,
    receiverUser: null,
    senderName: ''
  };

  constructor(
    private messageService: MessageService,
    private userService: UserService,
    private notification: NotificationService){}

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers(): void{
    this.userService.getListUsers().subscribe({
      next:(list) =>{
        this.userList = list;
      }
    })
  }

  sendMessage(message: Message): void{
    if(!message.receiverUser || !message.context){
      this.notification.showWarningToast('Debe seleccionar un destinatario', 'top', 3000);
      return;
    }
    this.messageService.sendMessage(message).subscribe({
      next: (response)=>{
        const message = response.message;
        this.notification.showSuccessToast(message, 'top', 3000);
        this.resetModal();
      },error: (error)=>{
        const message = error.error?.error || error.error?.message;
        console.log('Error: ',message);
        this.notification.showErrorToast('Error: ' + message, 'top', 3000);
      }
    });
  }

  resetModal(): void{
    this.message.context = '';
    this.message.receiverUser = null;
  }
}
