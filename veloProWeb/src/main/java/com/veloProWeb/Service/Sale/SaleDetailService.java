package com.veloProWeb.Service.Sale;

import com.veloProWeb.Model.DTO.DetailSaleDTO;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.Sale.SaleDetail;
import com.veloProWeb.Repository.Sale.SaleDetailRepo;
import com.veloProWeb.Service.Product.ProductService;
import com.veloProWeb.Service.Sale.Interface.ISaleDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleDetailService implements ISaleDetailService {

    @Autowired private SaleDetailRepo saleDetailRepo;
    @Autowired private ProductService productService;

    /**
     * Crear detalle de ventas proporcionadas
     * Busca el producto correspondiente en el sistema utilizando por ID.
     * Actualiza el stock del producto mediante el servicio de Producto
     * @param dtoList - Lista de objetos DTO que contienen los detalles de la venta.
     * @param sale - Objeto que representa la venta asociada a los detalles.
     * @throws IllegalArgumentException Si no se encuentra un producto con el ID proporcionado en alguno de los detalles.
     */
    @Override
    public void createSaleDetails(List<DetailSaleDTO> dtoList, Sale sale) {
        for (DetailSaleDTO dto : dtoList) {
            Product product = productService.getProductById(dto.getIdProduct());
            SaleDetail saleDetail = new SaleDetail();
            saleDetail.setId(null);
            saleDetail.setQuantity(dto.getQuantity());
            saleDetail.setPrice((int) (product.getSalePrice() * 1.19));
            saleDetail.setTax((int) (product.getSalePrice() * 0.19));
            saleDetail.setTotal((int) ((product.getSalePrice() * 1.19) * 2));
            saleDetail.setSale(sale);
            saleDetail.setProduct(product);
            saleDetailRepo.save(saleDetail);
            productService.updateStockSale(product, saleDetail.getQuantity());
        }
    }

    /**
     * Obtiene el registro de todos los detalles de ventas
     * @return - Lista con los detalles de ventas
     */
    @Override
    public List<SaleDetail> getAll() {
        return saleDetailRepo.findAll();
    }
}
