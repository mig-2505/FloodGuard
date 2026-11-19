package com.floodguard.sensor;

import com.floodguard.exception.FonteIndisponivelException;
import com.floodguard.model.TipoSensor;

import java.util.concurrent.ThreadLocalRandom;

public class SensorUmidadeSolo implements Sensor {

    private boolean funcionando = true;

    @Override
    public double lerValor() {

        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException(
                    "Sensor de umidade do solo indisponivel."
            );
        }

        return ThreadLocalRandom.current()
                .nextInt(0, 1001) / 10.0;
    }

    @Override
    public boolean verificarFuncionamento() {
        return funcionando;
    }

    @Override
    public TipoSensor getTipo() {
        return TipoSensor.UMIDADE_SOLO;
    }

    public void setFuncionando(boolean funcionando) {
        this.funcionando = funcionando;
    }
}
