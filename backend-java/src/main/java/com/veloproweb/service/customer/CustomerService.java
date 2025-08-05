    package com.veloproweb.service.customer;

    import com.veloproweb.exceptions.customer.*;
    import com.veloproweb.mapper.CustomerMapper;
    import com.veloproweb.model.dto.customer.CustomerRequestDTO;
    import com.veloproweb.model.dto.customer.CustomerResponseDTO;
    import com.veloproweb.model.entity.customer.Customer;
    import com.veloproweb.model.enums.PaymentStatus;
    import com.veloproweb.repository.customer.CustomerRepo;
    import com.veloproweb.service.customer.interfaces.ICustomerService;
    import com.veloproweb.util.TextFormatter;
    import com.veloproweb.validation.CustomerValidator;
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
        private final CustomerMapper mapper;

        /**
         * Crea un nuevo cliente
         * Se valida que el cliente a crear no haya registro de este en la DB
         * Se asigna un valor predeterminado a los emails que estén nulo o vacíos
         * @param dto contiene los detalles del cliente
         */
        @Transactional
        @Override
        public void addNewCustomer(CustomerRequestDTO dto) {
            Customer customerDB = getCustomerCreated(dto.getName(), dto.getSurname());
            validator.existCustomer(customerDB);
            Customer customer = mapper.toEntity(dto);
            assignDefaultEmail(customer);
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
         * Se valida que los nuevos datos del cliente a modificar no haya registro similar de este en la DB
         * Se asigna un valor predeterminado a los emails que estén nulo o vacíos
         * @param dto contiene los nuevos detalles del cliente
         */
        @Transactional
        @Override
        public void updateCustomer(CustomerRequestDTO dto) {
            Customer customerDB = getCustomerById(dto.getId());
            mapper.updateCustomerFromDto(dto, customerDB);
            assignDefaultEmail(customerDB);
            validator.validateInfoCustomer(customerDB);
            customerRepo.save(customerDB);
        }

        /**
         * Obtiene un cliente específico
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
        public void delete(CustomerRequestDTO dto) {
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
        public void activeCustomer(CustomerRequestDTO dto) {
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
            statusAssign(customer);
            customerRepo.save(customer);
        }

        /**
         * Asigna el estado correspondiente al cliente dependiendo en su deuda total y actual
         * @param customer cliente que debe actualizar su estado
         */
        @Override
        public void statusAssign(Customer customer) {
            if (customer.getTotalDebt() == 0) {
                customer.setStatus(PaymentStatus.NULO);
                return;
            }
            if (customer.getDebt() == 0 && customer.getTotalDebt() > 0) {
                customer.setStatus(PaymentStatus.PAGADA);
                return;
            }
            if (customer.getDebt() <= (customer.getTotalDebt() / 2) && customer.getDebt() > 0) {
                customer.setStatus(PaymentStatus.PARCIAL);
                return;
            }
            if (customer.getDebt() > 0 && customer.getTotalDebt() > 0) {
                customer.setStatus(PaymentStatus.PENDIENTE);
            }
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
            statusAssign(customer);
            customerRepo.save(customer);
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
        private Customer getCustomerCreated(String name, String surname) {
            Optional<Customer> customerOptional = customerRepo.findBySimilarNameAndSurname(
                    TextFormatter.capitalize(name), TextFormatter.capitalize(surname));
            return customerOptional.orElse(null);
        }

        /**
         * Asigna un valor predeterminado al email del cliente si este es nulo o vacio
         * @param customer - cliente a asignar el email
         */
        private void assignDefaultEmail(Customer customer){
            if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
                customer.setEmail("x@x.xxx");
            }
        }
    }
