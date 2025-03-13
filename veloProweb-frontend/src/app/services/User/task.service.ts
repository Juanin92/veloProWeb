import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Task } from '../../models/Entity/task';
import { TaskRequestDTO } from '../../models/DTO/task-request-dto';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private apiUrl = 'http://localhost:8080/tareas';

  constructor(private httpClient: HttpClient) { }

  getTasks(userID: number): Observable<Task[]>{
    return this.httpClient.get<Task[]>(this.apiUrl, {params: {userId: userID.toString()}});
  }

  createTask(task: TaskRequestDTO): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(this.apiUrl, task);
  }

  completeTask(taskID: number): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(this.apiUrl, {}, {params: {taskID: taskID.toString()}});
  }

  getAllTasks(): Observable<TaskRequestDTO[]>{
    return this.httpClient.get<TaskRequestDTO[]>(`${this.apiUrl}/lista-tarea`);
  }
}
