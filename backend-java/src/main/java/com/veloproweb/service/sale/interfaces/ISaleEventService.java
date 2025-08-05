package com.veloproweb.service.sale.interfaces;

import com.veloproweb.model.enums.PaymentMethod;
import com.veloproweb.model.entity.customer.Customer;

public interface ISaleEventService {

    void createSaleLoanPaymentEvent(Customer customer, int amount);
    void createSaleMixPaymentEvent(Customer customer, int amount);
    Customer needsCustomer(PaymentMethod paymentMethod, Long idCustomer);
}
