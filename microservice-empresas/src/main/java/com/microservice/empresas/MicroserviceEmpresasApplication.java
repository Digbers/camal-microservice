package com.microservice.empresas;

import com.microservice.empresas.persistence.entity.*;
import com.microservice.empresas.persistence.repository.IDocumentosTiposRepository;
import com.microservice.empresas.persistence.repository.IEmpresaRepository;
import com.microservice.empresas.persistence.repository.IEntidadRepository;
import com.microservice.empresas.persistence.repository.IEntidadesTiposRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@EnableDiscoveryClient
@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan("com.microservice.empresas")
public class MicroserviceEmpresasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceEmpresasApplication.class, args);
	}
	@Bean
	CommandLineRunner init(IDocumentosTiposRepository documentosTiposRepository, IEntidadesTiposRepository entidadesTiposRepository, IEmpresaRepository empresaRepository, IEntidadRepository entidadRepository) {
		return args -> {
			EmpresaEntity empresa = EmpresaEntity.builder()
					.empresaCodigo("AV DON JOSE")
					.departamento("Arequipa")
					.celular("1221221")
					.distrito("Cerro Colorado")
					.direccion("calle 33")
					.provincia("Arequipa")
					.razonSocial("AVICOLA DON JOSE S.A.C")
					.estado(true)
					.ruc("10101010101")
					.correo("avicolaDonJose@gmail.com")
					.web("https://www.avicoladonjose.com.pe")
					.usuarioCreacion("ADMIN")
					.ubigeo("150101")
					.build();
			empresaRepository.saveAll(Set.of(empresa));
			DocumentoTiposEntity ruc = DocumentoTiposEntity.builder()
					.empresa(empresa)
					.descripcion("Registro Unico de Contribuyente")
					.docCodigo("RUC")
					.codigoSunat("6")
					.usuarioCreacion("ADMIN")
					.build();
			DocumentoTiposEntity dni = DocumentoTiposEntity.builder()
					.empresa(empresa)
					.docCodigo("DNI")
					.codigoSunat("1")
					.descripcion("Documento Nacional de Identidad")
					.usuarioCreacion("ADMIN")
					.build();
			documentosTiposRepository.saveAll(List.of(ruc, dni));

			EntidadesTiposEntity cliente = EntidadesTiposEntity.builder()
					.empresa(empresa)
					.tipoCodigo("CLI")
					.descripcion("CLIENTE")
					.usuarioCreacion("ADMIN")
					.build();
			EntidadesTiposEntity proveedor = EntidadesTiposEntity.builder()
					.empresa(empresa)
					.tipoCodigo("PRO")
					.descripcion("PROVEEDOR")
					.usuarioCreacion("ADMIN")
					.build();
			EntidadesTiposEntity trabajador = EntidadesTiposEntity.builder()
					.empresa(empresa)
					.tipoCodigo("TRA")
					.descripcion("TRABAJADOR")
					.usuarioCreacion("ADMIN")
					.build();
			EntidadesTiposEntity vendedor = EntidadesTiposEntity.builder()
					.empresa(empresa)
					.tipoCodigo("VEN")
					.descripcion("VENDEDOR")
					.usuarioCreacion("ADMIN")
					.build();
			entidadesTiposRepository.saveAll(List.of(cliente, proveedor, trabajador, vendedor));

			EntidadEntity trabjadorAlex = EntidadEntity.builder()
					.empresa(empresa)
					.nombre("Alex")
					.apellidoPaterno("Alejandro")
					.apellidoMaterno("Perez")
					.documentoTipo(dni)
					.nroDocumento("12345678")
					.email("alex@gmail.com")
					.celular("123456789")
					.direccion("calle 1")
					.estado(true)
					.sexo("M")
					.condicion("Activo")
					.entidadesTiposList(List.of(trabajador))
					.build();
			AsistenciasEntity asistencia = AsistenciasEntity.builder()
					.entidad(trabjadorAlex)
					.fechaAsistencia(LocalDate.now())
					.asistio(true)
					.usuarioCreacion("ADMIN")
					.build();
			AsistenciasEntity asistencia2 = AsistenciasEntity.builder()
					.entidad(trabjadorAlex)
					.fechaAsistencia( LocalDate.parse("2024-11-06"))
					.asistio(true)
					.usuarioCreacion("ADMIN")
					.build();
			trabjadorAlex.setAsistencias(List.of(asistencia, asistencia2));
			entidadRepository.save(trabjadorAlex);
		};
	}

}
