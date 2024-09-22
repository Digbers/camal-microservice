package com.camal.microservice_auth.controller;

import com.camal.microservice_auth.controller.dto.*;
import com.camal.microservice_auth.exception.InvalidCredentialsException;
import com.camal.microservice_auth.service.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;
    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login (@RequestBody @Valid AuthLoginRequest userRequest) {
        //return new ResponseEntity<>(this.userDetailServiceImpl.loginUser(userRequest), HttpStatus.OK);
        try {
            AuthResponse response = this.userDetailServiceImpl.loginUser(userRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidCredentialsException e) {
            return new ResponseEntity<>( AuthResponse.builder()
                    .status(false)
                    .message("Invalid credentials")
                    .jwt(null)
                    .build(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(AuthResponse.builder().message(e.getMessage()).status(false).jwt(null).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/log-in-complete")
    public ResponseEntity<AuthResponseComplete> loginComplete (@RequestBody @Valid AuthLoginComplete userRequest) {
        try{
            AuthResponseComplete response = this.userDetailServiceImpl.loginUserComplete(userRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidCredentialsException e) {
            return new ResponseEntity<>( AuthResponseComplete.builder()
                    .status(false)
                    .usercodigo(null)
                    .idEmpresa(null)
                    .idAlmacen(null)
                    .idPuntoVenta(null)
                    .message("Invalid credentials")
                    .jwt(null)
                    .build(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(AuthResponseComplete.builder().message(e.getMessage()).status(false).jwt(null).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthCreateUserRequest authCreateUser){
        return new ResponseEntity<>(this.userDetailServiceImpl.createUser(authCreateUser), HttpStatus.OK);

    }
    @PatchMapping("/users/update/{id}")
    public ResponseEntity<AuthResponse> update(@PathVariable Long id,@RequestBody @Valid AuthUserUpdate authUpdateUser){
        return new ResponseEntity<>(this.userDetailServiceImpl.updateUser(id,authUpdateUser), HttpStatus.OK);
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<AuthResponse> delete(@PathVariable Long id){
        return new ResponseEntity<>(this.userDetailServiceImpl.deleteUser(id),HttpStatus.OK);
    }
    @GetMapping("/list")
    public Page<UserDTO> getUsers(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Boolean isEnabled) {
        return this.userDetailServiceImpl.findAll(id, username, isEnabled, PageRequest.of(page, size));
    }

    @GetMapping("/users/list-users")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(this.userDetailServiceImpl.findAll());
    }

    @PostMapping("/agregar-menus/{id}")
    public ResponseEntity<?> agregarMenus(@PathVariable Long id, @RequestBody List<Long> menus) {
        userDetailServiceImpl.agregarMenus(id, menus);
        return ResponseEntity.ok("Menús agregados correctamente");
    }


    @DeleteMapping("/eliminar-menus/{id}")
    public ResponseEntity<?> eliminarMenus(@PathVariable Long id, @RequestBody List<Long> menus) {
        userDetailServiceImpl.eliminarMenus(id, menus);
        return ResponseEntity.ok("Menús eliminados correctamente");
    }


    @GetMapping("/menus/{userName}")
    public ResponseEntity<List<MenuDTO>> obtenerMenusDeUsuario(@PathVariable String userName) {
        try{
            List<MenuDTO> menus = userDetailServiceImpl.obtenerMenus(userName);
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/menus/id/{userId}")
    public ResponseEntity<List<MenuDTO>> obtenerMenusDeUsuario(@PathVariable Long userId) {
        try {
            List<MenuDTO> menus = userDetailServiceImpl.obtenerMenus(userId);
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

    }

}
