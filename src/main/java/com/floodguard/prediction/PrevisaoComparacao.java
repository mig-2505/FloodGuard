package com.floodguard.prediction;

import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    public PrevisaoComparacao() {
        super(10);
    }

    @Override
    public double prever(List<Double> leituras) {

        validarLeituras(leituras);

        int fim = leituras.size();
        int meio = fim - 5;
        int inicio = fim - 10;

        double mediaAnterior =
                calcularMedia(leituras, inicio, meio);

        double mediaRecente =
                calcularMedia(leituras, meio, fim);

        double variacao = mediaRecente - mediaAnterior;

        return Math.max(0, mediaRecente + variacao);
    }
}
