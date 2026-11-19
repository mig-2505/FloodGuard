package com.floodguard.model;

import com.floodguard.exception.*;
import com.floodguard.prediction.*;
import com.floodguard.sensor.*;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DominioTest {

    @Test
    void deveCriarRegiaoMonitorada() {

        RegiaoMonitorada regiao =
                new RegiaoMonitorada(
                        1L, "Centro", "Zona Leste"
                );

        assertEquals("ATIVO", regiao.getStatus());
        assertEquals("Centro", regiao.getNome());
    }

    @Test
    void deveGerarLeituraValida() {

        Sensor sensor = new SensorPluviometrico();

        double valor = sensor.lerValor();

        assertTrue(valor >= 0);
        assertTrue(valor <= 120);
    }

    @Test
    void deveLancarExcecaoSeSensorFalhar() {

        SensorNivelAgua sensor = new SensorNivelAgua();
        sensor.setFuncionando(false);

        assertThrows(
                FonteIndisponivelException.class,
                sensor::lerValor
        );
    }

    @Test
    void deveCalcularMediaMovel() {

        EstrategiaPrevisao estrategia =
                new PrevisaoMediaMovel();

        double resultado = estrategia.prever(
                List.of(40.0, 50.0, 60.0, 70.0, 80.0)
        );

        assertEquals(60.0, resultado, 0.001);
    }

    @Test
    void deveCalcularPrevisaoComparacao() {

        EstrategiaPrevisao estrategia =
                new PrevisaoComparacao();

        double resultado = estrategia.prever(
                List.of(
                        30.0, 35.0, 40.0, 45.0, 50.0,
                        60.0, 65.0, 70.0, 75.0, 80.0
                )
        );

        assertEquals(100.0, resultado, 0.001);
    }

    @Test
    void deveClassificarNivelDeAlerta() {

        DadosTerritorio dados =
                new DadosTerritorio("1234");

        NivelAlerta nivel = dados.classificarRisco(
                TipoSensor.NIVEL_AGUA,
                85.0
        );

        assertEquals(NivelAlerta.ALERTA, nivel);
    }

    @Test
    void deveRejeitarMedicaoInvalida() {

        DadosTerritorio dados =
                new DadosTerritorio("1234");

        assertThrows(
                DadosInvalidosException.class,
                () -> dados.validarValor(
                        TipoSensor.NIVEL_AGUA,
                        -10
                )
        );
    }

    @Test
    void deveRejeitarLimitesForaDeOrdem() {

        DadosTerritorio dados =
                new DadosTerritorio("1234");

        assertThrows(
                DadosInvalidosException.class,
                () -> dados.alterarLimite(
                        TipoSensor.NIVEL_AGUA,
                        NivelAlerta.ATENCAO,
                        90,
                        "1234"
                )
        );
    }
}
