import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Message } from '../../../models/Entity/message';
import { CommonModule } from '@angular/common';
import { MessageModalComponent } from "../message-modal/message-modal.component";
import { MessageService } from '../../../services/communication/message.service';

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

  constructor(private messageService: MessageService){}

  ngOnInit(): void {
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
}
