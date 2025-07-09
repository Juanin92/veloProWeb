import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TaskForm } from '../../models/entity/communication/task-form';
import { AuthService } from '../user/auth.service';
import { Task } from '../../models/entity/communication/task';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private apiUrl = 'http://localhost:8080/tareas';

  constructor(private httpClient: HttpClient, private auth: AuthService) { }

  getTasks(): Observable<Task[]>{
    return this.httpClient.get<Task[]>(this.apiUrl, {headers: this.auth.getAuthHeaders()});
  }

  createTask(task: TaskForm): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(this.apiUrl, task, {headers: this.auth.getAuthHeaders()});
  }

  completeTask(taskID: number): Observable<{message: string}>{
    return this.httpClient.put<{ message: string }>(this.apiUrl, {}, {
      params: { taskID: taskID.toString() },
      headers: this.auth.getAuthHeaders(),
    });  
  }

  getAllTasks(): Observable<Task[]>{
    return this.httpClient.get<Task[]>(`${this.apiUrl}/lista-tarea`);
  }
}
