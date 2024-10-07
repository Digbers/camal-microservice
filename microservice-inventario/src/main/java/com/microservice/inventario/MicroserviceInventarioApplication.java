package com.microservice.inventario;

import com.microservice.inventario.persistence.entity.*;
import com.microservice.inventario.persistence.repository.IAlmacenRepository;
import com.microservice.inventario.persistence.repository.IAlmacenTipoRepository;
import com.microservice.inventario.persistence.repository.IMovimientosMotivosRepository;
import com.microservice.inventario.persistence.repository.IPuntoVentaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;

@EnableDiscoveryClient
@SpringBootApplication
@EnableAspectJAutoProxy
public class MicroserviceInventarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceInventarioApplication.class, args);
	}
	@Bean
	CommandLineRunner init(IAlmacenRepository IAlmacenRepository, IPuntoVentaRepository puntoVentaRepository, IAlmacenTipoRepository almacenTipoRepository, IMovimientosMotivosRepository movimientosMotivosRepository) {
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
			PuntoVentaEntity punto = PuntoVentaEntity.builder()
					.direccion("AV. INDEPENDENCIA")
					.idAlmacen(almacen)
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
		};
	}
}
