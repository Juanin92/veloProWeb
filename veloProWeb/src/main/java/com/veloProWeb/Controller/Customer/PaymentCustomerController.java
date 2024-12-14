package com.veloProWeb.Controller.Customer;

import com.veloProWeb.Model.Entity.Customer.PaymentCustomer;
import com.veloProWeb.Service.Customer.Interfaces.IPaymentCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentCustomerController {

    @Autowired
    private IPaymentCustomerService paymentCustomerService;

    @GetMapping
    public ResponseEntity<List<PaymentCustomer>> getAllPayment(){
        try {
            return ResponseEntity.ok(paymentCustomerService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/abonos")
    public ResponseEntity<List<PaymentCustomer>> getCustomerSelectedPayment(@RequestParam Long customerId){
        try {
            return ResponseEntity.ok(paymentCustomerService.getCustomerSelected(customerId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

