package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.PurchaseDetail;
import com.veloProWeb.Repository.Purchase.PurchaseDetailRepo;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PurchaseDetailService implements IPurchaseDetailService {

    @Autowired private PurchaseDetailRepo purchaseDetailRepo;

    /**
     * Crea un nuevo detalle de compra.
     * Inicializa y registra el detalle de compra en la BD
     * @param dto - DTO que contiene los detalles de la compra
     * @param purchase - Compra relacionada con el detalle
     * @param product - Producto relacionado con la compra
     */
    @Override
    public void createDetailPurchase(DetailPurchaseDTO dto, Purchase purchase, Product product) {
        PurchaseDetail purchaseDetail =  new PurchaseDetail();
        purchaseDetail.setPrice(dto.getPrice());
        purchaseDetail.setQuantity(dto.getQuantity());
        purchaseDetail.setTax(dto.getTax());
        purchaseDetail.setTotal(dto.getTotal());
        purchaseDetail.setProduct(product);
        purchaseDetail.setPurchase(purchase);
        purchaseDetailRepo.save(purchaseDetail);
    }

    /**
     * Obtiene el registro de todos los detalles de compras
     * @return - Lista con los detalles de compras
     */
    @Override
    public List<PurchaseDetail> getAll() {
        return purchaseDetailRepo.findAll();
    }

    /**
     * Crear un DTO con los detalles necesarios de un producto
     * @param product - Producto relacionado con el detalle de compra
     * @return - Retorna un objeto DTO con valores de producto o nulo sea el caso
     */
    @Override
    public DetailPurchaseDTO createDTO(Product product) {
        if (product != null) {
            DetailPurchaseDTO dto =new DetailPurchaseDTO();
            dto.setIdProduct(product.getId());
            dto.setBrand(product.getBrand().getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(0);
            dto.setTax(0);
            dto.setTotal(0);
            dto.setQuantity(0);
            return dto;
        }
        return null;
    }

    /**
     * Elimina un producto de la lista de detalles de compras
     * Busca un producto en la lista de detalles de compra por su ID y lo elimina si lo encuentra.
     * @param id - Identificador del producto a borrar
     * @param dtoList - lista de detalle de compra donde buscar
     * @return - True si el producto fue encontrado y eliminado, false en caso contrario.
     */
    @Override
    public boolean deleteProduct(Long id, List<DetailPurchaseDTO> dtoList) {
        Optional<DetailPurchaseDTO> optionalDto = dtoList.stream()
                .filter(dto -> Objects.equals(dto.getIdProduct(), id))
                .findFirst();

        if (optionalDto.isPresent()) {
            dtoList.remove(optionalDto.get());
            return true;
        }
        return false;
    }
}
