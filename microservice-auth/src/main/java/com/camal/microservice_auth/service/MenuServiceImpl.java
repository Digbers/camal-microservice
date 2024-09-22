package com.camal.microservice_auth.service;


import com.camal.microservice_auth.controller.dto.MenuDTO;
import com.camal.microservice_auth.persistence.entity.MenusEntity;
import com.camal.microservice_auth.persistence.repository.IMenuRepository;
import com.camal.microservice_auth.service.userDetail.IMenuService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
@Service
public class MenuServiceImpl implements IMenuService {
    @Autowired
    private IMenuRepository menuRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Page<MenuDTO> findAll(Pageable pageable) {
        return menuRepository.findAll(pageable).map(menu -> modelMapper.map(menu, MenuDTO.class));
    }

    @Override
    public Optional<MenusEntity> findById(Long id) {
        //nuevo
        return menuRepository.findById(id).map(menu -> modelMapper.map(menu, MenusEntity.class));
    }

    @Override
    public MenuDTO save(MenuDTO menuDTO) {
        MenusEntity menuEntity = modelMapper.map(menuDTO, MenusEntity.class);

        MenusEntity savedMenuEntity = menuRepository.save(menuEntity);

        return modelMapper.map(savedMenuEntity, MenuDTO.class);
    }

    @Override
    public boolean deleteById(Long id) {
        if (menuRepository.findById(id).isPresent()) {
            menuRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
