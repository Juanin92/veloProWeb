import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ErrorMessageService {

  errorMessageExtractor(error: any): string {
    const errorObject = error?.error || error;
    const messages: string[] = [];

    if (errorObject && typeof errorObject === 'object') {
      for (const key in errorObject) {
        if (key.toLowerCase() !== 'status' && typeof errorObject[key] === 'string') {
          messages.push(errorObject[key].trim());
        }
      }
    } else if (typeof errorObject === 'string') {
      messages.push(errorObject.trim());
    }

    return messages.length > 0 ? messages.join('\n -') : 'Error desconocido';
  }
}
