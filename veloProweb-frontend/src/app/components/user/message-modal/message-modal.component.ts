import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MessageService } from '../../../services/User/message.service';
import { Message } from '../../../models/Entity/message';
import { NotificationService } from '../../../utils/notification-service.service';
import { UserService } from '../../../services/User/user.service';
import { User } from '../../../models/Entity/user';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-message-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './message-modal.component.html',
  styleUrl: './message-modal.component.css'
})
export class MessageModalComponent implements OnInit{

  @Output() messagesUpdated = new EventEmitter<Message[]>();
  userList: User[] = [];
  messageList: Message[] = [];
  message: Message = {
    id: 0,
    context: '',
    created: '',
    read: false,
    delete: false,
    senderUser: 0,
    senderName: '',
    receiverUser: 0
  };

  constructor(
    private messageService: MessageService,
    private userService: UserService,
    private notification: NotificationService){}

  ngOnInit(): void {
    this.getUsers();
    this.getMessages();
  }

  getMessages(): void{
    this.messageService.getMessages(1).subscribe({
      next:(list)=>{
        this.messageList = list;
        const filteredList = list.filter(message => !message.read)
        this.messagesUpdated.emit(filteredList);
      }
    });
  }

  getUsers(): void{
    this.userService.getListUsers().subscribe({
      next:(list) =>{
        this.userList = list;
      }
    })
  }

  sendMessage(message: Message): void{
    message.senderUser = 1;
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

  isReadMessage(message: Message): void{
    if(!message.read && !message.delete){
      this.messageService.readMessage(message).subscribe({
        next:(response)=>{
          const message = response.message;
          console.log('Ok: ', message);
          this.getMessages();
        },error: (error)=>{
          const message = error.error?.error || error.error.message;
          console.log('Error: ', message);
        }
      });
    }else{
      console.log('Mensaje se encuentra eliminado o leído.');
    }
  }

  isDeleteMessage(message: Message): void{
    if(message.read){
      this.messageService.deleteMessage(message).subscribe({
        next:(response)=>{
          const messageResponse = response.message;
          console.log('Ok: ', messageResponse);
          message.delete = true;
          setTimeout(() => {
            this.getMessages();
          }, 3000);
        },error: (error)=>{
          const message = error.error?.error || error.error.message;
          console.log('Error: ', message);
        }
      });
    }else{
      console.log('Mensaje no se encuentra leído.');
    }
  }

  resetModal(): void{
    this.message.context = '';
    this.message.receiverUser = 0;
  }
}
