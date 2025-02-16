package com.veloProWeb.Service.Report;

import com.veloProWeb.Model.Entity.Kardex;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Enum.MovementsType;

import java.util.List;

public interface IkardexService {
    void addKardex(Product product, int quantity, String comment, MovementsType moves);
    List<Kardex> getAll();
    void checkLowSales(Product product);
}
