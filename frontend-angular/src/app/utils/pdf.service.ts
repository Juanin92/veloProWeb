import { Injectable } from '@angular/core';
import { jsPDF } from 'jspdf';
import { Sale } from '../models/entity/sale/sale';
import { SaleDetailResponse } from '../models/entity/sale/sale-detail-response';

@Injectable({
  providedIn: 'root'
})
export class PdfService {

  constructor() { }

  generatePDF(sale: Sale): void {
    const doc = new jsPDF();

    // Título de la boleta
    doc.setFontSize(20);
    doc.text('BOLETA', 105, 20, { align: 'center' });
    
    // Número de boleta
    doc.setFontSize(14);
    doc.text(`N°: ${sale.document.split('_')[1]}`, 105, 28, {align: 'center'});
    
    // Logo en la esquina superior derecha
    const urlLogo = 'assets/img/principalLogo.png';
    this.validateURLImage(urlLogo, doc);
    
    // Datos en una sola columna
    doc.setFontSize(10);
    let y = 40;
    const fields = [
      { label: 'Fecha:', value: `${new Date(sale.date).getDate().toString().padStart(2, '0')}-${(new Date(sale.date).getMonth() + 1).toString().padStart(2, '0')}-${new Date(sale.date).getFullYear()}` },
      { label: 'Cliente:', value: sale.customer },
      { label: 'Forma de Pago:', value: sale.paymentMethod },
      { label: 'Total:', value: `$${sale.totalSale.toFixed(0)}` },
      { label: 'Descuento:', value: `$${sale.discount.toFixed(0)}` },
      { label: 'Impuesto:', value: `$${sale.tax.toFixed(0)}` },
      { label: 'Deuda:', value: `$${(sale.totalSale - sale.discount).toFixed(0)}` },
      { label: 'Comentario:', value: sale.comment }
    ];

    const paymentMethodsToHideCustomer = ['PRESTAMO', 'MIXTO'];
    fields.forEach(field => {
      if (field.label === 'Cliente:' && !paymentMethodsToHideCustomer.includes(sale.paymentMethod.toString())) {
        return;
      }
      if (field.label === 'Comentario:' && sale.comment === null) {
        return;
      }
      if (field.label === 'Deuda:' && !paymentMethodsToHideCustomer.includes(sale.paymentMethod.toString())) {
        return;
      }
      doc.text(`${field.label} ${field.value}`, 7, y);
      y += 7;
    });

    // Tabla de productos
    const startY = y + 10;
    const startX = 10;
    const colWidths = [30, 80, 30];

    doc.setFontSize(12);
    doc.text('Cantidad', startX, startY);
    doc.text('Descripción', startX + colWidths[0], startY);
    doc.text('Precio', startX + colWidths[0] + colWidths[1], startY);
    doc.line(startX, startY + 2, startX + colWidths[0] + colWidths[1] + colWidths[2], startY + 2);

    let currentY = startY + 10;
    sale.saleDetails.forEach((detail: SaleDetailResponse) => {
      doc.text(detail.quantity.toString(), startX, currentY);
      doc.text(detail.descriptionProduct, startX + colWidths[0], currentY);
      doc.text(`$${detail.price.toFixed(0)}`, startX + colWidths[0] + colWidths[1], currentY);
      currentY += 10;
    });

    // Total general al final de la tabla
    doc.text('Total General:', startX + colWidths[0] + colWidths[1] + 10, currentY);
    doc.text(`$${sale.totalSale.toFixed(0)}`, startX + colWidths[0] + colWidths[1] + 50, currentY);

    // Campo de la empresa
    const companyDataString = sessionStorage.getItem('companyData');
    const companyData = companyDataString ? JSON.parse(companyDataString) : {};
    doc.setFontSize(10);
    const contactY = currentY + 20;
    doc.text(`Teléfono: ${companyData?.phone}`, 7, contactY);
    doc.text(`Email: ${companyData?.email}`, 7, contactY + 5);
    doc.text(`Dirección: ${companyData?.address}`, 7, contactY + 10);

    // Campo editable al final (sin label)
    doc.line(10, contactY + 40, 200, contactY + 40);
    doc.text('*Esta no es una boleta válida como comprobante', 105, contactY + 45, { align: 'center' });
    
    // Generar el PDF
    doc.save(`boleta_${sale.document}.pdf`);
  }

  validateURLImage(url: string, doc: jsPDF): void {
    if (!this.isValidUrl(url)) {
      console.error('URL de imagen no válida o potencialmente peligrosa:', url);
      return;
    }
  
    try {
      doc.addImage(url, "PNG", 160, 10, 40, 40);
    } catch (err) {
      console.error('Error al agregar la imagen:', err);
    }
  }
  
  // Método para validar URLs seguras
  isValidUrl(url: string): boolean {
    const regex = /^(https?:\/\/.*\.(?:png|jpg|jpeg|gif|svg))$/i;
    return regex.test(url);
  }  
}
