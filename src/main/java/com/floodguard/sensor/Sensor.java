package com.floodguard.sensor;

import com.floodguard.model.TipoSensor;

public interface Sensor {

    double lerValor();

    boolean verificarFuncionamento();

    TipoSensor getTipo();
}
