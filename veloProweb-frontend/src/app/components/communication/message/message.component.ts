import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Message } from '../../../models/entity/communication/message';
import { CommonModule } from '@angular/common';
import { MessageModalComponent } from "../message-modal/message-modal.component";
import { MessageService } from '../../../services/communication/message.service';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { NotificationService } from '../../../utils/notification-service.service';

@Component({
  selector: 'app-message',
  standalone: true,
  imports: [CommonModule, MessageModalComponent],
  templateUrl: './message.component.html',
  styleUrl: './message.component.css'
})
export class MessageComponent implements OnInit{
  @Output() messagesUpdated = new EventEmitter<Message[]>();
  messageList: Message[] = [];

  constructor(
    private messageService: MessageService,
    private errorMessage: ErrorMessageService,
    private notification: NotificationService){}

  ngOnInit(): void {
    this.loadUserMessages();
  }
  
  loadUserMessages(): void{
    this.messageService.getMessagesByUser().subscribe({
      next:(list)=>{
        this.messageList = list;
        const filteredList = list.filter(message => !message.read)
        this.messagesUpdated.emit(filteredList);
      }
    });
  }

  markMessageAsRead(message: Message): void{
    if(!message.read && !message.delete){
      this.messageService.markMessageAsRead(message.id).subscribe({
        next:(response)=>{
          this.loadUserMessages();
        },error: (error)=>{
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(message, 'top', 3000);
        }
      });
    }else{
      this.notification.showWarningToast('Mensaje se encuentra eliminado o leído.', 'top', 3000);
    }
  }

  markMessageAsDeleted(message: Message): void{
    if(message.read){
      this.messageService.markMessageAsDeleted(message.id).subscribe({
        next:(response)=>{
          message.delete = true;
          setTimeout(() => {
            this.loadUserMessages();
          }, 3000);
        },error: (error)=>{
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(message, 'top', 3000);
        }
      });
    }else{
      this.notification.showWarningToast('Mensaje no se encuentra leído.', 'top', 3000);
    }
  }
}
