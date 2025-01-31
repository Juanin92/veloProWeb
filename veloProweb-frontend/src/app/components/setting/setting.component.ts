import { Component, OnInit } from '@angular/core';
import { RecordService } from '../../services/record.service';
import { Record } from '../../models/Entity/record';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-setting',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './setting.component.html',
  styleUrl: './setting.component.css'
})
export class SettingComponent implements OnInit{

  recordList: Record[] = [];

  constructor(private recordService: RecordService){}

  ngOnInit(): void {
    this.getRecords();
  }

  getRecords(): void{
    this.recordService.getRecords().subscribe({
      next:(list)=>{
        this.recordList = list;
      },
      error: (error)=>{
        console.log('No se encontró información sobre los registros')
      }
    });
  }
}
