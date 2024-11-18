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
						   EnvaseRepository envaseRepository,
						   IUnidadesRepository unidadesRepository
	) {
		return args -> {
			UnidadesEntity unidad = UnidadesEntity.builder().idEmpresa(1L).codigo("UNI").nombre("UNIDAD").usuarioCreacion("ADMIN").build();
			unidadesRepository.save(unidad);

			AlmacenTipoEntity tipo = AlmacenTipoEntity.builder().descripcion("TIENDA").idEmpresa(1L).codigo("TIE").build();
			AlmacenTipoEntity tipo2 = AlmacenTipoEntity.builder().codigo("DEP").idEmpresa(1L).descripcion("DEPOSITO").build();
			almacenTipoRepository.saveAll(List.of(tipo2, tipo));

			AlmacenEntity almacen = AlmacenEntity.builder().nombre("ALMACEN PRINCIPAL").idEmpresa(1L).tipoAlmacen(tipo).build();
			IAlmacenRepository.save(almacen);

			UbigeoEntity ubigeo = UbigeoEntity.builder().departamento("002321").ubigeoCodigo("002321").departamentoCompleto("LIMA").provincia("002321").nombre("LIMA").distrito("002321").distritoCompleto("LIMA").provinciaCompleto("LIMA").build();
			ubigeoRepository.save(ubigeo);

			PuntoVentaEntity punto = PuntoVentaEntity.builder().direccion("AV. INDEPENDENCIA").almacen(almacen).idEmpresa(1L).nombre("punto de venta 1").ubigeo(ubigeo).build();
			puntoVentaRepository.save(punto);

			MovimientosMotivosEntity motivo = MovimientosMotivosEntity.builder().codigo("COM").descripcion("COMPRA").usuarioCreacion("ADMIN").build();
			MovimientosMotivosEntity motivo2 = MovimientosMotivosEntity.builder().codigo("VEN").descripcion("VENTA").usuarioCreacion("ADMIN").build();
			movimientosMotivosRepository.saveAll(List.of(motivo2, motivo));

			ProductosTiposEntity tipoP = ProductosTiposEntity.builder().idEmpresa(1L).codigo("VIV").nombre("pollo vivo").usuarioCreacion("ADMIN").build();
			ProductosTiposEntity pollosSacrificado = ProductosTiposEntity.builder().idEmpresa(1L).codigo("SAC").nombre("pollo sacrificado").usuarioCreacion("ADMIN").build();
			ProductosTiposEntity tipoP3 = ProductosTiposEntity.builder().idEmpresa(1L).codigo("POL").nombre("pollo pollada").usuarioCreacion("ADMIN").build();
			productosTiposRepository.saveAll(List.of(tipoP, pollosSacrificado, tipoP3));

			EnvaseEntity eJava8 = EnvaseEntity.builder().idEmpresa(1L).tipoEnvase("JAVA").descripcion("Java 8 hembras").capacidad(8).estado("VACIO").pesoReferencia(new BigDecimal(1)).usuarioCreacion("ADMIN").build();
			EnvaseEntity eJava6 = EnvaseEntity.builder().idEmpresa(1L).tipoEnvase("JAVA").descripcion("Java 6 machos").capacidad(6).estado("OCUPADO").pesoReferencia(new BigDecimal(1)).usuarioCreacion("ADMIN").build();
			EnvaseEntity eTina9 = EnvaseEntity.builder().idEmpresa(1L).tipoEnvase("TINA").descripcion("Tina 9 pollos").capacidad(9).estado("VACIO").pesoReferencia(new BigDecimal(1)).usuarioCreacion("ADMIN").build();
			EnvaseEntity eTina10 = EnvaseEntity.builder().idEmpresa(1L).tipoEnvase("TINA").descripcion("Tina 10 pollos").capacidad(10).estado("VACIO").pesoReferencia(new BigDecimal(1)).usuarioCreacion("ADMIN").build();
			envaseRepository.saveAll(List.of(eTina10, eTina9, eJava6, eJava8));
			// Construir el objeto StockAlmacen
			StockAlmacen stock = StockAlmacen.builder()
					.idEmpresa(1L)
					.envase(eJava8)
					.almacen(almacen)
					.cantidadEnvase(0)
					.cantidadProducto(0)
					.pesoTotal(new BigDecimal(0))
					.fechaRegistro(LocalDate.now())
					.usuarioCreacion("ADMIN")
					.build();
			StockAlmacen stock2 = StockAlmacen.builder()
					.idEmpresa(1L)
					.envase(eJava6)
					.almacen(almacen)
					.cantidadEnvase(0)
					.cantidadProducto(0)
					.pesoTotal(new BigDecimal(16))
					.fechaRegistro(LocalDate.now())
					.usuarioCreacion("ADMIN")
					.build();
			StockAlmacen stockPolloSacrificado = StockAlmacen.builder()
					.idEmpresa(1L)
					.almacen(almacen)
					.cantidadProducto(50)
					.pesoTotal(new BigDecimal(90))
					.fechaRegistro(LocalDate.now())
					.usuarioCreacion("ADMIN")
					.build();
			StockAlmacen stockCarcasa = StockAlmacen.builder()
					.idEmpresa(1L)
					.almacen(almacen)
					.envase(eTina10)
					.cantidadProducto(0)
					.pesoTotal(new BigDecimal(0))
					.fechaRegistro(LocalDate.now())
					.usuarioCreacion("ADMIN")
					.build();
			ProductosEntity productosEntity = ProductosEntity.builder()
					.empresaId(1L)
					.codigo("001")
					.nombre("pollo")
					.tipo(tipoP)
					.unidad(unidad)
					.stockAlmacenList(List.of(stock, stock2))  // Añadir la lista con el stock
					.generarStock(true)
					.estado(true)
					.precioSugerido(new BigDecimal(23))
					.usuarioCreacion("ADMIN")
					.build();
			ProductosEntity polloSacrificado = ProductosEntity.builder()
					.empresaId(1L)
					.codigo("POLLOSAC")
					.nombre("pollo sacrificado")
					.tipo(pollosSacrificado)
					.unidad(unidad)
					.stockAlmacenList(List.of(stockPolloSacrificado))  // Añadir la lista con el stock
					.generarStock(true)
					.estado(true)
					.precioSugerido(new BigDecimal(10))
					.usuarioCreacion("ADMIN")
					.build();
			ProductosEntity polloCarcasa = ProductosEntity.builder()
					.empresaId(1L)
					.codigo("POLCAR")
					.nombre("pollo carcasa")
					.tipo(tipoP3)
					.unidad(unidad)
					.stockAlmacenList(List.of(stockCarcasa))  // Añadir la lista con el stock
					.generarStock(true)
					.estado(true)
					.precioSugerido(new BigDecimal(10))
					.usuarioCreacion("ADMIN")
					.build();
			stock.setProducto(productosEntity);
			stock2.setProducto(productosEntity);
			stockCarcasa.setProducto(polloCarcasa);
			stockPolloSacrificado.setProducto(polloSacrificado);

			productosRepository.saveAll(List.of(productosEntity, polloSacrificado, polloCarcasa));
		};
	}
}
