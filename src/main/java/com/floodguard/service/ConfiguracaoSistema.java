package com.floodguard.service;

import com.floodguard.model.DadosTerritorio;
import com.floodguard.model.NivelAlerta;
import com.floodguard.model.TipoSensor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoSistema {

    private final DadosTerritorio dadosTerritorio;

    public ConfiguracaoSistema(
            @Value("${floodguard.codigo-acesso:1234}")
            String codigoAcesso
    ) {
        this.dadosTerritorio =
                new DadosTerritorio(codigoAcesso);
    }

    public NivelAlerta classificarRisco(
            TipoSensor tipoSensor,
            double valor
    ) {
        return dadosTerritorio.classificarRisco(
                tipoSensor,
                valor
        );
    }

    public double consultarLimite(
            TipoSensor tipoSensor,
            NivelAlerta nivel
    ) {
        return dadosTerritorio.consultarLimite(
                tipoSensor,
                nivel
        );
    }

    public void alterarLimite(
            TipoSensor tipoSensor,
            NivelAlerta nivel,
            double novoValor,
            String codigoAcesso
    ) {
        dadosTerritorio.alterarLimite(
                tipoSensor,
                nivel,
                novoValor,
                codigoAcesso
        );
    }

    public void validarValor(
            TipoSensor tipoSensor,
            double valor
    ) {
        dadosTerritorio.validarValor(
                tipoSensor,
                valor
        );
    }
}
