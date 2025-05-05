    package com.veloProWeb.service.customer;

    import com.veloProWeb.exceptions.Customer.*;
    import com.veloProWeb.mapper.CustomerMapper;
    import com.veloProWeb.model.dto.customer.CustomerDTO;
    import com.veloProWeb.model.dto.customer.CustomerResponseDTO;
    import com.veloProWeb.model.entity.customer.Customer;
    import com.veloProWeb.model.Enum.PaymentStatus;
    import com.veloProWeb.repository.customer.CustomerRepo;
    import com.veloProWeb.service.customer.interfaces.ICustomerService;
    import com.veloProWeb.util.HelperService;
    import com.veloProWeb.validation.CustomerValidator;
    import lombok.AllArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;
    import java.util.Optional;

    @Service
    @AllArgsConstructor
    public class CustomerService implements ICustomerService {

        private final CustomerRepo customerRepo;
        private final CustomerValidator validator;
        private final HelperService helperService;
        private final CustomerMapper mapper;

        /**
         * Crea un nuevo cliente
         * Sé válida que el cliente a crear no haya registro de este en la DB
         * Se asigna un valor predeterminado a los emails que estén nulo o vacíos
         * @param dto contiene los detalles del cliente
         */
        @Transactional
        @Override
        public void addNewCustomer(CustomerDTO dto) {
            Customer customerDB = getCostumerCreated(dto.getName(), dto.getSurname());
            validator.existCustomer(customerDB);
            Customer customer = mapper.toEntity(dto);
            if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
                customer.setEmail("x@x.xxx");
            }
            customer.setId(null);
            customer.setAccount(true);
            customer.setStatus(PaymentStatus.NULO);
            customer.setTotalDebt(0);
            customer.setDebt(0);
            validator.validateInfoCustomer(customer);
            customerRepo.save(customer);
        }

        /**
         * Actualizar los datos de un cliente seleccionado
         * Sé válida que los nuevos datos del cliente a modificar no haya registro similar de este en la DB
         * Se asigna un valor predeterminado a los emails que estén nulo o vacíos
         * @param dto contiene los nuevos detalles del cliente
         */
        @Transactional
        @Override
        public void updateCustomer(CustomerDTO dto) {
            Customer customerDB = getCustomerById(dto.getId());
            mapper.updateCustomerFromDto(dto, customerDB);
            if (customerDB.getEmail() == null || customerDB.getEmail().isEmpty()) {
                customerDB.setEmail("x@x.xxx");
            }
            validator.validateInfoCustomer(customerDB);
            customerRepo.save(customerDB);
        }

        /**
         * Obtiene un cliente espécifico
         * @param ID - Identificador del cliente
         * @return - Objeto de cliente o una excepción si no encuentra nada
         */
        @Override
        public Customer getCustomerById(Long ID) {
            return customerRepo.findById(ID).orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado"));
        }

        /**
         *  Obtiene el registro de todos los clientes en la DB
         * @return lista de clientes.
         */
        @Override
        public List<CustomerResponseDTO> getAll() {
            return customerRepo.findAll().stream()
                    .map(mapper::toResponseDTO)
                    .toList();
        }

        /**
         * Elimina un cliente desactivando su cuenta
         * Si la cuenta del cliente esta desactivada lanza una excepción
         * @param dto - cliente seleccionado a eliminar
         */
        @Transactional
        @Override
        public void delete(CustomerDTO dto) {
            Customer customer = getCustomerById(dto.getId());
            validator.deleteCustomer(customer);
            customer.setAccount(false);
            customerRepo.save(customer);
        }

        /**
         * Activa la cuenta de un cliente
         * Válida que cliente o su ID no sea nulo y si su cuenta ya está activa, lanza una excepción
         * @param dto Cliente a activar cuenta
         */
        @Transactional
        @Override
        public void activeCustomer(CustomerDTO dto) {
            Customer customer = getCustomerById(dto.getId());
            validator.isActive(customer);
            customer.setAccount(true);
            customerRepo.save(customer);
        }

        /**
         *  Realiza un pago a la deuda del cliente
         *  Válida el monto del pago y actualiza la deuda, luego asignando el estado correspondiente
         * @param customer Cliente que realiza el pago
         * @param amount monto a pagar
         */
        @Transactional
        @Override
        public void paymentDebt(Customer customer, String amount) {
            int number = Integer.parseInt(amount);
            validator.validateValuePayment(number, customer);
            customer.setDebt(customer.getDebt() - number);
            customerRepo.save(customer);
            statusAssign(customer);
        }

        /**
         * Asigna el estado correspondiente al cliente dependiendo en su deuda total y actual
         * @param customer cliente que debe actualizar su estado
         */
        @Override
        public void statusAssign(Customer customer) {
            if (customer.getTotalDebt() == 0) {
                customer.setStatus(PaymentStatus.NULO);
            }else if (customer.getDebt() == 0 && customer.getTotalDebt() > 0) {
                customer.setStatus(PaymentStatus.PAGADA);
            } else if (customer.getDebt() <= (customer.getTotalDebt() / 2) && customer.getDebt() > 0) {
                customer.setStatus(PaymentStatus.PARCIAL);
            } else if (customer.getDebt() > 0 && customer.getTotalDebt() > 0) {
                customer.setStatus(PaymentStatus.PENDIENTE);
            }
            customerRepo.save(customer);
        }

        /**
         * Agrega una venta a la deuda del cliente
         * Actualiza la deuda total a la deuda y se asigna un nuevo estado
         * @param customer cliente al que se le agrega la venta
         */
        @Transactional
        @Override
        public void addSaleToCustomer(Customer customer) {
            customer.setDebt(customer.getTotalDebt());
            customerRepo.save(customer);
            statusAssign(customer);
        }

        /**
         * Actualiza el total de la deuda del cliente
         * @param customer cliente que se actualizara la deuda total
         */
        @Transactional
        @Override
        public void updateTotalDebt(Customer customer) {
            customerRepo.save(customer);
        }

        /**
         * Busca un cliente ya creado por su nombre y apellido dados
         * Utiliza el repositorio para encontrar un cliente con un nombre y apellido similar, capitalizados correctamente.
         * @param name nombre del cliente
         * @param surname apellido del cliente
         * @return cliente encontrado o null si no encuentra similitud
         */
        private Customer getCostumerCreated(String name, String surname) {
            Optional<Customer> customerOptional = customerRepo.findBySimilarNameAndSurname(
                    helperService.capitalize(name), helperService.capitalize(surname));
            return customerOptional.orElse(null);
        }
    }
