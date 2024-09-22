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
public class UserDetailServiceImpl implements UserDetailsService, UserDetailServicePersonalizado {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IMenuRepository menuRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper modelMapper;

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

        AuthResponse authResponse = new AuthResponse(userCreated.getUsercodigo(), userCreated.getUsername(), "User created successfuly", accessToken, true);
        return authResponse;
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
    public List<MenuDTO> obtenerMenus(String userId) {

        UserEntity userEntity = userRepository.findUserEntityByUsername(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (userEntity.getMenus().isEmpty()) {
            throw new NotFoundException("El usuario no tiene menus");
        }
        List<MenuDTO> allMenus = userEntity.getMenus().stream()
                .filter(menu -> menu.getNivel() == 1)
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return allMenus;
    }

    @Override
    public List<MenuDTO> obtenerMenus(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        if (user.getMenus().isEmpty()) {
            throw new NotFoundException("El usuario no tiene menus");
        }
        List<MenuDTO> allMenus = user.getMenus().stream()
                .filter(menu -> menu.getNivel() == 1)
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return allMenus;
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
        menuDTO.setNivel(menuEntity.getNivel());
        menuDTO.setPadre(menuEntity.getPadre() != null ? menuEntity.getPadre().getId() : 0);

        // Convertir submenús recursivamente
        Set<MenuDTO> submenusDTO = menuEntity.getSubmenus().stream()
                .map(this::convertToDTO) // Recursivamente convertir submenús
                .collect(Collectors.toSet());

        menuDTO.setSubmenus(submenusDTO);

        return menuDTO;
    }
}