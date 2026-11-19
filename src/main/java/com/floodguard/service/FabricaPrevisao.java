package com.floodguard.service;

import com.floodguard.exception.DadosInvalidosException;

import com.floodguard.prediction.EstrategiaPrevisao;
import com.floodguard.prediction.PrevisaoComparacao;
import com.floodguard.prediction.PrevisaoMediaMovel;
import com.floodguard.prediction.TipoPrevisao;

import org.springframework.stereotype.Component;

@Component
public class FabricaPrevisao {

    public EstrategiaPrevisao criarEstrategia(
            TipoPrevisao tipo
    ) {

        if (tipo == null) {
            throw new DadosInvalidosException(
                    "Tipo de previsao obrigatorio."
            );
        }

        return switch (tipo) {

            case MEDIA_MOVEL ->
                    new PrevisaoMediaMovel();

            case COMPARACAO ->
                    new PrevisaoComparacao();
        };
    }
}
