package com.veloProWeb.service.product;

import com.veloProWeb.exceptions.product.ProductAlreadyActivatedException;
import com.veloProWeb.exceptions.product.ProductAlreadyDeletedException;
import com.veloProWeb.mapper.ProductMapper;
import com.veloProWeb.model.dto.product.ProductRequestDTO;
import com.veloProWeb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloProWeb.model.entity.product.*;
import com.veloProWeb.model.Enum.StatusProduct;
import com.veloProWeb.repository.product.ProductRepo;
import com.veloProWeb.validation.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks private ProductService productService;
    @Mock private ProductRepo productRepo;
    @Mock private ProductValidator validator;
    @Mock private ProductEventService productEventService;
    @Mock private ProductMapper mapper;
    @Mock private UserDetails userDetails;
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
        ProductRequestDTO dto = ProductRequestDTO.builder().description("product 1").brand(brand).unit(unit)
                .subcategoryProduct(subcategory).category(category).build();
        Product productMapped = Product.builder().description("product 1").brand(brand).unit(unit)
                .subcategoryProduct(subcategory).category(category).threshold(0).stock(0).status(false)
                .statusProduct(StatusProduct.NODISPONIBLE).buyPrice(0).salePrice(0).build();
        when(mapper.toEntity(dto)).thenReturn(productMapped);

        productService.create(dto, userDetails);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(mapper, times(1)).toEntity(dto);
        verify(productRepo, times(1)).save(productArgumentCaptor.capture());
        verify(productEventService, times(1))
                .handleCreateRegister(any(Product.class), eq("Creación Producto"), any(UserDetails.class));

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

        productService.updateProductInfo(dto, userDetails);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo, times(1)).save(productArgumentCaptor.capture());
        verify(productRepo, times(1)).findById(dto.getId());
        verify(productEventService, times(1))
                .isChangeStockOriginalValue(any(Product.class), eq(product.getStock()), eq(dto),
                        any(UserDetails.class));

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

        productService.updateProductInfo(dto, userDetails);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo, times(1)).save(productArgumentCaptor.capture());
        verify(productRepo, times(1)).findById(dto.getId());
        verify(productEventService, times(1))
                .isChangeStockOriginalValue(any(Product.class), eq(originalStock), eq(dto), any(UserDetails.class));

        Product result = productArgumentCaptor.getValue();
        assertEquals(result.getDescription(), dto.getDescription());
        assertEquals(result.getStock(), dto.getStock());
        assertEquals(result.getSalePrice(), dto.getSalePrice());
    }

    //Prueba para actualizar un producto
    @Test
    public void update_StockStatus_validMoreStock(){
        Product product = Product.builder().stock(10).build();
        productService.updateStockStatus(product);

        verify(productRepo).save(product);
        assertTrue(product.isStatus());
        assertEquals(StatusProduct.DISPONIBLE, product.getStatusProduct());
        assertEquals(10, product.getStock());
    }
    @Test
    public void update_StockStatus_validNonStock(){
        Product product = Product.builder().stock(0).build();
        productService.updateStockStatus(product);

        verify(productRepo).save(product);
        assertFalse(product.isStatus());
        assertEquals(StatusProduct.NODISPONIBLE, product.getStatusProduct());
        assertEquals(0, product.getStock());
    }

    //Prueba para activar un producto
    @Test
    public void reactive_valid(){
        ProductUpdatedRequestDTO dto = ProductUpdatedRequestDTO.builder().id(1L).build();
        Product product = Product.builder().id(1L).status(false).build();
        when(productRepo.findById(dto.getId())).thenReturn(Optional.of(product));
        doNothing().when(validator).isActivated(product);

        productService.reactive(dto, userDetails);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo, times(1)).findById(dto.getId());
        verify(productRepo, times(1)).save(productArgumentCaptor.capture());
        verify(productEventService, times(1))
                .handleCreateRegister(any(Product.class), eq("Activado"), any(UserDetails.class));

        Product result = productArgumentCaptor.getValue();
        assertEquals(StatusProduct.NODISPONIBLE, result.getStatusProduct());
    }
    @Test
    public void reactive_ThrowException(){
        ProductUpdatedRequestDTO dto = ProductUpdatedRequestDTO.builder().id(1L).build();
        Product product = Product.builder().id(1L).status(true).build();
        when(productRepo.findById(dto.getId())).thenReturn(Optional.of(product));
        doThrow(new ProductAlreadyActivatedException("El producto ya está activado."))
                .when(validator).isActivated(product);

        assertThrows(ProductAlreadyActivatedException.class,
                () -> productService.reactive(dto, userDetails));

        verify(productRepo, times(1)).findById(dto.getId());
        verify(productRepo, never()).save(product);
    }

    //Prueba para actualizar el stock después de una compra un producto
    @Test
    public void updateStockStatusStockPurchase_valid(){
        Product product = Product.builder().build();
        productService.updateStockPurchase(product, 20000, 10);
        assertEquals(20000, product.getBuyPrice());
        assertEquals(10, product.getStock());
        verify(productRepo).save(product);
    }

    //Prueba para actualizar el stock después de una venta un producto
    @Test
    public void updateStockStatusStockSale_valid(){
        Product product = Product.builder().stock(30).build();
        productService.updateStockSale(product, 10);
        assertEquals(20, product.getStock());
        verify(productRepo).save(product);
    }

    //Prueba para eliminar un producto
    @Test
    public void discontinueProduct_valid(){
        ProductUpdatedRequestDTO dto = ProductUpdatedRequestDTO.builder().id(1L).build();
        Product product = Product.builder().id(1L).status(true).build();
        when(productRepo.findById(dto.getId())).thenReturn(Optional.of(product));
        doNothing().when(validator).isDeleted(product);

        productService.discontinueProduct(dto, userDetails);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo, times(1)).findById(dto.getId());
        verify(productRepo, times(1)).save(productArgumentCaptor.capture());
        verify(productEventService, times(1))
                .handleCreateRegister(any(Product.class), eq("Eliminado / Descontinuado"),any(UserDetails.class));

        Product result = productArgumentCaptor.getValue();
        assertFalse(result.isStatus());
        assertEquals(StatusProduct.DESCONTINUADO, result.getStatusProduct());
    }
    @Test
    public void discontinueProduct_ThrowException(){
        ProductUpdatedRequestDTO dto = ProductUpdatedRequestDTO.builder().id(1L).build();
        Product product = Product.builder().id(1L).status(false).build();
        when(productRepo.findById(dto.getId())).thenReturn(Optional.of(product));
        doThrow(new ProductAlreadyDeletedException("El producto ya está desactivado."))
                .when(validator).isDeleted(product);

        assertThrows(ProductAlreadyDeletedException.class, () -> productService.discontinueProduct(dto, userDetails));

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
        Product product = Product.builder().id(1L).status(true).build();
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        productService.getProductById(product.getId());
        verify(productRepo).findById(product.getId());
    }

    //Prueba para actualizar el stock y reserva de un producto después de un despacho
    @Test
    public void updateStockStatusStockAndReserveDispatch_validSuccess(){
        Product product = Product.builder().stock(30).reserve(0).build();
        productService.updateStockAndReserveDispatch(product, 10, true);
        assertEquals(20, product.getStock());
        assertEquals(10, product.getReserve());
        verify(productRepo).save(product);
    }
    @Test
    public void updateStockStatusStockAndReserveDispatch_validNoSuccess(){
        Product product = Product.builder().stock(30).reserve(10).build();
        productService.updateStockAndReserveDispatch(product, 10, false);
        assertEquals(40, product.getStock());
        assertEquals(0, product.getReserve());
        verify(productRepo).save(product);
    }
}
