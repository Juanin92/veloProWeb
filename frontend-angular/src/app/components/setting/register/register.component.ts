import { Component, OnInit } from '@angular/core';
import { Record } from '../../../models/entity/reporting/record';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SettingPermissionsService } from '../../../services/permissions/setting-permissions.service';
import { RecordService } from '../../../services/reporting/record.service';

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
  sortEntryDate: boolean = true;
  sortEndDate: boolean = true;
  sortActionDate: boolean = true;
  sortPosition: boolean = true;

  constructor(private recordService: RecordService,
    protected permission: SettingPermissionsService){}

  ngOnInit(): void {
    this.loadRecords();
  }

  loadRecords(): void{
    this.recordService.getRecords().subscribe({
      next:(list)=>{
        this.recordList = list;
        this.filteredRecordList = list;
      }
    });
  }

  toggleSortEntryDate(): void{
    this.sortEntryDate = !this.sortEntryDate;
    this.filteredRecordList.sort((a, b) => {
      const dateA = new Date(a.entryDate).getTime();
      const dateB = new Date(b.entryDate).getTime();
      return this.sortEntryDate ? dateA - dateB : dateB - dateA;
    });
  }
  
  toggleSortEndDate(): void{
    this.sortEndDate = !this.sortEndDate;
    this.filteredRecordList.sort((a, b) => {
      const dateA = new Date(a.endDate).getTime();
      const dateB = new Date(b.endDate).getTime();
      return this.sortEndDate ? dateA - dateB : dateB - dateA;
    });
  }
  
  toggleSortActionDate(): void{
    this.sortActionDate = !this.sortActionDate;
    this.filteredRecordList.sort((a, b) => {
      const dateA = new Date(a.actionDate).getTime();
      const dateB = new Date(b.actionDate).getTime();
      return this.sortActionDate ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortPosition(): void{
    this.filteredRecordList.reverse();
    this.sortPosition = !this.sortPosition;
  }

  searchFilterRecords(): void {
    if (this.textFilter.trim() === '') {
      this.filteredRecordList = this.recordList;
    } else {
      this.filteredRecordList = this.recordList.filter(record =>
        record.user.toLowerCase().includes(this.textFilter.toLowerCase()) || 
        record.action.toLowerCase().includes(this.textFilter.toLowerCase())
      );
    }
  }
}
