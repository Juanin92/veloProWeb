package com.veloProWeb.mapper;

import com.veloProWeb.model.Enum.DispatchStatus;
import com.veloProWeb.model.dto.DetailSaleDTO;
import com.veloProWeb.model.dto.sale.DispatchRequestDTO;
import com.veloProWeb.model.dto.sale.DispatchResponseDTO;
import com.veloProWeb.model.entity.Sale.Dispatch;
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

    public DispatchResponseDTO toResponseDTO(Dispatch dispatch, List<DetailSaleDTO> saleDTOList){
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
                .detailSaleDTOS(saleDTOList)
                .build();
    }
}
