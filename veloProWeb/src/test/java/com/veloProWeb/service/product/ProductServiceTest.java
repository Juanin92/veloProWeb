package com.veloProWeb.service.product;

import com.veloProWeb.exceptions.product.ProductAlreadyActivatedException;
import com.veloProWeb.exceptions.product.ProductAlreadyDeletedException;
import com.veloProWeb.mapper.ProductMapper;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.model.dto.product.ProductRequestDTO;
import com.veloProWeb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloProWeb.model.entity.product.*;
import com.veloProWeb.model.Enum.StatusProduct;
import com.veloProWeb.repository.product.ProductRepo;
import com.veloProWeb.service.Report.IkardexService;
import com.veloProWeb.service.User.Interface.IAlertService;
import com.veloProWeb.validation.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks private ProductService productService;
    @Mock private ProductRepo productRepo;
    @Mock private ProductValidator validator;
    @Mock private IAlertService alertService;
    @Mock private IkardexService kardexService;
    @Spy private ProductMapper mapper = new ProductMapper();
    private Product product;
    private BrandProduct brand;
    private UnitProduct unit;
    private CategoryProduct category;
    private SubcategoryProduct subcategory;

    @BeforeEach
    void setUp(){
        brand = BrandProduct.builder().id(1L).name("Sony").build();
        unit = UnitProduct.builder().id(1L).nameUnit("1 UN").build();
        category = CategoryProduct.builder().id(1L).name("Tech").build();
        subcategory = SubcategoryProduct.builder().id(1L).name("TV").category(category).build();
    }

    //Prueba para crear un nuevo producto
    @Test
    public void create_valid(){
        ProductRequestDTO dto = ProductRequestDTO.builder()
                .description("product 1").brand(brand).unit(unit)
                .subcategoryProduct(subcategory).category(category).build();

        productService.create(dto);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo, times(1)).save(productArgumentCaptor.capture());

        Product result = productArgumentCaptor.getValue();
        assertEquals(result.getDescription(), dto.getDescription());
        assertEquals(result.getBrand(), dto.getBrand());
        assertEquals(result.getUnit(), dto.getUnit());
        assertEquals(result.getSubcategoryProduct(), dto.getSubcategoryProduct());
        assertFalse(result.isStatus());
        assertEquals(StatusProduct.NODISPONIBLE, result.getStatusProduct());
        assertEquals(0, result.getBuyPrice());
        assertEquals(0, result.getSalePrice());
        assertEquals(0, result.getStock());
    }

    //Prueba para actualizar info de producto
    @Test
    public void updateProductInfo_normalNoStockChange(){
        ProductUpdatedRequestDTO dto = ProductUpdatedRequestDTO.builder().id(1L).description("Sony TV 50p").comment("")
                .salePrice(1000).stock(2).build();
        Product product = Product.builder().id(1L).description("Sony Tv").salePrice(900).stock(2).build();
        when(productRepo.findById(dto.getId())).thenReturn(Optional.of(product));
        doNothing().when(validator).isDeleted(product);
        when(validator.isChangeStockOriginalValue(product.getStock(), dto.getStock())).thenReturn(false);

        productService.updateProductInfo(dto);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo, times(1)).save(productArgumentCaptor.capture());
        verify(productRepo, times(1)).findById(dto.getId());

        Product result = productArgumentCaptor.getValue();
        assertEquals(result.getDescription(), dto.getDescription());
        assertEquals(result.getStock(), dto.getStock());
        assertEquals(result.getSalePrice(), dto.getSalePrice());
    }
    @Test
    public void updateProductInfo_StockChange(){
        ProductUpdatedRequestDTO dto = ProductUpdatedRequestDTO.builder().id(1L).description("Sony TV 50p")
                .comment("Comment change stock").salePrice(1000).stock(5).build();
        Product product = Product.builder().id(1L).description("Sony Tv").salePrice(900).stock(2).build();
        int originalStock = product.getStock();
        when(productRepo.findById(dto.getId())).thenReturn(Optional.of(product));
        doNothing().when(validator).isDeleted(product);
        when(validator.isChangeStockOriginalValue(product.getStock(), dto.getStock())).thenReturn(true);

        productService.updateProductInfo(dto);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo, times(1)).save(productArgumentCaptor.capture());
        verify(productRepo, times(1)).findById(dto.getId());

        Product result = productArgumentCaptor.getValue();
        assertEquals(result.getDescription(), dto.getDescription());
        assertEquals(result.getStock(), dto.getStock());
        assertEquals(result.getSalePrice(), dto.getSalePrice());

        String expectedComment = String.format("%s - stock original: %s, stock nuevo: %s", dto.getComment(),
                originalStock, dto.getStock());
        int expectedQuantity = Math.abs(originalStock - dto.getStock());
        verify(kardexService, times(1)).addKardex(product, expectedQuantity, expectedComment,
                MovementsType.AJUSTE);
        verify(alertService, times(1)).createAlert(product, expectedComment);
    }

    //Prueba para actualizar un producto
    @Test
    public void update_StockStatus_validMoreStock(){
        product.setStock(10);
        productService.updateStockStatus(product);

        verify(productRepo).save(product);
        assertTrue(product.isStatus());
        assertEquals(StatusProduct.DISPONIBLE, product.getStatusProduct());
        assertEquals(10, product.getStock());
    }
    @Test
    public void update_StockStatus_validNonStock(){
        product.setStock(0);
        productService.updateStockStatus(product);

        verify(productRepo).save(product);
        assertFalse(product.isStatus());
        assertEquals(StatusProduct.NODISPONIBLE, product.getStatusProduct());
        assertEquals(0, product.getStock());
    }

    //Prueba para activar un producto
    @Test
    public void active_valid(){
        ProductUpdatedRequestDTO dto = ProductUpdatedRequestDTO.builder().id(1L).build();
        Product product = Product.builder().id(1L).status(false).build();
        when(productRepo.findById(dto.getId())).thenReturn(Optional.of(product));
        doNothing().when(validator).isActivated(product);

        productService.active(dto);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo, times(1)).findById(dto.getId());
        verify(productRepo, times(1)).save(productArgumentCaptor.capture());

        Product result = productArgumentCaptor.getValue();
        assertEquals(StatusProduct.NODISPONIBLE, result.getStatusProduct());
    }
    @Test
    public void active_ThrowException(){
        ProductUpdatedRequestDTO dto = ProductUpdatedRequestDTO.builder().id(1L).build();
        Product product = Product.builder().id(1L).status(true).build();
        when(productRepo.findById(dto.getId())).thenReturn(Optional.of(product));
        doThrow(new ProductAlreadyActivatedException("El producto ya está activado."))
                .when(validator).isActivated(product);

        assertThrows(ProductAlreadyActivatedException.class, () -> productService.active(dto));

        verify(productRepo, times(1)).findById(dto.getId());
        verify(productRepo, never()).save(product);
    }

    //Prueba para actualizar el stock después de una compra un producto
    @Test
    public void updateStockStatusStockPurchase_valid(){
        productService.updateStockPurchase(product, 20000, 10);
        assertEquals(20000, product.getBuyPrice());
        assertEquals(10, product.getStock());
        verify(productRepo).save(product);
    }

    //Prueba para actualizar el stock después de una venta un producto
    @Test
    public void updateStockStatusStockSale_valid(){
        product.setStock(30);
        productService.updateStockSale(product, 10);
        assertEquals(20, product.getStock());
        verify(productRepo).save(product);
    }

    //Prueba para eliminar un producto
    @Test
    public void delete_valid(){
        ProductUpdatedRequestDTO dto = ProductUpdatedRequestDTO.builder().id(1L).build();
        Product product = Product.builder().id(1L).status(true).build();
        when(productRepo.findById(dto.getId())).thenReturn(Optional.of(product));
        doNothing().when(validator).isDeleted(product);

        productService.delete(dto);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo, times(1)).findById(dto.getId());
        verify(productRepo, times(1)).save(productArgumentCaptor.capture());

        Product result = productArgumentCaptor.getValue();
        assertFalse(result.isStatus());
        assertEquals(StatusProduct.DESCONTINUADO, result.getStatusProduct());
    }
    @Test
    public void delete_ThrowException(){
        ProductUpdatedRequestDTO dto = ProductUpdatedRequestDTO.builder().id(1L).build();
        Product product = Product.builder().id(1L).status(false).build();
        when(productRepo.findById(dto.getId())).thenReturn(Optional.of(product));
        doThrow(new ProductAlreadyDeletedException("El producto ya está desactivado."))
                .when(validator).isDeleted(product);

        assertThrows(ProductAlreadyDeletedException.class, () -> productService.delete(dto));

        verify(productRepo, times(1)).findById(dto.getId());
        verify(productRepo, never()).save(product);
    }

    //Prueba para obtener todos los productos
    @Test
    public void getAll_valid(){
        productService.getAll();
        verify(productRepo).findAll();
    }

    //Prueba para obtener producto por ID
    @Test
    public void getProductById_valid(){
        product.setId(1L);
        productService.getProductById(product.getId());
        verify(productRepo).findById(product.getId());
    }

    //Prueba para verificar las alertas creadas o cuáles se deben crear para cada situación
    @Test
    public void checkAndCreateAlertsByProduct_validNoStock(){
        product.setStock(0);
        product.setThreshold(3);
        product.setDescription("product 1");
        List<Product> products = Collections.singletonList(product);
        String noStockDescription = "Sin Stock (" + product.getDescription() + " )";
        when(productRepo.findAll()).thenReturn(products);
        when(alertService.isAlertActive(product, noStockDescription)).thenReturn(false);
        productService.checkAndCreateAlertsByProduct();

        verify(productRepo, times(1)).findAll();
        verify(alertService, times(1)).isAlertActive(product, noStockDescription);
        verify(alertService, times(1)).createAlert(product, noStockDescription);
        verify(kardexService, times(1)).checkLowSales(product);
    }
    @Test
    public void checkAndCreateAlertsByProduct_validCriticalStock(){
        product.setStock(10);
        product.setThreshold(15);
        product.setDescription("product 1");
        List<Product> products = Collections.singletonList(product);
        String criticalStockDescription = "Stock Crítico (" + product.getDescription() + " - " + product.getStock() + " unidades)";
        when(productRepo.findAll()).thenReturn(products);
        when(alertService.isAlertActive(product, criticalStockDescription)).thenReturn(false);
        productService.checkAndCreateAlertsByProduct();

        verify(productRepo, times(1)).findAll();
        verify(alertService, times(1)).isAlertActive(product, criticalStockDescription);
        verify(alertService, times(1)).createAlert(product, criticalStockDescription);
        verify(kardexService, times(1)).checkLowSales(product);
    }
    @Test
    public void checkAndCreateAlertsByProduct_validWithAlertActive(){
        product.setStock(0);
        product.setThreshold(15);
        product.setDescription("product 1");
        List<Product> products = Collections.singletonList(product);
        String noStockDescription = "Sin Stock (" + product.getDescription() + " )";
        when(productRepo.findAll()).thenReturn(products);
        when(alertService.isAlertActive(product, noStockDescription)).thenReturn(true);
        productService.checkAndCreateAlertsByProduct();

        verify(productRepo, times(1)).findAll();
        verify(alertService, times(1)).isAlertActive(product, noStockDescription);
        verify(alertService, never()).createAlert(product, noStockDescription);
        verify(kardexService, times(1)).checkLowSales(product);
    }

    //Prueba para actualizar el stock y reserva de un producto después de un despacho
    @Test
    public void updateStockStatusStockAndReserveDispatch_validSuccess(){
        product.setStock(30);
        product.setReserve(0);
        productService.updateStockAndReserveDispatch(product, 10, true);
        assertEquals(20, product.getStock());
        assertEquals(10, product.getReserve());
        verify(productRepo).save(product);
    }
    @Test
    public void updateStockStatusStockAndReserveDispatch_validNoSuccess(){
        product.setStock(30);
        product.setReserve(10);
        productService.updateStockAndReserveDispatch(product, 10, false);
        assertEquals(40, product.getStock());
        assertEquals(0, product.getReserve());
        verify(productRepo).save(product);
    }
}
