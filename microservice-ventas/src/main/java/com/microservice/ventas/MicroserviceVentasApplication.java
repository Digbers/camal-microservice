package com.microservice.ventas;

import com.microservice.ventas.entity.*;
import com.microservice.ventas.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.Set;

@EnableDiscoveryClient
@SpringBootApplication
public class MicroserviceVentasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceVentasApplication.class, args);
	}
	@Bean
	CommandLineRunner init(IComprobantesVentasEstadosRepository comprobantesVentasEstadosRepository, IComprobantesTiposVentasRepository comprobantesTiposVentasRepository, IComprobantesCompraEstadoRepository comprobantesCompraEstadoRepository, IComprobantesTiposComprasRepository comprobantesTiposComprasRepository, ISeriesRepository iSeriesRepository) {
		return args -> {
			ComprobantesVentasEstadoEntity comprobantesVentasEstado1 = ComprobantesVentasEstadoEntity.builder()
					.codigo("ANU")
					.descripcion("Anulado")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesVentasEstadoEntity comprobantesVentasEstado2 = ComprobantesVentasEstadoEntity.builder()
					.codigo("CAN")
					.descripcion("Cancelado")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesVentasEstadoEntity comprobantesVentasEstado3 = ComprobantesVentasEstadoEntity.builder()
					.codigo("CON")
					.descripcion("Contado")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesVentasEstadoEntity comprobantesVentasEstado4 = ComprobantesVentasEstadoEntity.builder()
					.codigo("CRE")
					.descripcion("Credito")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesVentasEstadoEntity comprobantesVentasEstado5 = ComprobantesVentasEstadoEntity.builder()
					.codigo("EMI")
					.descripcion("Emitido")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesVentasEstadoEntity comprobantesVentasEstado6 = ComprobantesVentasEstadoEntity.builder()
					.codigo("PRO")
					.descripcion("Promocion")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			comprobantesVentasEstadosRepository.saveAll(Set.of(comprobantesVentasEstado1, comprobantesVentasEstado2, comprobantesVentasEstado3, comprobantesVentasEstado4, comprobantesVentasEstado5, comprobantesVentasEstado6));
			ComprobantesTiposVentasEntity comprobantesTiposVentas1 = ComprobantesTiposVentasEntity.builder()
					.codigo("TIK")
					.descripcion("TICKET")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesTiposVentasEntity comprobantesTiposVentas2 = ComprobantesTiposVentasEntity.builder()
					.codigo("BOL")
					.descripcion("BOLETA")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesTiposVentasEntity comprobantesTiposVentas3 = ComprobantesTiposVentasEntity.builder()
					.codigo("FAC")
					.descripcion("FACTURA")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			comprobantesTiposVentasRepository.saveAll(Set.of(comprobantesTiposVentas1, comprobantesTiposVentas2, comprobantesTiposVentas3));
			ComprobantesComprasEstadosEntity comprobantesComprasEstados1 = ComprobantesComprasEstadosEntity.builder()
					.codigo("ANU")
					.descripcion("Anulado")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesComprasEstadosEntity comprobantesComprasEstados2 = ComprobantesComprasEstadosEntity.builder()
					.codigo("CAN")
					.descripcion("Cancelado")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesComprasEstadosEntity comprobantesComprasEstados3 = ComprobantesComprasEstadosEntity.builder()
					.codigo("PRO")
					.descripcion("Promocion")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesComprasEstadosEntity comprobantesComprasEstados4 = ComprobantesComprasEstadosEntity.builder()
					.codigo("CRE")
					.descripcion("Credito")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			comprobantesCompraEstadoRepository.saveAll(Set.of(comprobantesComprasEstados1, comprobantesComprasEstados2, comprobantesComprasEstados3, comprobantesComprasEstados4));
			ComprobantesTiposComprasEntity comprobantesTiposCompras1 = ComprobantesTiposComprasEntity.builder()
					.codigo("TIK")
					.descripcion("TICKET")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesTiposComprasEntity comprobantesTiposCompras2 = ComprobantesTiposComprasEntity.builder()
					.codigo("BOL")
					.descripcion("BOLETA")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			ComprobantesTiposComprasEntity comprobantesTiposCompras3 = ComprobantesTiposComprasEntity.builder()
					.codigo("FAC")
					.descripcion("FACTURA")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.build();
			comprobantesTiposComprasRepository.saveAll(Set.of(comprobantesTiposCompras1, comprobantesTiposCompras2, comprobantesTiposCompras3));
			SeriesEntity serie1 = SeriesEntity.builder()
					.id(1L)
					.idEmpresa(1L)
					.idPuntoVenta(1L)
					.codigoTipoComprobante(comprobantesTiposVentas1)
					.codigoSerie("T001")
					.defaultSerie(true)
					.build();
			SeriesEntity serie2 = SeriesEntity.builder()
					.id(2L)
					.idEmpresa(1L)
					.idPuntoVenta(1L)
					.codigoTipoComprobante(comprobantesTiposVentas1)
					.codigoSerie("T002")
					.defaultSerie(false)
					.build();
			SeriesEntity serie3 = SeriesEntity.builder()
					.id(3L)
					.idEmpresa(1L)
					.idPuntoVenta(1L)
					.codigoTipoComprobante(comprobantesTiposVentas2)
					.codigoSerie("B001")
					.defaultSerie(false)
					.build();
			SeriesEntity serie4 = SeriesEntity.builder()
					.id(4L)
					.idEmpresa(1L)
					.idPuntoVenta(1L)
					.codigoTipoComprobante(comprobantesTiposVentas1)
					.codigoSerie("B002")
					.defaultSerie(true)
					.build();
			SeriesEntity serie5 = SeriesEntity.builder()
					.id(5L)
					.idEmpresa(1L)
					.idPuntoVenta(1L)
					.codigoTipoComprobante(comprobantesTiposVentas3)
					.codigoSerie("F003")
					.defaultSerie(false)
					.build();
			iSeriesRepository.saveAll(Set.of(serie1, serie2, serie3, serie4, serie5));
		};
	}
}
