
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
@Table(name = "medicoes")
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sensor", nullable = false)
    private TipoSensor tipoSensor;

    @Column(nullable = false)
    private Double valor;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    protected Medicao() {
        // Construtor utilizado pelo JPA
    }

    public Medicao(TipoSensor tipoSensor, Double valor) {

        if (tipoSensor == null) {
            throw new DadosInvalidosException(
                    "O tipo do sensor e obrigatorio."
            );
        }

        if (valor == null || !Double.isFinite(valor)
                || valor < 0) {
            throw new DadosInvalidosException(
                    "O valor da medicao e invalido."
            );
        }

        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public TipoSensor getTipoSensor() {
        return tipoSensor;
    }

    public Double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
