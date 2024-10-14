package com.microservice.empresas;

import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.entity.EntidadesTiposEntity;
import com.microservice.empresas.persistence.repository.IDocumentosTiposRepository;
import com.microservice.empresas.persistence.repository.IEmpresaRepository;
import com.microservice.empresas.persistence.repository.IEntidadesTiposRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

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
	CommandLineRunner init(IDocumentosTiposRepository documentosTiposRepository, IEntidadesTiposRepository entidadesTiposRepository, IEmpresaRepository empresaRepository) {
		return args -> {
			EmpresaEntity empresa = EmpresaEntity.builder()
					.empresaCodigo("AVI DON JOSE")
					.departamento("Arequipa")
					.celular("1221221")
					.distrito("Cerro Colorado")
					.direccion("calle 33")
					.provincia("Arequipa")
					.razonSocial("AVICOLA DON JOSE")
					.ruc("10101010101")
					.correo("bOjz1@example.com")
					.web("https://www.avicoladonjose.com.pe")
					.usuarioCreacion("ADMIN")
					.ubigeo("150101")
					.build();
			EmpresaEntity empresa1 = EmpresaEntity.builder()
					.empresaCodigo("CAFE SUB")
					.departamento("Arequipa")
					.celular("1221221")
					.distrito("Cerro Colorado")
					.direccion("calle 33")
					.provincia("Arequipa")
					.razonSocial("CAFE SUB")
					.ruc("10101014545")
					.correo("bOjz1@example.com")
					.web("https://www.cafesub.com.pe")
					.usuarioCreacion("ADMIN")
					.ubigeo("150101")
					.build();
			empresaRepository.saveAll(Set.of(empresa, empresa1));
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
		};
	}

}
