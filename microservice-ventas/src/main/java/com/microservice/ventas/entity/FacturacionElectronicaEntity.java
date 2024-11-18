package com.microservice.ventas.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.cglib.core.Local;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "facturacion_electronica")
public class FacturacionElectronicaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fel_id")
    private Long felId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fel_iddocreferencia", referencedColumnName = "id")
    private ComprobantesVentasCabEntity comprobantesVentasCabEntity;

    @Column(name = "fel_tipodocumento", length = 3)
    private String tipoDocumento;

    @Column(name = "fel_serie", length = 4)
    private String serie;

    @Column(name = "fel_numero", length = 9)
    private String numero;

    @Column(name = "fel_docsgenerado")
    private Boolean docsGenerado;

    @Column(name = "fel_xmlrevisado")
    private Boolean xmlRevisado;

    @Column(name = "fee_id", length = 3, columnDefinition = "CHAR(3) DEFAULT 'INI'")
    private String feeId;

    @Column(name = "fel_fechaenvio")
    private LocalDate fechaEnvio;

    @Column(name = "fel_fecharecepcion")
    private LocalDate fechaRecepcion;

    @Column(name = "usu_codigo", length = 8)
    private String usuarioCodigo;

    @Column(name = "fel_nroticket", length = 200)
    private String nroTicket;

    @Column(name = "fel_bloque")
    private Integer bloque;

    @Column(name = "fel_rutaxml", length = 200)
    private String rutaXml;

    @Column(name = "fel_rutaxmlresumen", length = 200)
    private String rutaXmlResumen;

    @Column(name = "fel_rutacdr", length = 200)
    private String rutaCdr;

    @Column(name = "fel_valorfirma", length = 500)
    private String valorFirma;

    @Column(name = "fel_valorresumen", length = 500)
    private String valorResumen;

    @Column(name = "fel_fechaenviobaja")
    private LocalDate fechaEnvioBaja;

    @Column(name = "fel_fecharecepcionbaja")
    private LocalDate fechaRecepcionBaja;

    @Column(name = "usu_codigobaja", length = 8)
    private String usuarioCodigoBaja;

    @Column(name = "fel_nroticketbaja", length = 200)
    private String nroTicketBaja;

    @Column(name = "fel_bloquebaja")
    private Integer bloqueBaja;

    @Column(name = "fel_rutaxmlbaja", length = 200)
    private String rutaXmlBaja;

    @Column(name = "fel_rutacdrbaja", length = 200)
    private String rutaCdrBaja;

    @Column(name = "fel_observacion")
    private String observacion;

    @CreationTimestamp
    @Column(name = "fel_feccreacion", updatable = false)
    private Timestamp fechaCreacion;

    @Column(name = "fel_facturadorobservacion", length = 250)
    private String facturadorObservacion;

    @Column(name = "fel_facturadorfirmadigital", length = 250)
    private String facturadorFirmaDigital;

    @Column(name = "fel_facturadorcodestado", length = 2)
    private String facturadorCodEstado;

    @Column(name = "fel_firmadigital", length = 500)
    private String firmaDigital;

    @Column(name = "fel_resumen", length = 250)
    private String resumen;

    @Column(name = "fel_anulado")
    private Boolean anulado;

    @Column(name = "fe_usucreacion", length = 8)
    private String usuarioCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;

    @Column(name = "fel_usumodificacion", length = 8)
    private String usuarioModificacion;

    // Getters and setters...
}
