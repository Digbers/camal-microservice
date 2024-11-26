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
			PermissionEntity createPermission = PermissionEntity.builder().name("CREATE").build();
			PermissionEntity readPermission = PermissionEntity.builder().name("READ").build();
			PermissionEntity updatePermission = PermissionEntity.builder().name("UPDATE").build();
			PermissionEntity deletePermission = PermissionEntity.builder().name("DELETE").build();
			/*ROLES*/
			RolesEntity roleAdmin = RolesEntity.builder().roleEnum(RoleEnum.ADMIN).permissionsList(Set.of(createPermission, readPermission, updatePermission, deletePermission)).build();
			RolesEntity roleUser = RolesEntity.builder().roleEnum(RoleEnum.USER).permissionsList(Set.of(readPermission)).build();
			RolesEntity roleDeveloper = RolesEntity.builder().roleEnum(RoleEnum.DEVELOPER).permissionsList(Set.of(readPermission, createPermission, updatePermission, deletePermission)).build();
			RolesEntity roleTester = RolesEntity.builder().roleEnum(RoleEnum.TESTER).permissionsList(Set.of(readPermission)).build();
			RolesEntity roleInvited = RolesEntity.builder().roleEnum(RoleEnum.INVITED).permissionsList(Set.of(readPermission)).build();
			/*MENUS*/
			MenusEntity menusForAdmin = MenusEntity.builder()
					.menuUrl("/")
					.menuName("Mantenimiento")
					.icon("fa-tools")
					.orden(1)
					.nivel(1L)
					.build();

			MenusEntity menusEmpresas = MenusEntity.builder()
					.menuUrl("mantenimiento/empresas/")
					.menuName("Empresas")
					.icon("fa-building")
					.orden(2)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menusProductos = MenusEntity.builder()
					.menuUrl("mantenimiento/productos/")
					.menuName("Productos")
					.icon("fa-basket-shopping")
					.orden(3)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menuEnvaces = MenusEntity.builder()
					.menuUrl("mantenimiento/envaces/")
					.menuName("Envaces")
					.icon("fa-envelope")
					.orden(4)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menuEstadosCCompras = MenusEntity.builder()
					.menuUrl("mantenimiento/estadoccompras/")
					.menuName("Estados de compras")
					.icon("fa-envelope")
					.orden(5)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menusEstadosCVentas = MenusEntity.builder()
					.menuUrl("mantenimiento/estadocventas/")
					.menuName("Estados de ventas")
					.icon("fa-envelope")
					.orden(6)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menusMonedas = MenusEntity.builder()
					.menuUrl("mantenimiento/monedas/")
					.menuName("Monedas")
					.icon("fa-envelope")
					.orden(7)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menuPuntosVentas = MenusEntity.builder()
					.menuUrl("mantenimiento/puntosventas/")
					.menuName("Puntos de venta")
					.icon("fa-envelope")
					.orden(8)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menuTipoCVentas = MenusEntity.builder()
					.menuUrl("mantenimiento/tipocventas/")
					.menuName("Tipos de ventas")
					.icon("fa-file-contract")
					.orden(10)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menusTipoDocumentos = MenusEntity.builder()
					.menuUrl("mantenimiento/tipodocumentos/")
					.menuName("Tipos de documentos")
					.icon("fa-id-card")
					.orden(11)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menusTipoEntidades = MenusEntity.builder()
					.menuUrl("mantenimiento/tipoentidades/")
					.menuName("Tipos de entidades")
					.icon("fa-person-circle-check")
					.orden(12)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menusTipoProductos = MenusEntity.builder()
					.menuUrl("mantenimiento/tipoproductos/")
					.menuName("Tipos de productos")
					.icon("fa-envelope")
					.orden(13)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menusTipoCCompras = MenusEntity.builder()
					.menuUrl("mantenimiento/tipoccompras/")
					.menuName("Tipos de compras")
					.icon("fa-file-contract")
					.orden(14)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();

			MenusEntity menusUnidades = MenusEntity.builder()
					.menuUrl("mantenimiento/unidades/")
					.menuName("Unidades")
					.icon("fa-scale-balanced")
					.orden(16)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menusAlmacenes = MenusEntity.builder()
					.menuUrl("mantenimiento/almacenes/")
					.menuName("Almacenes")
					.icon("fa-tent")
					.orden(17)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menusEntidades = MenusEntity.builder()
					.menuUrl("mantenimiento/entidades/")
					.menuName("Entidades")
					.icon("fa-users-gear")
					.orden(18)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
			MenusEntity menusTrabajadores = MenusEntity.builder()
					.menuUrl("mantenimiento/trabajadores/")
					.menuName("Trabajadores")
					.icon("fa-id-card-clip")
					.orden(19)
					.nivel(2L)
					.padre(menusForAdmin)
					.build();
// Asignar submenús al menú padre
			menusForAdmin.setSubmenus(Set.of(menusEmpresas,
					menusProductos, menuEnvaces, menuEstadosCCompras, menusEstadosCVentas, menusMonedas, menuPuntosVentas,
					menuTipoCVentas, menusTipoDocumentos, menusTipoEntidades, menusTipoProductos, menusTipoCCompras,
					menusUnidades, menusAlmacenes, menusEntidades, menusTrabajadores));
			MenusEntity menusCompras = MenusEntity.builder()
					.menuUrl("/")
					.menuName("Compras")
					.icon("fa-bag-shopping")
					.orden(2)
					.nivel(1L)
					.build();
			MenusEntity nuevaCompra = MenusEntity.builder()
					.menuUrl("compras/nuevo/")
					.menuName("Nueva Compra")
					.icon("fa-cart-plus")
					.orden(1)
					.nivel(2L)
					.padre(menusCompras)
					.build();
			MenusEntity comprobantesCompras = MenusEntity.builder()
					.menuUrl("compras/comprobantes/")
					.menuName("Comprobantes")
					.icon("fa-ticket")
					.orden(2)
					.nivel(2L)
					.padre(menusCompras)
					.build();
			// Asignar submenús al menú padre
			menusCompras.setSubmenus(Set.of(nuevaCompra, comprobantesCompras));
			MenusEntity caja = MenusEntity.builder()
					.menuUrl("/")
					.menuName("Caja")
					.icon("fa-vault")
					.orden(1)
					.nivel(1L)
					.build();
			MenusEntity cuentasPorPagar = MenusEntity.builder()
					.menuUrl("caja/cuentasxpagar/")
					.menuName("Cuentas por Pagar")
					.icon("fa-money-check")
					.orden(1)
					.nivel(2L)
					.padre(caja)
					.build();
			MenusEntity cuentasPorComprar = MenusEntity.builder()
					.menuUrl("caja/cuentasxcomprar/")
					.menuName("Cuentas por Cobrar")
					.icon("fa-cash-register")
					.orden(2)
					.nivel(2L)
					.padre(caja)
					.build();
			MenusEntity ingresarGastos = MenusEntity.builder()
					.menuUrl("caja/ingresargastos/")
					.menuName("Ingresar Gastos")
					.icon("fa-bag-shopping")
					.orden(3)
					.nivel(2L)
					.padre(caja)
					.build();
			// Asignar submenús al menú padre
			caja.setSubmenus(Set.of(cuentasPorPagar, cuentasPorComprar, ingresarGastos));

			MenusEntity menusVentas = MenusEntity.builder()
					.menuUrl("/")
					.menuName("Ventas")
					.icon("fa-shopping-cart")
					.orden(2)
					.nivel(1L)
					.build();

			MenusEntity nuevaVenta = MenusEntity.builder()
					.menuUrl("ventas/nuevo/")
					.menuName("Nueva Venta")
					.icon("fa-file-circle-plus")
					.orden(1)
					.nivel(2L)
					.padre(menusVentas)
					.build();

			MenusEntity comprobantes = MenusEntity.builder()
					.menuUrl("ventas/comprobantes/")
					.menuName("Comprobantes")
					.icon("fa-ticket")
					.orden(2)
					.nivel(2L)
					.padre(menusVentas)
					.build();
			menusVentas.setSubmenus(Set.of(nuevaVenta, comprobantes));

			MenusEntity menusAsistencias = MenusEntity.builder()
					.menuUrl("/")
					.menuName("Asistencias")
					.icon("fa-calendar-days")
					.orden(3)
					.nivel(1L)
					.build();
			MenusEntity nuevaAsistencia = MenusEntity.builder()
					.menuUrl("asistencias/nueva/")
					.menuName("Nueva Asistencia")
					.icon("fa-address-book")
					.orden(1)
					.nivel(2L)
					.padre(menusAsistencias)
					.build();
			menusAsistencias.setSubmenus(Set.of(nuevaAsistencia));

			MenusEntity menuAlmacenesPri = MenusEntity.builder()
					.menuUrl("/")
					.menuName("Almacenes")
					.icon("fa-store")
					.orden(4)
					.nivel(1L)
					.build();
			MenusEntity almacenesMovimientos = MenusEntity.builder()
					.menuUrl("almacenes/movimientos/")
					.menuName("Movimientos")
					.icon("fa-envelope")
					.orden(1)
					.nivel(2L)
					.padre(menuAlmacenesPri)
					.build();
			menusAlmacenes.setSubmenus(Set.of(almacenesMovimientos));

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
					.menus(Set.of(menusForAdmin, menusEmpresas, menusVentas, nuevaVenta, comprobantes, menusCompras, caja, menusAsistencias, nuevaAsistencia, menuAlmacenesPri, almacenesMovimientos))
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
					.menus(Set.of( menusEmpresas, menusVentas, nuevaVenta, comprobantes, menusCompras, caja, menusAsistencias, nuevaAsistencia))
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
					.menus(Set.of(menusVentas, nuevaVenta, comprobantes))
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
					.menus(Set.of(menusEmpresas))
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
					.menus(Set.of(menusEmpresas))
					.build();
			userRepository.saveAll(List.of(userAdmin, userUser, userDeveloper, userTester, userInvited));


		};
	}


}
