package com.microservice.inventario;

import com.microservice.inventario.persistence.entity.*;
import com.microservice.inventario.persistence.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@EnableDiscoveryClient
@SpringBootApplication
@EnableAspectJAutoProxy
public class MicroserviceInventarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceInventarioApplication.class, args);
	}
	@Bean
	CommandLineRunner init(IAlmacenRepository IAlmacenRepository,
						   IPuntoVentaRepository puntoVentaRepository,
						   IAlmacenTipoRepository almacenTipoRepository,
						   IMovimientosMotivosRepository movimientosMotivosRepository,
						   IUbigeoRepository ubigeoRepository,
						   ProductosRepository productosRepository,
						   ProductosTiposRepository productosTiposRepository,
						   EnvaseRepository envaseRepository) {
		return args -> {

			AlmacenTipoEntity tipo = AlmacenTipoEntity.builder()
					.descripcion("TIENDA")
					.idEmpresa(1L)
					.codigo(TipoEnum.valueOf(TipoEnum.TIE.name()))
					.build();
			AlmacenTipoEntity tipo2 = AlmacenTipoEntity.builder()
					.codigo(TipoEnum.valueOf(TipoEnum.DEP.name()))
					.idEmpresa(1L)
					.descripcion("DEPOSITO")
					.build();
			almacenTipoRepository.saveAll(List.of(tipo2));
			AlmacenEntity almacen = AlmacenEntity.builder()
					.nombre("ALMACEN PRINCIPAL")
					.idEmpresa(1L)
					.tipoAlmacen(tipo)
					.build();

			IAlmacenRepository.save(almacen);
			UbigeoEntity ubigeo = UbigeoEntity.builder()
					.departamento("002321")
					.ubigeoCodigo("002321")
					.departamentoCompleto("LIMA")
					.provincia("002321")
					.nombre("LIMA")
					.distrito("002321")
					.distritoCompleto("LIMA")
					.provinciaCompleto("LIMA")
					.build();
			ubigeoRepository.save(ubigeo);
			PuntoVentaEntity punto = PuntoVentaEntity.builder()
					.direccion("AV. INDEPENDENCIA")
					.almacen(almacen)
					.idEmpresa(1L)
					.nombre("punto de venta 1")
					.ubigeo(ubigeo)
					.build();
			puntoVentaRepository.save(punto);
			MovimientosMotivosEntity motivo = MovimientosMotivosEntity.builder()
					.codigo("COM")
					.descripcion("COMPRA")
					.usuarioCreacion("admin")
					.build();
			MovimientosMotivosEntity motivo2 = MovimientosMotivosEntity.builder()
					.codigo("VEN")
					.descripcion("VENTA")
					.usuarioCreacion("admin")
					.build();
			movimientosMotivosRepository.saveAll(List.of(motivo2, motivo));
			ProductosTiposEntity tipoP = ProductosTiposEntity.builder()
					.codigo("VIV")
					.nombre("pollo vivo")
					.usuarioCreacion("admin")
					.build();
			productosTiposRepository.save(tipoP);
			EnvaseEntity envase = EnvaseEntity.builder()
					.idEmpresa(1L)
					.tipoEnvase("JAVA")
					.descripcion("Java 8 hembras")
					.capacidad(8)
					.estado("VACIO")
					.pesoReferencia(new BigDecimal(1))
					.usuarioCreacion("admin")
					.build();
			EnvaseEntity envase2 = EnvaseEntity.builder()
					.idEmpresa(1L)
					.tipoEnvase("JAVA")
					.descripcion("Java 6 machos")
					.capacidad(6)
					.estado("OCUPADO")
					.pesoReferencia(new BigDecimal(1))
					.usuarioCreacion("admin")
					.build();
			envaseRepository.saveAll(List.of(envase, envase2));
			// Construir el objeto StockAlmacen
			StockAlmacen stock = StockAlmacen.builder()
					.idEmpresa(1L)
					.envase(envase)
					.almacen(almacen)
					.cantidadEnvase(4)
					.cantidadProducto(32)
					.pesoTotal(new BigDecimal(32))
					.fechaRegistro(LocalDate.now())
					.usuarioCreacion("admin")
					.build();
			StockAlmacen stock2 = StockAlmacen.builder()
					.idEmpresa(1L)
					.envase(envase2)
					.almacen(almacen)
					.cantidadEnvase(2)
					.cantidadProducto(16)
					.pesoTotal(new BigDecimal(16))
					.fechaRegistro(LocalDate.now())
					.usuarioCreacion("admin")
					.build();
			ProductosEntity productosEntity = ProductosEntity.builder()
					.empresaId(1L)
					.codigo("001")
					.nombre("pollo")
					.tipo(tipoP)
					.stockAlmacenList(List.of(stock, stock2))  // Añadir la lista con el stock
					.generarStock(true)
					.estado(true)
					.precioSugerido(new BigDecimal(23))
					.usuarioCreacion("admin")
					.build();
			stock.setProducto(productosEntity);
			stock2.setProducto(productosEntity);
			productosRepository.save(productosEntity);
		};
	}
}
