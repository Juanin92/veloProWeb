import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModalService {

  private modalStatus = new Subject<boolean>();
  
  getModalStatus() {
    return this.modalStatus.asObservable();
  }

  openModal() {
    this.modalStatus.next(true);
  }

  closeModal() {
    this.modalStatus.next(false);
  }
}
