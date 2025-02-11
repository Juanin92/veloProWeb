import { Injectable } from '@angular/core';
import { jsPDF } from 'jspdf';

@Injectable({
  providedIn: 'root'
})
export class PdfService {

  constructor() { }

  generatePDF(saleDetails: any): void {
    const doc = new jsPDF();

    // Título de la boleta
    doc.setFontSize(20);
    doc.text('BOLETA', 105, 20, { align: 'center' });
    
    // Número de boleta
    doc.setFontSize(14);
    doc.text(`N°: ${saleDetails.document.split('_')[1]}`, 105, 28, {align: 'center'});
    
    // Logo en la esquina superior derecha
    const urlLogo = 'assets/img/principalLogo.png';
    doc.addImage(urlLogo, 'PNG', 160, 10, 40, 40);
    
    // Datos en una sola columna
    doc.setFontSize(10);
    let y = 40;
    const fields = [
      { label: 'Fecha:', value: `${new Date(saleDetails.date).getDate().toString().padStart(2, '0')}-${(new Date(saleDetails.date).getMonth() + 1).toString().padStart(2, '0')}-${new Date(saleDetails.date).getFullYear()}` },
      { label: 'Cliente:', value: saleDetails.customer },
      { label: 'Forma de Pago:', value: saleDetails.paymentMethod },
      { label: 'Total:', value: `$${saleDetails.totalSale.toFixed(0)}` },
      { label: 'Descuento:', value: `$${saleDetails.discount.toFixed(0)}` },
      { label: 'Impuesto:', value: `$${saleDetails.tax.toFixed(0)}` },
      { label: 'Deuda:', value: `$${(saleDetails.totalSale - saleDetails.discount + saleDetails.tax).toFixed(0)}` },
      { label: 'Comentario:', value: saleDetails.comment }
    ];

    const paymentMethodsToHideCustomer = ['PRESTAMO', 'MIXTO'];
    fields.forEach(field => {
      if (field.label === 'Cliente:' && !paymentMethodsToHideCustomer.includes(saleDetails.paymentMethod)) {
        return;
      }
      if (field.label === 'Comentario:' && saleDetails.comment.toString().includes('null')) {
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
    saleDetails.items.forEach((item: any) => {
      doc.text(item.quantity.toString(), startX, currentY);
      doc.text(item.description.toString(), startX + colWidths[0], currentY);
      doc.text(`$${item.price.toFixed(0)}`, startX + colWidths[0] + colWidths[1], currentY);
      currentY += 10;
    });

    // Total general al final de la tabla
    doc.text('Total General:', startX + colWidths[0] + colWidths[1] + 10, currentY);
    doc.text(`$${saleDetails.totalSale.toFixed(0)}`, startX + colWidths[0] + colWidths[1] + 50, currentY);

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
    doc.save(`boleta_${saleDetails.document}.pdf`);
  }
}
