package com.veloProWeb.Controller.User;

import com.veloProWeb.Model.DTO.LoginRequest;
import com.veloProWeb.Security.EncryptionService;
import com.veloProWeb.Security.JwUtil;
import com.veloProWeb.Service.Record.IRecordService;
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
    @Autowired private EncryptionService encryptionService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private JwUtil jwtUtil;

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
