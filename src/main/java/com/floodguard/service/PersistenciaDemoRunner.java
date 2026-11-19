package com.floodguard.service;

import com.floodguard.model.Alerta;
import com.floodguard.model.Medicao;
import com.floodguard.model.NivelAlerta;
import com.floodguard.model.TipoSensor;

import com.floodguard.repository.AlertaRepository;
import com.floodguard.repository.MedicaoRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PersistenciaDemoRunner implements CommandLineRunner {

    private final MedicaoRepository medicaoRepository;
    private final AlertaRepository alertaRepository;

    public PersistenciaDemoRunner(
            MedicaoRepository medicaoRepository,
            AlertaRepository alertaRepository
    ) {
        this.medicaoRepository = medicaoRepository;
        this.alertaRepository = alertaRepository;
    }

    @Override
    public void run(String... args) {

        System.out.println();
        System.out.println("=== TESTE DE PERSISTENCIA ===");

        if (medicaoRepository.count() == 0) {

            Medicao medicao = new Medicao(
                    TipoSensor.NIVEL_AGUA,
                    85.0
            );

            medicaoRepository.save(medicao);

            System.out.println(
                    "Medicao de exemplo salva com sucesso!"
            );
        }

        if (alertaRepository.count() == 0) {

            Alerta alerta = new Alerta(
                    TipoSensor.NIVEL_AGUA,
                    NivelAlerta.ALERTA,
                    85.0,
                    70.0,
                    "Valor previsto ultrapassou o limite de alerta."
            );

            alertaRepository.save(alerta);

            System.out.println(
                    "Alerta de exemplo salvo com sucesso!"
            );
        }

        System.out.println(
                "Total de medicoes: "
                        + medicaoRepository.count()
        );

        System.out.println(
                "Total de alertas: "
                        + alertaRepository.count()
        );

        System.out.println("=== FIM DO TESTE ===");
    }
}
