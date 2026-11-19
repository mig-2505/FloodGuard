package com.floodguard.model;

import com.floodguard.exception.DadosInvalidosException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sensor", nullable = false)
    private TipoSensor tipoSensor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAlerta nivel;

    @Column(name = "valor_previsto", nullable = false)
    private Double valorPrevisto;

    @Column(name = "limite_atingido", nullable = false)
    private Double limiteAtingido;

    @Column(nullable = false, length = 500)
    private String justificativa;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    protected Alerta() {
        // Construtor utilizado pelo JPA
    }

    public Alerta(
            TipoSensor tipoSensor,
            NivelAlerta nivel,
            Double valorPrevisto,
            Double limiteAtingido,
            String justificativa
    ) {

        if (tipoSensor == null || nivel == null) {
            throw new DadosInvalidosException(
                    "Sensor e nivel de alerta sao obrigatorios."
            );
        }

        if (nivel == NivelAlerta.NORMAL) {
            throw new DadosInvalidosException(
                    "NORMAL nao deve gerar alerta."
            );
        }

        if (valorPrevisto == null
                || !Double.isFinite(valorPrevisto)
                || valorPrevisto < 0) {
            throw new DadosInvalidosException(
                    "Valor previsto invalido."
            );
        }

        if (limiteAtingido == null
                || !Double.isFinite(limiteAtingido)
                || limiteAtingido < 0) {
            throw new DadosInvalidosException(
                    "Limite de alerta invalido."
            );
        }

        if (justificativa == null
                || justificativa.isBlank()
                || justificativa.length() > 500) {
            throw new DadosInvalidosException(
                    "Justificativa invalida."
            );
        }

        this.tipoSensor = tipoSensor;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limiteAtingido = limiteAtingido;
        this.justificativa = justificativa;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public TipoSensor getTipoSensor() {
        return tipoSensor;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public Double getValorPrevisto() {
        return valorPrevisto;
    }

    public Double getLimiteAtingido() {
        return limiteAtingido;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
