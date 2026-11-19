
package com.floodguard.model;

public class RegiaoMonitorada extends ComponenteTerritorial {

    public RegiaoMonitorada(
            Long id,
            String nome,
            String regiao
    ) {
        super(id, nome, regiao);
    }

    @Override
    public String verificarStatus() {
        return "Regiao: " + getRegiao()
                + " | Nome: " + getNome()
                + " | Status: " + getStatus();
    }
}
