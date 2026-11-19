package com.floodguard.model;

import com.floodguard.exception.DadosInvalidosException;

import java.util.EnumMap;
import java.util.Map;

public class DadosTerritorio {

    private final Map<TipoSensor, double[]> limites =
            new EnumMap<>(TipoSensor.class);

    private final String codigoAcesso;

    public DadosTerritorio(String codigoAcesso) {

        if (codigoAcesso == null || codigoAcesso.isBlank()) {
            throw new DadosInvalidosException(
                    "Codigo de acesso obrigatorio."
            );
        }

        this.codigoAcesso = codigoAcesso;

        limites.put(
                TipoSensor.PLUVIOMETRICO,
                new double[]{20, 50, 80}
        );

        limites.put(
                TipoSensor.NIVEL_AGUA,
                new double[]{40, 70, 100}
        );

        limites.put(
                TipoSensor.UMIDADE_SOLO,
                new double[]{60, 80, 95}
        );
    }

    public NivelAlerta classificarRisco(
            TipoSensor tipo,
            double valor
    ) {
        validarValor(tipo, valor);

        double[] faixa = obterFaixa(tipo);

        if (valor >= faixa[2]) {
            return NivelAlerta.CRITICO;
        }

        if (valor >= faixa[1]) {
            return NivelAlerta.ALERTA;
        }

        if (valor >= faixa[0]) {
            return NivelAlerta.ATENCAO;
        }

        return NivelAlerta.NORMAL;
    }

    public double consultarLimite(
            TipoSensor tipo,
            NivelAlerta nivel
    ) {
        return obterFaixa(tipo)[obterIndice(nivel)];
    }

    public void alterarLimite(
            TipoSensor tipo,
            NivelAlerta nivel,
            double novoValor,
            String codigoInformado
    ) {
        if (codigoInformado == null
                || !codigoAcesso.equals(codigoInformado)) {
            throw new DadosInvalidosException(
                    "Codigo de acesso incorreto."
            );
        }

        validarValor(tipo, novoValor);

        int indice = obterIndice(nivel);
        double[] novosLimites = obterFaixa(tipo).clone();

        novosLimites[indice] = novoValor;

        if (!(novosLimites[0] < novosLimites[1]
                && novosLimites[1] < novosLimites[2])) {
            throw new DadosInvalidosException(
                    "Limites devem estar em ordem crescente."
            );
        }

        limites.put(tipo, novosLimites);
    }

    public void validarValor(
            TipoSensor tipo,
            double valor
    ) {
        if (tipo == null) {
            throw new DadosInvalidosException(
                    "Tipo de sensor obrigatorio."
            );
        }

        double maximo = switch (tipo) {
            case PLUVIOMETRICO -> 120;
            case NIVEL_AGUA -> 150;
            case UMIDADE_SOLO -> 100;
        };

        if (!Double.isFinite(valor)
                || valor < 0
                || valor > maximo) {
            throw new DadosInvalidosException(
                    "Valor invalido para o sensor " + tipo
            );
        }
    }

    private double[] obterFaixa(TipoSensor tipo) {
        double[] faixa = limites.get(tipo);

        if (faixa == null) {
            throw new DadosInvalidosException(
                    "Tipo de sensor invalido."
            );
        }

        return faixa;
    }

    private int obterIndice(NivelAlerta nivel) {

        if (nivel == null) {
            throw new DadosInvalidosException(
                    "Nivel de alerta obrigatorio."
            );
        }

        return switch (nivel) {
            case ATENCAO -> 0;
            case ALERTA -> 1;
            case CRITICO -> 2;
            case NORMAL -> throw new DadosInvalidosException(
                    "NORMAL nao possui limite configuravel."
            );
        };
    }
}
