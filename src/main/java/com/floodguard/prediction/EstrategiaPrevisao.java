package com.floodguard.prediction;

import com.floodguard.exception.DadosInvalidosException;

import java.util.List;

public abstract class EstrategiaPrevisao {

    private final int quantidadeMinima;

    protected EstrategiaPrevisao(int quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public abstract double prever(List<Double> leituras);

    protected void validarLeituras(List<Double> leituras) {

        if (leituras == null
                || leituras.size() < quantidadeMinima) {
            throw new DadosInvalidosException(
                    "Historico insuficiente para previsao."
            );
        }

        for (Double valor : leituras) {
            if (valor == null
                    || !Double.isFinite(valor)
                    || valor < 0) {
                throw new DadosInvalidosException(
                        "Historico contem medicao invalida."
                );
            }
        }
    }

    protected double calcularMedia(
            List<Double> leituras,
            int inicio,
            int fim
    ) {
        double soma = 0;

        for (int i = inicio; i < fim; i++) {
            soma += leituras.get(i);
        }

        return soma / (fim - inicio);
    }
}
