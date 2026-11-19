package com.floodguard.model;

import com.floodguard.exception.DadosInvalidosException;

public abstract class ComponenteTerritorial {

    private Long id;
    private String nome;
    private String regiao;
    private String status;

    public ComponenteTerritorial(
            Long id,
            String nome,
            String regiao
    ) {
        this.id = id;
        setNome(nome);
        setRegiao(regiao);
        this.status = "ATIVO";
    }

    public abstract String verificarStatus();

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public String getStatus() {
        return status;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException(
                    "O nome nao pode estar vazio."
            );
        }

        this.nome = nome;
    }

    public void setRegiao(String regiao) {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException(
                    "A regiao nao pode estar vazia."
            );
        }

        this.regiao = regiao;
    }

    public void setStatus(String status) {
        if (!"ATIVO".equals(status)
                && !"INATIVO".equals(status)) {
            throw new DadosInvalidosException(
                    "Status deve ser ATIVO ou INATIVO."
            );
        }

        this.status = status;
    }
}
