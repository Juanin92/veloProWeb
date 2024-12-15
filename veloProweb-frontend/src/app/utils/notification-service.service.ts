import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class NotificationServiceService {

  constructor() { }

  showSuccess(title: string, text: string): void {
    Swal.fire({
      icon: 'success',
      title: title,
      text: text,
      confirmButtonText: 'Aceptar'
    });
  }

  showError(title: string, text: string): void {
    Swal.fire({
      icon: 'error',
      title: title,
      text: text,
      confirmButtonText: 'Aceptar'
    });
  }

  showWarning(title: string, text: string): void {
    Swal.fire({
      icon: 'warning',
      title: title,
      text: text,
      confirmButtonText: 'Aceptar'
    });
  }

  showSuccessToast(title: string): void {
    const Toast = Swal.mixin({
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 3000,
      timerProgressBar: true,
      didOpen: (toast) => {
        toast.addEventListener('mouseenter', Swal.stopTimer);
        toast.addEventListener('mouseleave', Swal.resumeTimer);
      }
    });

    Toast.fire({
      icon: 'success',
      title: title
    });
  }

  showErrorToast(title: string): void {
    const Toast = Swal.mixin({
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 3000,
      timerProgressBar: true,
      didOpen: (toast) => {
        toast.addEventListener('mouseenter', Swal.stopTimer);
        toast.addEventListener('mouseleave', Swal.resumeTimer);
      }
    });

    Toast.fire({
      icon: 'error',
      title: title
    });
  }
}
