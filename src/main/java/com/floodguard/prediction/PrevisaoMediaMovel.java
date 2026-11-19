package com.floodguard.prediction;

import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    public PrevisaoMediaMovel() {
        super(5);
    }

    @Override
    public double prever(List<Double> leituras) {

        validarLeituras(leituras);

        int fim = leituras.size();
        int inicio = fim - 5;

        return calcularMedia(leituras, inicio, fim);
    }
}
