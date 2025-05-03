package com.veloProWeb.Controller.User;

import com.veloProWeb.Model.DTO.AuthRequestDTO;
import com.veloProWeb.Model.DTO.LoginRequest;
import com.veloProWeb.Security.Service.EncryptionService;
import com.veloProWeb.Security.Jwt.JwtUtil;
import com.veloProWeb.Service.Record.IRecordService;
import com.veloProWeb.Service.User.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired private IRecordService recordService;
    @Autowired private IUserService userService;
    @Autowired private EncryptionService encryptionService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest user) {
        Map<String, String> response = new HashMap<>();
        try{
            String decryptedPassword = encryptionService.decrypt(user.getPassword());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), decryptedPassword)
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String jwtToken = jwtUtil.generateToken(userDetails);
            response.put("token", jwtToken);
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);
            response.put("role", role);
            recordService.registerEntry(userDetails);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("error", "Error de autenticación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    @PostMapping("/login/code")
    public ResponseEntity<Map<String, String>> loginWithCode(@RequestBody LoginRequest user) {
        Map<String, String> response = new HashMap<>();
        try{
            String decryptedPassword = encryptionService.decrypt(user.getPassword());
            userService.getAuthUserToken(user.getUsername(), decryptedPassword);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String jwtToken = jwtUtil.generateToken(userDetails);
            response.put("token", jwtToken);
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);
            response.put("role", role);
            recordService.registerEntry(userDetails);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("error", "Error de autenticación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER' , 'SELLER', 'GUEST', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response =  new HashMap<>();
        try{
            response.put("message", "Usuario Desconectado Correctamente");
            recordService.registerEnd(userDetails);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/verificar")
    @PreAuthorize("hasAnyAuthority('MASTER')")
    public ResponseEntity<Boolean> getAuthAccess(@RequestBody AuthRequestDTO dto,
                                                 @AuthenticationPrincipal UserDetails userDetails){
        try{
            String decryptedPassword = encryptionService.decrypt(dto.getToken());
            if (userService.getAuthUser(decryptedPassword, userDetails)){
                return ResponseEntity.ok(true);
            }else{
                return ResponseEntity.ok(false);
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/olvide-codigo")
    public ResponseEntity<Map<String, Object>> sendEmailCode(@RequestBody LoginRequest loginRequest){
        Map<String, Object> response = new HashMap<>();
        try{
            if (loginRequest != null && loginRequest.getUsername() != null) {
                userService.sendEmailCode(loginRequest.getUsername());
                response.put("message", "Código de seguridad enviado");
                response.put("action", true);
                recordService.registerActionManual(loginRequest.getUsername(), "CHANGE",
                        String.format("Envio de código de seguridad, realizado por el usuario %s", loginRequest.getUsername()));
                return ResponseEntity.ok(response);
            }else {
                response.put("message", "Debe agregar un nombre de usuario para enviar su código al correo");
                response.put("action", false);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }catch (Exception e){
            response.put("message", e.getMessage());
            response.put("action", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/encriptado")
    public ResponseEntity<String> getEncryptionKey() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(encryptionService.getEncryptionKey());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetails> getAuthenticatedUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userDetails);
    }
}
