import { Injectable } from '@angular/core';
import * as XLSX from 'xlsx';
import * as FileSaver from 'file-saver';

@Injectable({
  providedIn: 'root'
})
export class ExcelService {

  constructor() { }

  /**
   * Método para generar y descargar un archivo Excel a partir de datos proporcionados.
   * @param data - Lista de datos para incluir en el archivo
   * @param type - nombre del reporte 
   */
  generateExcel(data: any[], type: string): void {
    //Convierte los datos en hoja de cálculo Excel
    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(data);
    //Crea un libro de Excel
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    //Agregar la hoja al libro
    XLSX.utils.book_append_sheet(wb, ws, 'Datos');
    //Escribe el libro en un buffer (memoria)
    const excelBuffer: any = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
    this.saveAsExcelFile(excelBuffer, type);
  }

  /**
   * Guardar un archivo Excel a partir de un buffer de datos.
   * @param buffer - Memoria con los datos
   * @param fileName - nombre del archivo
   */
  private saveAsExcelFile(buffer: any, fileName: string): void {
    // Crea un objeto Blob con el contenido del buffer y el tipo de archivo Excel
    const data: Blob = new Blob([buffer], { type: EXCEL_TYPE });
  
    //Obtener fecha actual y formatearla
    const now = new Date();
    const day = String(now.getDate()).padStart(2, '0');
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const year = now.getFullYear();
    const formattedDate = `${day}-${month}-${year}`;
  
    const fullFileName = `${fileName}-${formattedDate}.xlsx`;
    FileSaver.saveAs(data, fullFileName);
  }
  
}

const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
