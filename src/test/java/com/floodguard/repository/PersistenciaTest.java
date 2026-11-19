
package com.floodguard.repository;

import com.floodguard.model.Alerta;
import com.floodguard.model.Medicao;
import com.floodguard.model.NivelAlerta;
import com.floodguard.model.TipoSensor;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PersistenciaTest {

    @Autowired
    private MedicaoRepository medicaoRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @Test
    void deveSalvarMedicao() {

        Medicao medicao = new Medicao(
                TipoSensor.NIVEL_AGUA,
                85.0
        );

        Medicao salva = medicaoRepository.saveAndFlush(
                medicao
        );

        assertNotNull(salva.getId());
        assertEquals(85.0, salva.getValor());
        assertEquals(
                TipoSensor.NIVEL_AGUA,
                salva.getTipoSensor()
        );
    }

    @Test
    void deveConsultarMedicoesPorSensor() {

        medicaoRepository.save(
                new Medicao(TipoSensor.NIVEL_AGUA, 40.0)
        );

        medicaoRepository.save(
                new Medicao(TipoSensor.NIVEL_AGUA, 70.0)
        );

        medicaoRepository.save(
                new Medicao(TipoSensor.PLUVIOMETRICO, 25.0)
        );

        List<Medicao> medicoes =
                medicaoRepository
                        .findByTipoSensorOrderByIdDesc(
                                TipoSensor.NIVEL_AGUA
                        );

        assertEquals(2, medicoes.size());
        assertEquals(70.0, medicoes.get(0).getValor());
    }

    @Test
    void deveBuscarUltimasDezMedicoes() {

        for (int i = 1; i <= 12; i++) {
            medicaoRepository.save(
                    new Medicao(
                            TipoSensor.NIVEL_AGUA,
                            (double) i
                    )
            );
        }

        List<Medicao> medicoes =
                medicaoRepository
                        .findTop10ByTipoSensorOrderByIdDesc(
                                TipoSensor.NIVEL_AGUA
                        );

        assertEquals(10, medicoes.size());
        assertEquals(12.0, medicoes.get(0).getValor());
    }

    @Test
    void deveSalvarAlerta() {

        Alerta alerta = new Alerta(
                TipoSensor.NIVEL_AGUA,
                NivelAlerta.CRITICO,
                110.0,
                100.0,
                "Previsao ultrapassou o limite critico."
        );

        Alerta salvo = alertaRepository.saveAndFlush(
                alerta
        );

        assertNotNull(salvo.getId());
        assertEquals(
                NivelAlerta.CRITICO,
                salvo.getNivel()
        );
        assertEquals(110.0, salvo.getValorPrevisto());
    }

    @Test
    void deveConsultarAlertasCriticos() {

        alertaRepository.save(
                new Alerta(
                        TipoSensor.NIVEL_AGUA,
                        NivelAlerta.CRITICO,
                        110.0,
                        100.0,
                        "Nivel de agua critico."
                )
        );

        alertaRepository.save(
                new Alerta(
                        TipoSensor.PLUVIOMETRICO,
                        NivelAlerta.ATENCAO,
                        25.0,
                        20.0,
                        "Chuva acima do limite de atencao."
                )
        );

        List<Alerta> alertas =
                alertaRepository.findByNivelOrderByIdDesc(
                        NivelAlerta.CRITICO
                );

        assertEquals(1, alertas.size());
        assertEquals(
                NivelAlerta.CRITICO,
                alertas.get(0).getNivel()
        );
    }
}
