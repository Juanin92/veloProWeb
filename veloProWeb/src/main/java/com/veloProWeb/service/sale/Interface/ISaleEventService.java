package com.veloProWeb.service.sale.Interface;

import com.veloProWeb.model.Enum.PaymentMethod;
import com.veloProWeb.model.entity.customer.Customer;

public interface ISaleEventService {

    void createSaleLoanPaymentEvent(Customer customer, int amount);
    void createSaleMixPaymentEvent(Customer customer, int amount);
    Customer needsCustomer(PaymentMethod paymentMethod, Long idCustomer);
}
