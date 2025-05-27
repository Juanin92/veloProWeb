package com.veloProWeb.controller.User;

import com.veloProWeb.model.dto.user.AuthRequestDTO;
import com.veloProWeb.model.dto.user.LoginRequestDTO;
import com.veloProWeb.security.Service.EncryptionService;
import com.veloProWeb.security.Jwt.JwtUtil;
import com.veloProWeb.service.Record.IRecordService;
import com.veloProWeb.service.User.Interface.ILoginService;
import com.veloProWeb.util.ResponseMessage;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final IRecordService recordService;
    private final ILoginService loginService;
    private final EncryptionService encryptionService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDTO user) throws Exception {
        String decryptedPassword = encryptionService.decrypt(user.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), decryptedPassword)
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtUtil.generateToken(userDetails);
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
        recordService.registerEntry(userDetails);
        return new ResponseEntity<>(ResponseMessage.multiMessage("token", jwtToken, "role", role),
                HttpStatus.OK);
    }

    @PostMapping("/login/code")
    public ResponseEntity<Map<String, String>> loginWithCode(@RequestBody @Valid LoginRequestDTO user) throws Exception
    {
        String decryptedPassword = encryptionService.decrypt(user.getPassword());
        loginService.validateSecurityToken(user.getUsername(), decryptedPassword);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtUtil.generateToken(userDetails);
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
        recordService.registerEntry(userDetails);
        return new ResponseEntity<>(ResponseMessage.multiMessage("token", jwtToken, "role", role),
                HttpStatus.OK);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER' , 'SELLER', 'GUEST', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal UserDetails userDetails){
        recordService.registerEnd(userDetails);
        return new ResponseEntity<>(ResponseMessage.message("Usuario Desconectado Correctamente"), HttpStatus.OK);
    }

    @PostMapping("/verificar")
    @PreAuthorize("hasAnyAuthority('MASTER')")
    public ResponseEntity<Boolean> getAuthAccess(@RequestBody AuthRequestDTO dto,
                                                 @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        String decryptedPassword = encryptionService.decrypt(dto.getToken());
        return ResponseEntity.ok(loginService.isPasswordValid(decryptedPassword, userDetails));
    }

    @PostMapping("/olvide-codigo")
    public ResponseEntity<Map<String, Object>> sendEmailCode(@RequestBody @Valid LoginRequestDTO loginRequestDTO){
        try{
            loginService.sendSecurityTokenByEmail(loginRequestDTO.getUsername());
            recordService.registerActionManual(loginRequestDTO.getUsername(), "CHANGE",
                    String.format("Envio de código de seguridad, realizado por el usuario %s", loginRequestDTO.getUsername()));
            return new ResponseEntity<>(ResponseMessage.messageWithBooleanKey("message",
                    "Código de seguridad enviado", "action", true), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(ResponseMessage.messageWithBooleanKey("message", e.getMessage(),
                    "action", false), HttpStatus.CONFLICT);
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
