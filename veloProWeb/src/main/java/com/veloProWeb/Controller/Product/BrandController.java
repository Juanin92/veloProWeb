package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.BrandProduct;
import com.veloProWeb.Service.Product.Interfaces.IBrandService;
import com.veloProWeb.Service.Record.IRecordService;
import com.veloProWeb.Service.User.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar operaciones relacionadas con las marcas de productos.
 * Este controlador proporciona endpoints para obtener todas las marcas y crear nuevas marcas.
 */
@RestController
@RequestMapping("/marcas")
@CrossOrigin(origins = "http://localhost:4200")
public class BrandController {

    @Autowired private IBrandService brandService;
    @Autowired private IUserService userService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de todas las marcas
     * @return - ResponseEntity con una lista de las marcas
     */
    @GetMapping
    public ResponseEntity<List<BrandProduct>> getAllBrands(@AuthenticationPrincipal UserDetails userDetails){
        if (userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
            return ResponseEntity.ok(brandService.getAll());
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Crea una nueva marca
     * @param brand - Marca con los datos a crear
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createBrand(@RequestBody BrandProduct brand,
                                                           @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            if(userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
                brandService.save(brand);
                response.put("message", "Marca registrada correctamente");
                recordService.registerAction(userDetails, "CREATE", "Marca creada: " + brand.getName());
                return ResponseEntity.ok(response);
            }else {
                recordService.registerAction(userDetails, "CREATE_FAILURE",
                        "Error: " + userDetails.getUsername() + " ingreso indebido");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }catch (IllegalArgumentException e){
            response.put("message",e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    "ERROR: crear marca(" + brand.getName() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
