import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../../utils/notification-service.service';
import { UserService } from '../../../services/user/user.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserResponse } from '../../../models/entity/user/user-response';
import { MessageService } from '../../../services/communication/message.service';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { MessageForm } from '../../../models/entity/communication/message-form';

@Component({
  selector: 'app-message-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './message-modal.component.html',
  styleUrl: './message-modal.component.css'
})
export class MessageModalComponent implements OnInit{

  userList: UserResponse[] = [];
  message: MessageForm = {
    context: '',
    receiverUser: ''
  };

  constructor(
    private messageService: MessageService,
    private userService: UserService,
    private notification: NotificationService,
    private errorMessage: ErrorMessageService){}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void{
    this.userService.getListUsers().subscribe({
      next:(list) =>{
        this.userList = list;
      }
    })
  }

  sendMessage(message: MessageForm): void{
    if(!message.receiverUser || !message.context){
      this.notification.showWarningToast('Debe seleccionar un destinatario', 'top', 3000);
      return;
    }
    this.messageService.sendMessage(message).subscribe({
      next: (response)=>{
        this.notification.showSuccessToast(response.message, 'top', 3000);
        this.resetModal();
      },error: (error)=>{
        const message = this.errorMessage.errorMessageExtractor(error);
        this.notification.showErrorToast('Error: ' + message, 'top', 3000);
      }
    });
  }

  resetModal(): void{
    this.message.context = '';
    this.message.receiverUser = '';
  }
}
