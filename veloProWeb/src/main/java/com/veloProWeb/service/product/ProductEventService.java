package com.veloProWeb.service.product;

import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.service.Report.IKardexService;
import com.veloProWeb.service.User.Interface.IAlertService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductEventService {

    private final IAlertService alertService;
    private final IKardexService kardexService;

    public void handleCreateRegister(Product product, String comment, UserDetails userDetails){
        kardexService.addKardex(userDetails, product, 0, comment, MovementsType.AJUSTE);
    }

    public void isChangeStockOriginalValue(Product product, int originalStock, ProductUpdatedRequestDTO dto,
                                           UserDetails userDetails) {
        if (originalStock != dto.getStock()) {
            String comment = String.format("%s - stock original: %s, stock nuevo: %s", dto.getComment(),
                    originalStock, dto.getStock());
            int quantity = Math.abs(originalStock - dto.getStock());
            kardexService.addKardex(userDetails, product, quantity, comment, MovementsType.AJUSTE);
            alertService.createAlert(product, comment);
        }
    }
}
