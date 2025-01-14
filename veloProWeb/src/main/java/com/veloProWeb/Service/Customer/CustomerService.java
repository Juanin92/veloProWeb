package com.veloProWeb.Service.Customer;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Enum.PaymentStatus;
import com.veloProWeb.Repository.Customer.CustomerRepo;
import com.veloProWeb.Service.Customer.Interfaces.ICustomerService;
import com.veloProWeb.Utils.HelperService;
import com.veloProWeb.Validation.CustomerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService implements ICustomerService {

    @Autowired private CustomerRepo customerRepo;
    @Autowired private CustomerValidator validator;
    @Autowired private HelperService helperService;

    /**
     * Crea un nuevo cliente
     * Sé válida que el cliente a crear no haya registro de este en la DB
     * Se asigna un valor predeterminado a los emails que estén nulo o vacíos
     * @param customer contiene los detalles del cliente
     */
    @Override
    public void addNewCustomer(Customer customer) {
        Customer customerDB = getCostumerCreated(customer.getName(), customer.getSurname());
        if (customerDB != null){
            throw new IllegalArgumentException("Cliente Existente: Hay registro de este cliente.");
        }else {
            if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
                customer.setEmail("x@x.xxx");
            }
            validator.validate(customer);
            customer.setId(null);
            customer.setAccount(true);
            customer.setStatus(PaymentStatus.NULO);
            customer.setTotalDebt(0);
            customer.setDebt(0);
            customer.setName(helperService.capitalize(customer.getName()));
            customer.setSurname(helperService.capitalize(customer.getSurname()));
            customerRepo.save(customer);
        }
    }

    /**
     * Actualizar los datos de un cliente seleccionado
     * Sé válida que los nuevos datos del cliente a modificar no haya registro similar de este en la DB
     * Se asigna un valor predeterminado a los emails que estén nulo o vacíos
     * @param customer contiene los nuevos detalles del cliente
     */
    @Override
    public void updateCustomer(Customer customer) {
        Customer customerDB = getCostumerCreated(customer.getName(), customer.getSurname());
        if (customerDB != null && !customerDB.getId().equals(customer.getId())){
            throw new IllegalArgumentException("Cliente Existente: Hay registro de este cliente.");
        }else {
            if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
                customer.setEmail("x@x.xxx");
            }
            validator.validate(customer);
            customer.setName(helperService.capitalize(customer.getName()));
            customer.setSurname(helperService.capitalize(customer.getSurname()));
            customerRepo.save(customer);
        }
    }

    /**
     * Obtiene un cliente espécifico
     * @param ID - Identificador del cliente
     * @return - Objeto de cliente o una excepción si no encuentra nada
     */
    @Override
    public Customer getCustomerById(Long ID) {
        return customerRepo.findById(ID).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }

    /**
     *  Obtiene el registro de todos los clientes en la DB
     * @return lista de clientes.
     */
    @Override
    public List<Customer> getAll() {
        return customerRepo.findAll();
    }

    /**
     * Elimina un cliente desactivando su cuenta
     * Si la cuenta del cliente esta desactivada lanza una excepción
     * @param customer cliente seleccionado a eliminar
     */
    @Override
    public void delete(Customer customer) {
        if (customer.isAccount()){
            customer.setAccount(false);
            customerRepo.save(customer);
        }else {
            throw new IllegalArgumentException("Cliente ya ha sido eliminado anteriormente.");
        }
    }

    /**
     * Activa la cuenta de un cliente
     * Válida que cliente o su ID no sea nulo y si su cuenta ya está activa, lanza una excepción
     * @param customer Cliente a activar cuenta
     */
    @Override
    public void activeCustomer(Customer customer) {
        if (customer == null || customer.getId() == null) {
            throw new IllegalArgumentException("Cliente no válido, Null");
        }
        if (!customer.isAccount()){
            customer.setAccount(true);
            customerRepo.save(customer);
        }else{
            throw new IllegalArgumentException("El cliente tiene su cuenta activada");
        }
    }

    /**
     *  Realiza un pago a la deuda del cliente
     *  Válida el monto del pago y actualiza la deuda, luego asignando el estado correspondiente
     * @param customer Cliente que realiza el pago
     * @param amount monto a pagar
     */
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
        Optional<Customer> customerOptional = customerRepo.findBySimilarNameAndSurname(helperService.capitalize(name), helperService.capitalize(surname));
        return customerOptional.orElse(null);
    }
}
