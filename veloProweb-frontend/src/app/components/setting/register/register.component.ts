import { Component, OnInit } from '@angular/core';
import { Record } from '../../../models/Entity/record';
import { RecordService } from '../../../services/record.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit{

  recordList: Record[] = [];
  filteredRecordList: Record[] = [];
  textFilter: string = '';

  constructor(private recordService: RecordService){}

  ngOnInit(): void {
    this.getRecords();
  }

  getRecords(): void{
    this.recordService.getRecords().subscribe({
      next:(list)=>{
        this.recordList = list;
        this.filteredRecordList = list;
      },error: (error)=>{
        console.log('No se encontró información sobre los registros');
      }
    });
  }

  searchFilterRecords(): void {
    if (this.textFilter.trim() === '') {
      this.filteredRecordList = this.recordList;
    } else {
      this.filteredRecordList = this.recordList.filter(record =>
        record.user.name.toLowerCase().includes(this.textFilter.toLowerCase()) || 
        record.user.surname.toLowerCase().includes(this.textFilter.toLowerCase()) || 
        record.user.username.toLowerCase().includes(this.textFilter.toLowerCase()) || 
        record.action.toLowerCase().includes(this.textFilter.toLowerCase())
      );
    }
  }
}
