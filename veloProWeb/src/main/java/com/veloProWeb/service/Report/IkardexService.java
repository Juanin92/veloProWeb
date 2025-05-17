package com.veloProWeb.service.Report;

import com.veloProWeb.model.entity.Kardex;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.Enum.MovementsType;

import java.util.List;

public interface IkardexService {
    void addKardex(Product product, int quantity, String comment, MovementsType moves);
    List<Kardex> getAll();
    void checkLowSales(Product product);
}
