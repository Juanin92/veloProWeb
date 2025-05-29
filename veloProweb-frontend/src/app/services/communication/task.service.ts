import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TaskRequestDTO } from '../../models/DTO/task-request-dto';
import { AuthService } from '../User/auth.service';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private apiUrl = 'http://localhost:8080/tareas';

  constructor(private httpClient: HttpClient, private auth: AuthService) { }

  getTasks(): Observable<TaskRequestDTO[]>{
    return this.httpClient.get<TaskRequestDTO[]>(this.apiUrl, {headers: this.auth.getAuthHeaders()});
  }

  createTask(task: TaskRequestDTO): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(this.apiUrl, task, {headers: this.auth.getAuthHeaders()});
  }

  completeTask(taskID: number): Observable<{message: string}>{
    return this.httpClient.put<{ message: string }>(this.apiUrl, {}, {
      params: { taskID: taskID.toString() },
      headers: this.auth.getAuthHeaders(),
    });  
  }

  getAllTasks(): Observable<TaskRequestDTO[]>{
    return this.httpClient.get<TaskRequestDTO[]>(`${this.apiUrl}/lista-tarea`);
  }
}
