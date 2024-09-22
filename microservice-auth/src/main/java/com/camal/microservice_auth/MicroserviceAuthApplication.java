package com.camal.microservice_auth;

import com.camal.microservice_auth.persistence.entity.*;
import com.camal.microservice_auth.persistence.repository.IMenuRepository;
import com.camal.microservice_auth.persistence.repository.IUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@EnableDiscoveryClient
@SpringBootApplication
public class MicroserviceAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceAuthApplication.class, args);
	}
	@Bean
	CommandLineRunner  init(IUserRepository userRepository, IMenuRepository menuRepository) {
		return args -> {
			PermissionEntity createPermission = PermissionEntity.builder()
					.name("CREATE")
					.build();
			PermissionEntity readPermission = PermissionEntity.builder()
					.name("READ")
					.build();
			PermissionEntity updatePermission = PermissionEntity.builder()
					.name("UPDATE")
					.build();
			PermissionEntity deletePermission = PermissionEntity.builder()
					.name("DELETE")
					.build();
			/*ROLES*/
			RolesEntity roleAdmin = RolesEntity.builder()
					.roleEnum(RoleEnum.ADMIN)
					.permissionsList(Set.of(createPermission, readPermission, updatePermission, deletePermission))
					.build();
			RolesEntity roleUser = RolesEntity.builder()
					.roleEnum(RoleEnum.USER)
					.permissionsList(Set.of(readPermission))
					.build();
			RolesEntity roleDeveloper = RolesEntity.builder()
					.roleEnum(RoleEnum.DEVELOPER)
					.permissionsList(Set.of(readPermission, createPermission, updatePermission, deletePermission))
					.build();
			RolesEntity roleTester = RolesEntity.builder()
					.roleEnum(RoleEnum.TESTER)
					.permissionsList(Set.of(readPermission))
					.build();
			RolesEntity roleInvited = RolesEntity.builder()
					.roleEnum(RoleEnum.INVITED)
					.permissionsList(Set.of(readPermission))
					.build();
			/*MENUS*/
			MenusEntity menusForAdmin = MenusEntity.builder()
					.menuUrl("/")
					.menuName("Mantenimiento")
					.icon("fa-tools")
					.nivel(1L)
					.build();

			MenusEntity menusUsuario = MenusEntity.builder()
					.menuUrl("mantenimiento/usuarios/")
					.menuName("Usuarios")
					.icon("fa-user")
					.nivel(2L)
					.padre(menusForAdmin)
					.build();

			MenusEntity menusEmpresas = MenusEntity.builder()
					.menuUrl("mantenimiento/empresas/")
					.menuName("Empresas")
					.icon("fa-building")
					.nivel(2L)
					.padre(menusForAdmin)
					.build();

// Asignar submenús al menú padre
			menusForAdmin.setSubmenus(Set.of(menusUsuario, menusEmpresas));

			MenusEntity menusVentas = MenusEntity.builder()
					.menuUrl("/")
					.menuName("Ventas")
					.icon("fa-shopping-cart")
					.nivel(1L)
					.build();

			MenusEntity nuevaVenta = MenusEntity.builder()
					.menuUrl("ventas/nuevo/")
					.menuName("Nueva Venta")
					.icon("fa-plus")
					.nivel(2L)
					.padre(menusVentas)
					.build();

			MenusEntity comprobantes = MenusEntity.builder()
					.menuUrl("ventas/comprobantes/")
					.menuName("Comprobantes")
					.icon("fa-file-invoice")
					.nivel(2L)
					.padre(menusVentas)
					.build();

// Asignar submenús al menú padre
			menusVentas.setSubmenus(Set.of(nuevaVenta, comprobantes));

			/*USUARIOS*/
			UserEntity userAdmin = UserEntity.builder()
					.usercodigo("ADMIN")
					.username("admin")
					.password("$2a$10$gFppMCAq/stgWofRtokoPexL9FS4zp/k3BHrTUpjiumA7vnVyHKyK")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialsNoExpired(true)
					.roles(Set.of(roleAdmin))
					.menus(Set.of(menusForAdmin, menusUsuario, menusEmpresas, menusVentas, nuevaVenta, comprobantes))
					.build();
			UserEntity userUser = UserEntity.builder()
					.usercodigo("USER")
					.username("user")
					.password("$2a$10$Go2SeDEWgr74kyxsBQGkxuX5TSaCIo2mxUPzFURWfVe87VX6xUNTm")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialsNoExpired(true)
					.roles(Set.of(roleUser))
					.menus(Set.of(menusUsuario, menusEmpresas, menusVentas, nuevaVenta, comprobantes))
					.build();
			UserEntity userDeveloper = UserEntity.builder()
					.usercodigo("DEV")
					.username("developer")
					.password("$2a$10$OLggbw0E7sRWPrR5TJ2OuO9gWuzWjztqNpbdyoJsDXLSFxC7x5/RO")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialsNoExpired(true)
					.roles(Set.of(roleDeveloper))
					.menus(Set.of(menusEmpresas, menusUsuario, menusForAdmin, menusVentas, nuevaVenta, comprobantes))
					.build();
			UserEntity userTester = UserEntity.builder()
					.usercodigo("TEST")
					.username("tester")
					.password("$2a$10$1/Bozp.fUGY8Su1MwknCX.I83hTO9cPWrjdG9Vv6ABD97gU99a5NC")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialsNoExpired(true)
					.roles(Set.of(roleTester))
					.menus(Set.of(menusUsuario, menusEmpresas))
					.build();
			UserEntity userInvited = UserEntity.builder()
					.usercodigo("INV")
					.username("invited")
					.password("$2a$10$RWGP/rI9iUlHB/9o6wwuROCeHGb26yvvlZWjRuyw96/9nXgyVrF26")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialsNoExpired(true)
					.roles(Set.of(roleInvited))
					.menus(Set.of(menusUsuario, menusEmpresas))
					.build();
			userRepository.saveAll(List.of(userAdmin, userUser, userDeveloper, userTester, userInvited));


		};
	}


}
