package com.veloProWeb.Controller.Purchase;

import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseDetailService;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compras")
@CrossOrigin(origins = "http://localhost:4200")
public class PurchaseController {

    @Autowired private IPurchaseService purchaseService;
    @Autowired private IPurchaseDetailService purchaseDetailService;


}
