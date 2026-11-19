package com.floodguard.sensor;

import com.floodguard.exception.FonteIndisponivelException;
import com.floodguard.model.TipoSensor;

import java.util.concurrent.ThreadLocalRandom;

public class SensorPluviometrico implements Sensor {

    private boolean funcionando = true;

    @Override
    public double lerValor() {

        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException(
                    "Sensor pluviometrico indisponivel."
            );
        }

        return ThreadLocalRandom.current()
                .nextInt(0, 1201) / 10.0;
    }

    @Override
    public boolean verificarFuncionamento() {
        return funcionando;
    }

    @Override
    public TipoSensor getTipo() {
        return TipoSensor.PLUVIOMETRICO;
    }

    public void setFuncionando(boolean funcionando) {
        this.funcionando = funcionando;
    }
}
