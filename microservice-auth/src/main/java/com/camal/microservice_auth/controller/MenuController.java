package com.camal.microservice_auth.controller;

import com.camal.microservice_auth.controller.dto.MenuDTO;
import com.camal.microservice_auth.persistence.entity.MenusEntity;
import com.camal.microservice_auth.service.userDetail.IMenuService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private IMenuService menuService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/list")
    public Page<MenuDTO> findAll(Pageable pageable) {
        return menuService.findAll(pageable);
    }
    @GetMapping("/find/{id}")
    public ResponseEntity<MenuDTO> findById(@PathVariable Long id) {
        Optional<MenusEntity> menu= menuService.findById(id);
        return menu.map(entity -> {
            MenuDTO menuDTO = modelMapper.map(entity, MenuDTO.class);
            return ResponseEntity.ok(menuDTO);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/save")
    public MenuDTO save(@RequestBody MenuDTO menuDTO) {
        return menuService.save(menuDTO);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<MenuDTO> delete(@PathVariable Long id) {
        Optional<MenusEntity> menu = menuService.findById(id);
        if (menu.isPresent()) {
            menuService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
