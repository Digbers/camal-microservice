package com.camal.microservice_auth.service;

import com.camal.microservice_auth.controller.dto.*;
import com.camal.microservice_auth.exception.InvalidCredentialsException;
import com.camal.microservice_auth.persistence.entity.MenusEntity;
import com.camal.microservice_auth.persistence.entity.RolesEntity;
import com.camal.microservice_auth.persistence.entity.UserEntity;
import com.camal.microservice_auth.persistence.especification.UserSpecification;
import com.camal.microservice_auth.persistence.repository.IMenuRepository;
import com.camal.microservice_auth.persistence.repository.IUserRepository;
import com.camal.microservice_auth.persistence.repository.RoleRepository;
import com.camal.microservice_auth.service.userDetail.UserDetailServicePersonalizado;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.camal.microservice_auth.util.JwtUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService, UserDetailServicePersonalizado {
    private final JwtUtil jwtUtil;
    private final IUserRepository userRepository;
    private final IMenuRepository menuRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final IMenuRepository menusRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserEntityByUsername(username).orElseThrow(() ->new UsernameNotFoundException("User not found"));
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userEntity.getRoles()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionsList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getIsEnabled(),
                userEntity.getAccountNoExpired(),
                userEntity.getCredentialsNoExpired(),
                userEntity.getAccountNoLocked(),
                authorityList);
    }

    public AuthResponse loginUser (AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<String> usercodigo = userRepository.findUserCodigo(username);
        String usercodigo1 = usercodigo.get();
        String accessToken = jwtUtil.generateJwtToken(authentication, usercodigo1);
        //System.out.println(usercodigo1);
        AuthResponse authResponse = new AuthResponse(usercodigo1, username, "User Loged successfuly", accessToken, true);
        return authResponse;
    }
    public AuthResponseComplete loginUserComplete (AuthLoginComplete authLoginComplete) {
        String usercodigo = authLoginComplete.usercodigo();
        String username = authLoginComplete.username();
        Integer sesionEmpId = authLoginComplete.idEmpresa();
        Integer sesionAlmId = authLoginComplete.idAlmacen();
        Integer sesionPtoVtaId = authLoginComplete.idPuntoVenta();
        //Authentication authentication = this.authenticate(username, password);
        //SecurityContextHolder.getContext().setAuthentication(authentication);
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = jwtUtil.generateJwtTokenComplete(authentication, authLoginComplete);
        AuthResponseComplete authResponse = new AuthResponseComplete(usercodigo, username, sesionEmpId, sesionAlmId, sesionPtoVtaId, "User Loged successfuly", accessToken, true);
        return authResponse;
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);
        if(userDetails == null){
            throw new InvalidCredentialsException("Invalid credentials provided(user)");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new InvalidCredentialsException("Invalid credentials provided");
        }
        return  new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }
    public UserDTO saveUser (AuthCreateUserRequest authCreateUserRequest) {
        try {
            String usercodigo = authCreateUserRequest.usercodigo();
            String username = authCreateUserRequest.username();
            String password = authCreateUserRequest.password();
            List<String> roleRequest = authCreateUserRequest.rolesRequest().roleListName();

            Set<RolesEntity> roleEntitySet = roleRepository.findRoleEntityByRoleEnumIn(roleRequest).stream().collect(Collectors.toSet());

            if(roleEntitySet.isEmpty()){
                throw new IllegalArgumentException("Roles especificados sin existencias");
            }
            UserEntity userEntity = UserEntity.builder()
                    .usercodigo(usercodigo)
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .roles(roleEntitySet)
                    .isEnabled(true)
                    .accountNoLocked(true)
                    .accountNoExpired(true)
                    .credentialsNoExpired(true)
                    .build();

            UserEntity userCreated = userRepository.save(userEntity);
            return modelMapper.map(userCreated, UserDTO.class);
        } catch (Exception e) {
            log.error("Error al crear usuario", e);
            throw new NotFoundException("Error al crear usuario");
        }

    }
    public AuthResponse createUser (AuthCreateUserRequest authCreateUserRequest) {
        String usercodigo = authCreateUserRequest.usercodigo();
        String username = authCreateUserRequest.username();
        String password = authCreateUserRequest.password();
        List<String> roleRequest = authCreateUserRequest.rolesRequest().roleListName();

        Set<RolesEntity> roleEntitySet = roleRepository.findRoleEntityByRoleEnumIn(roleRequest).stream().collect(Collectors.toSet());

        if(roleEntitySet.isEmpty()){
            throw new IllegalArgumentException("Roles especificados sin existencias");
        }
        UserEntity userEntity = UserEntity.builder()
                .usercodigo(usercodigo)
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roleEntitySet)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialsNoExpired(true)
                .build();

        UserEntity userCreated = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userCreated.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userCreated.getRoles()
                .stream()
                .flatMap(role -> role.getPermissionsList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        //SecurityContext contex =  SecurityContextHolder.getContext();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(), authorityList);

        String accessToken =  jwtUtil.generateJwtToken(authentication, userCreated.getUsercodigo());

        return new AuthResponse(userCreated.getUsercodigo(), userCreated.getUsername(), "User created successfuly", accessToken, true);
    }
    public AuthResponse updateUser(Long userId, AuthUserUpdate authUpdateUserRequest) {
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        if (authUpdateUserRequest.usercodigo() != null && !authUpdateUserRequest.usercodigo().isEmpty()) {
            existingUser.setUsercodigo(authUpdateUserRequest.usercodigo());
        }
        if (authUpdateUserRequest.username() != null && !authUpdateUserRequest.username().isEmpty()) {
            existingUser.setUsername(authUpdateUserRequest.username());
        }
        if (authUpdateUserRequest.password() != null && !authUpdateUserRequest.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(authUpdateUserRequest.password()));
        }

        // Actualizar los roles si se proporciona una nueva lista de roles
        if (authUpdateUserRequest.rolesRequest() != null && !authUpdateUserRequest.rolesRequest().roleListName().isEmpty()) {
            List<String> roleRequest = authUpdateUserRequest.rolesRequest().roleListName();
            Set<RolesEntity> roleEntitySet = roleRepository.findRoleEntityByRoleEnumIn(roleRequest).stream().collect(Collectors.toSet());
            if (roleEntitySet.isEmpty()) {
                throw new IllegalArgumentException("Roles especificados sin existencias");
            }
            existingUser.setRoles(roleEntitySet);
        }
        UserEntity updatedUser = userRepository.save(existingUser);

        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        updatedUser.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        updatedUser.getRoles()
                .stream()
                .flatMap(role -> role.getPermissionsList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        // Crear un nuevo token JWT con los nuevos datos del usuario
        Authentication authentication = new UsernamePasswordAuthenticationToken(updatedUser.getUsername(), updatedUser.getPassword(), authorityList);

        String accessToken = jwtUtil.generateJwtToken(authentication, updatedUser.getUsercodigo());

        // Crear y devolver la respuesta de autenticación
        AuthResponse authResponse = new AuthResponse(updatedUser.getUsercodigo(), updatedUser.getUsername(), "User updated successfully", accessToken, updatedUser.getIsEnabled());
        return authResponse;
    }
    public AuthResponse deleteUser(Long userId) {

        UserEntity userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        userRepository.delete(userToDelete);

        AuthResponse authResponse = new AuthResponse(userToDelete.getUsercodigo(), userToDelete.getUsername(), "User deleted successfully", null, true);
        return authResponse;
    }



    @Override
    public Page<UserDTO> findAll(Long id, String username, Boolean isEnabled, Pageable pageable) {
        Specification<UserEntity> spec = UserSpecification.getUsers(id, username, isEnabled);
        return userRepository.findAll(spec, pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void agregarMenus(Long usuarioId, List<Long> menusIds) {
        UserEntity usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        for (Long menuId : menusIds) {
            menuRepository.findById(menuId).ifPresent(menu -> {
                if (!usuario.getMenus().contains(menu)) {
                    usuario.getMenus().add(menu);
                }
            });
        }
        userRepository.save(usuario);
    }

    @Override
    public void eliminarMenus(Long userId, List<Long> menusId) {
        UserEntity usuario = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        for (Long menuId : menusId) {
            menuRepository.findById(menuId).ifPresent(menu -> {
                if (usuario.getMenus().contains(menu)) {
                    usuario.getMenus().remove(menu);
                }
            });
        }
        userRepository.save(usuario);
    }

    @Override
    public List<MenuDTO> obtenerMenus(String userCode) {
        try {
            UserEntity userEntity = userRepository.findByUsercodigo(userCode)
                    .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

            Set<MenusEntity> userMenus = userEntity.getMenus();

            if (userMenus.isEmpty()) {
                throw new NotFoundException("El usuario no tiene menús");
            }
            // Filtra para incluir solo los menús de nivel 1 que el usuario tiene disponibles y que contienen submenús disponibles
            List<MenuDTO> userMenusDTO = userMenus.stream()
                    .filter(menu -> menu.getNivel() == 1) // Filtra solo menús de nivel 1
                    .sorted(Comparator.comparing(MenusEntity::getOrden))
                    .map(menu -> {
                        // Convierte a DTO y verifica si el menú tiene submenús disponibles antes de agregarlo
                        MenuDTO menuDTO = convertToDTO2(menu, userMenus);
                        // Solo incluir el menú si tiene submenús disponibles o si está directamente asignado al usuario
                        return menuDTO.getSubmenus().isEmpty() && !userMenus.contains(menu) ? null : menuDTO;
                    })
                    .filter(Objects::nonNull) // Elimina los menús nulos (sin submenús disponibles)
                    .collect(Collectors.toList());

            return userMenusDTO;
        } catch (Exception e) {
            log.error("Error obteniendo menus del usuario con codigo: " + userCode, e);
            throw new NotFoundException("Usuario no encontrado");
        }
    }

    @Override
    public List<MenuDTO> obtenerMenus(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        if (userEntity.getMenus().isEmpty()) {
            throw new NotFoundException("El usuario no tiene menus");
        }
        List<MenusEntity> allMenusDisponibles = menusRepository.findAll();
        // Obtener los menús del usuario
        Set<MenusEntity> userMenus = userEntity.getMenus();
        // Convertir todos los menús de nivel 1 a DTO, marcando los disponibles
        List<MenuDTO> allMenus = allMenusDisponibles.stream()
                .filter(menu -> menu.getNivel() == 1)
                .sorted(Comparator.comparing(menu -> menu.getOrden()))
                .map(menu -> convertToDTO2(menu, userMenus))
                .collect(Collectors.toList());
        return allMenus;
    }
    @Override
    public List<MenuDTO> obtenerMenusConfiguracion(String usercode) {
        try {
            log.info("Obteniendo los menus de usuario con codigo: " + usercode);
            // Obtener el usuario
            UserEntity userEntity = userRepository.findByUsercodigo(usercode)
                    .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
            // Obtener todos los menús disponibles
            List<MenusEntity> allMenusDisponibles = menusRepository.findAll();
            // Obtener los menús del usuario
            Set<MenusEntity> userMenus = userEntity.getMenus();
            // Convertir todos los menús de nivel 1 a DTO, marcando los disponibles
            List<MenuDTO> allMenus = allMenusDisponibles.stream()
                    .filter(menu -> menu.getNivel() == 1)
                    .sorted(Comparator.comparing(menu -> menu.getOrden()))
                    .map(menu -> convertToDTO(menu, userMenus))
                    .collect(Collectors.toList());
            return allMenus;
        } catch (Exception e) {
            log.error("Error obteniendo menus del usuario con codigo: " + usercode, e);
            throw new NotFoundException("Error al obtener los menús");
        }
    }

    @Override
    public Boolean guardarMenus(String usercode, List<Long> menus) {
        try {
            log.info("Obteniendo los menus de usuario con codigo: " + usercode);
            // Obtener el usuario
            UserEntity userEntity = userRepository.findByUsercodigo(usercode)
                    .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
            // Obtener todos los menús disponibles
            List<MenusEntity> allMenusDisponibles = menusRepository.findAllById(menus);
            userEntity.setMenus(new HashSet<>(allMenusDisponibles));
            userRepository.save(userEntity);
            log.info("Menús guardados correctamente");
            return true;
        } catch (Exception e) {
            log.error("Error guardando menus del usuario con codigo: " + usercode, e);
            throw new NotFoundException("Error al obtener los menús");
        }
    }

    private MenuDTO convertToDTO(MenusEntity menuEntity) {
        if (menuEntity == null) {
            return null;
        }
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(menuEntity.getId());
        menuDTO.setMenuName(menuEntity.getMenuName());
        menuDTO.setMenuUrl(menuEntity.getMenuUrl());
        menuDTO.setIcon(menuEntity.getIcon());
        menuDTO.setOrden(menuEntity.getOrden());
        menuDTO.setNivel(menuEntity.getNivel());
        menuDTO.setPadre(menuEntity.getPadre() != null ? menuEntity.getPadre().getId() : 0);
        // Convertir submenús recursivamente
        Set<MenuDTO> submenusDTO = menuEntity.getSubmenus().stream()
                .map(this::convertToDTO) // Recursivamente convertir submenús
                .collect(Collectors.toSet());

        menuDTO.setSubmenus(submenusDTO);

        return menuDTO;
    }
    // trae solo los menus del usuario
    private MenuDTO convertToDTO2(MenusEntity menuEntity, Set<MenusEntity> userMenus) {
        if (menuEntity == null) {
            return null;
        }

        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(menuEntity.getId());
        menuDTO.setMenuName(menuEntity.getMenuName());
        menuDTO.setMenuUrl(menuEntity.getMenuUrl());
        menuDTO.setIcon(menuEntity.getIcon());
        menuDTO.setOrden(menuEntity.getOrden());
        menuDTO.setNivel(menuEntity.getNivel());
        menuDTO.setPadre(menuEntity.getPadre() != null ? menuEntity.getPadre().getId() : 0);
        menuDTO.setMenuDisponible(userMenus.contains(menuEntity)); // Marca el menú como disponible si está en userMenus

        // Filtra submenús para incluir solo aquellos asignados al usuario
        Set<MenuDTO> submenusDTO = menuEntity.getSubmenus().stream()
                .filter(userMenus::contains) // Solo submenús que el usuario tiene asignados
                .map(submenu -> convertToDTO(submenu, userMenus))
                .collect(Collectors.toSet());

        menuDTO.setSubmenus(submenusDTO);

        return menuDTO;
    }
    // Método modificado para convertir MenuEntity a DTO
    private MenuDTO convertToDTO(MenusEntity menuEntity, Set<MenusEntity> userMenus) {
        if (menuEntity == null) {
            return null;
        }

        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(menuEntity.getId());
        menuDTO.setMenuName(menuEntity.getMenuName());
        menuDTO.setMenuUrl(menuEntity.getMenuUrl());
        menuDTO.setIcon(menuEntity.getIcon());
        menuDTO.setOrden(menuEntity.getOrden());
        menuDTO.setNivel(menuEntity.getNivel());
        menuDTO.setPadre(menuEntity.getPadre() != null ? menuEntity.getPadre().getId() : 0);
        menuDTO.setMenuDisponible(userMenus.contains(menuEntity));

        // Convertir submenús recursivamente
        Set<MenuDTO> submenusDTO = menuEntity.getSubmenus().stream()
                .map(submenu -> convertToDTO(submenu, userMenus))
                .collect(Collectors.toSet());

        menuDTO.setSubmenus(submenusDTO);

        return menuDTO;
    }
}
