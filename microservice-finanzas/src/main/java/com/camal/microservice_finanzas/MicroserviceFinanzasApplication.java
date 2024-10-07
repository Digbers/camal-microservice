package com.camal.microservice_finanzas;

import com.camal.microservice_finanzas.persistence.entity.FormasCobrosEntity;
import com.camal.microservice_finanzas.persistence.entity.FormasPagosEntity;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.repository.IFormasCobrosRepository;
import com.camal.microservice_finanzas.persistence.repository.IFormasPagosRepository;
import com.camal.microservice_finanzas.persistence.repository.IMonedasRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Set;

@SpringBootApplication
public class MicroserviceFinanzasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceFinanzasApplication.class, args);
	}
	@Bean
	CommandLineRunner init(IMonedasRepository monedasRepository, IFormasCobrosRepository formasCobrosRepository, IFormasPagosRepository formasPagosRepository) {
		return args -> {
			MonedasEntity moneda1 = MonedasEntity.builder()
					.codigo("PEN")
					.simbolo("S/.")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.nombre("Soles").build();
			MonedasEntity moneda2 = MonedasEntity.builder()
					.codigo("USD")
					.simbolo("$")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.nombre("Dólares").build();
			monedasRepository.saveAll(Set.of(moneda1, moneda2));

			FormasPagosEntity formaPago1 = FormasPagosEntity.builder()
					.codigo("CRE")
					.descripcion("CREDITO")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.moneda(moneda1)
					.build();
			FormasPagosEntity formaPago2 = FormasPagosEntity.builder()
					.codigo("CAN")
					.descripcion("CANCELADO")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.moneda(moneda1)
					.build();
			formasPagosRepository.saveAll(Set.of(formaPago1, formaPago2));

			FormasCobrosEntity formaCobro1 = FormasCobrosEntity.builder()
					.codigo("EFE")
					.descripcion("Efectivo")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.moneda(moneda1)
					.build();
			FormasCobrosEntity formaCobro2 = FormasCobrosEntity.builder()
					.codigo("CRE")
					.descripcion("CREDITO")
					.idEmpresa(1L)
					.usuarioCreacion("ADMIN")
					.moneda(moneda1)
					.build();
			formasCobrosRepository.saveAll(Set.of(formaCobro1, formaCobro2));

		};
	}
}
