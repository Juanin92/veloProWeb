package com.veloProWeb.mapper;

import com.veloProWeb.model.dto.customer.CustomerRequestDTO;
import com.veloProWeb.model.dto.customer.CustomerResponseDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.util.HelperService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomerMapper {

    private final HelperService helperService;

    public Customer toEntity(CustomerRequestDTO dto) {
        return Customer.builder()
                .id(dto.getId())
                .name(helperService.capitalize(dto.getName()))
                .surname(helperService.capitalize(dto.getSurname()))
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();
    }

    public CustomerResponseDTO toResponseDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .debt(customer.getDebt())
                .totalDebt(customer.getTotalDebt())
                .status(customer.getStatus())
                .account(customer.isAccount())
                .build();
    }

    public void updateCustomerFromDto(CustomerRequestDTO dto, Customer customer) {
        customer.setPhone(dto.getPhone());
        customer.setName(helperService.capitalize(dto.getName()));
        customer.setSurname(helperService.capitalize(dto.getSurname()));
        customer.setEmail(dto.getEmail());
    }
}
