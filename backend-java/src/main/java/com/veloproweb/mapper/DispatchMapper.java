package com.veloproweb.mapper;

import com.veloproweb.model.enums.DispatchStatus;
import com.veloproweb.model.dto.sale.DispatchRequestDTO;
import com.veloproweb.model.dto.sale.DispatchResponseDTO;
import com.veloproweb.model.dto.sale.SaleDetailResponseDTO;
import com.veloproweb.model.entity.sale.Dispatch;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DispatchMapper {

    public Dispatch toEntity(DispatchRequestDTO dto, Long dispatchCount){
        return Dispatch.builder()
                .trackingNumber("#" + (dispatchCount + 1))
                .created(LocalDate.now())
                .status(DispatchStatus.PREPARING)
                .address(dto.getAddress())
                .comment(dto.getComment())
                .customer(dto.getCustomer())
                .deliveryDate(null)
                .hasSale(false)
                .build();
    }

    public DispatchResponseDTO toResponseDTO(Dispatch dispatch, List<SaleDetailResponseDTO> saleDTOList){
        return DispatchResponseDTO.builder()
                .id(dispatch.getId())
                .trackingNumber(dispatch.getTrackingNumber())
                .status(dispatch.getStatus())
                .address(dispatch.getAddress())
                .comment(dispatch.getComment())
                .customer(dispatch.getCustomer())
                .hasSale(dispatch.isHasSale())
                .created(dispatch.getCreated())
                .deliveryDate(dispatch.getDeliveryDate())
                .saleDetails(saleDTOList)
                .build();
    }

    public DispatchResponseDTO toResponseDTO(Dispatch dispatch) {
        List<SaleDetailResponseDTO> saleDTOList = dispatch.getSaleDetails().stream()
                .map(saleDetail -> {
                    SaleDetailResponseDTO dto = new SaleDetailResponseDTO();
                    dto.setIdProduct(saleDetail.getProduct().getId());
                    dto.setDescriptionProduct(saleDetail.getProduct().getDescription());
                    dto.setQuantity(saleDetail.getQuantity());
                    dto.setPrice(saleDetail.getPrice());
                    dto.setTax(saleDetail.getTax());
                    dto.setHasDispatch(true);
                    return dto;
                })
                .toList();
        return toResponseDTO(dispatch, saleDTOList);
    }
}
