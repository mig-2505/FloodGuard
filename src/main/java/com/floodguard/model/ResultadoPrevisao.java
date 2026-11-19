package com.floodguard.model;

import com.floodguard.prediction.TipoPrevisao;

public record ResultadoPrevisao(

        TipoSensor tipoSensor,
        TipoPrevisao tipoPrevisao,
        double valorPrevisto,
        NivelAlerta nivelAlerta,
        String justificativa

) {
}
