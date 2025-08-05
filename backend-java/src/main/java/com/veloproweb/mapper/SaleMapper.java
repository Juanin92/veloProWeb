package com.veloproweb.mapper;

import com.veloproweb.model.enums.PaymentMethod;
import com.veloproweb.model.dto.sale.*;
import com.veloproweb.model.entity.sale.Dispatch;
import com.veloproweb.model.entity.sale.Sale;
import com.veloproweb.model.entity.sale.SaleDetail;
import com.veloproweb.model.entity.customer.Customer;
import com.veloproweb.model.entity.product.Product;
import com.veloproweb.util.IdentifyDocumentGenerator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SaleMapper {

    public Sale toSaleEntity(SaleRequestDTO dto, String document, PaymentMethod paymentMethod, String comment,
                             Customer customer){
        return Sale.builder()
                .date(LocalDate.now())
                .document(IdentifyDocumentGenerator.generateIdentifyDocumentSale(document))
                .discount(dto.getDiscount())
                .status(true)
                .tax(dto.getTax())
                .totalSale(dto.getTotal())
                .paymentMethod(paymentMethod)
                .comment(comment)
                .customer(customer)
                .build();
    }

    public SaleDetail toSaleDetailEntityFromSale(SaleDetailRequestDTO dto, Product product, Sale sale){
        return SaleDetail.builder()
                .product(product)
                .quantity(dto.getQuantity())
                .price((int) (product.getSalePrice() * 1.19))
                .tax((int) (product.getSalePrice() * 0.19))
                .total((int) ((product.getSalePrice() * 1.19) * dto.getQuantity()))
                .sale(sale)
                .dispatch(null)
                .build();

    }

    public SaleDetail toSaleDetailEntityFromDispatch(SaleDetailRequestDTO dto, Product product, Dispatch dispatch){
        return SaleDetail.builder()
                .product(product)
                .quantity(dto.getQuantity())
                .price((int) (product.getSalePrice() * 1.19))
                .tax((int) (product.getSalePrice() * 0.19))
                .total((int) ((product.getSalePrice() * 1.19) * dto.getQuantity()))
                .sale(null)
                .dispatch(dispatch)
                .build();

    }

    public SaleResponseDTO toResponseDTO(Sale sale){
        return SaleResponseDTO.builder()
                .date(sale.getDate())
                .paymentMethod(sale.getPaymentMethod())
                .document(sale.getDocument())
                .comment(sale.getComment())
                .discount(sale.getDiscount())
                .tax(sale.getTax())
                .totalSale(sale.getTotalSale())
                .status(sale.isStatus())
                .customer(sale.getCustomer() == null ?
                        "Sin Cliente" : 
                        String.format("%s %s", sale.getCustomer().getName(), sale.getCustomer().getSurname()))
                .notification(null)
                .ticketStatus(true)
                .saleDetails(
                        sale.getSaleDetails().stream()
                                .map(this::toDetailResponseDTO)
                                .toList()
                )
                .build();
    }

    public SaleDetailResponseDTO toDetailResponseDTO(SaleDetail saleDetail){
        return SaleDetailResponseDTO.builder()
                .idProduct(saleDetail.getProduct().getId())
                .descriptionProduct(saleDetail.getProduct().getDescription())
                .quantity(saleDetail.getQuantity())
                .price(saleDetail.getPrice())
                .tax(saleDetail.getTax())
                .hasDispatch(saleDetail.getDispatch() != null)
                .build();
    }
}
