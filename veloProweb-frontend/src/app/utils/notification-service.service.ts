import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  showSuccess(title: string, text: string): void {
    Swal.fire({
      icon: 'success',
      title: title,
      text: text,
      confirmButtonText: 'Aceptar',
      background: '#2c2c2c',
      color: '#ffffff',
    });
  }

  showError(title: string, text: string): void {
    Swal.fire({
      icon: 'error',
      title: title,
      text: text,
      confirmButtonText: 'Aceptar',
      background: '#2c2c2c',
      color: '#ffffff',
    });
  }

  showWarning(title: string, text: string): void {
    Swal.fire({
      icon: 'warning',
      title: title,
      text: text,
      confirmButtonText: 'Aceptar',
      background: '#2c2c2c',
      color: '#ffffff',
    });
  }

  showSuccessToast(title: string, showPosition: 'top-end' | 'top-start' | 'top' | 'bottom-end' | 'bottom-start' | 'bottom' | 'center' | 'center-start' | 'center-end', time: number): void {
    const Toast = Swal.mixin({
      toast: true,
      position: showPosition,
      showConfirmButton: false,
      timer: time,
      timerProgressBar: true,
      background: '#2c2c2c',
      color: '#ffffff',
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

  showErrorToast(title: string, showPosition: 'top-end' | 'top-start' | 'top' | 'bottom-end' | 'bottom-start' | 'bottom' | 'center' | 'center-start' | 'center-end', time: number): void {
    const Toast = Swal.mixin({
      toast: true,
      position: showPosition,    
      showConfirmButton: false,
      timer: time,
      timerProgressBar: true,
      background: '#2c2c2c',
      color: '#ffffff',
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

  showWarningToast(title: string, showPosition: 'top-end' | 'top-start' | 'top' | 'bottom-end' | 'bottom-start' | 'bottom' | 'center' | 'center-start' | 'center-end', time: number): void {
    const Toast = Swal.mixin({
      toast: true,
      position: showPosition,    
      showConfirmButton: false,
      timer: time,
      timerProgressBar: true,
      background: '#2c2c2c',
      color: '#ffffff',
      didOpen: (toast) => {
        toast.addEventListener('mouseenter', Swal.stopTimer);
        toast.addEventListener('mouseleave', Swal.resumeTimer);
      }
    });

    Toast.fire({
      icon: 'warning',
      title: title
    });
  }

  showConfirmation(title: string, text: string, confirmButton: string, cancelButton: string): Promise<any>{
    return Swal.fire({
            title: title,
            text: text,
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: confirmButton,
            cancelButtonText: cancelButton,
            background: '#2c2c2c',
            color: '#ffffff',
          })
  }

  showInputDialog(title: string, text: string, confirmButton: string, cancelButton: string): Promise<any> {
    return Swal.fire({
      title: title,
      text: text,
      input: 'text',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: confirmButton,
      cancelButtonText: cancelButton,
      background: '#2c2c2c',
      color: '#ffffff',
      inputValidator: (value) => {
        if (!value) {
          return '¡Por favor ingresa un valor!';
        }
        return null;
      }
    });
  }  
}
