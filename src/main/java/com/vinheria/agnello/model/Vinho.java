package com.vinheria.agnello.model;

import java.io.Serializable;

/**
 * JavaBean que representa um vinho no catálogo da Vinheria Agnello.
 * Contém dados de identificação, origem, características técnicas
 * e a curadoria personalizada do Giulio.
 */
public class Vinho implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String regiao;
    private String pais;
    private int safra;
    private String uva;
    private String tipo;
    private String imagemUrl;
    private double preco;

    // Dados técnicos
    private String maturacao;
    private String potencialGuarda;
    private String acidez;
    private String teorAlcoolico;
    private String harmonizacao;

    // Curadoria do Giulio
    private String comentarioGiulio;
    private int notaGiulio; // 1 a 5

    private int estoque = 50;
    private boolean favorito;

    public Vinho() {}

    public Vinho(int id, String nome, String regiao, String pais, int safra,
                 String uva, String tipo, String imagemUrl, double preco,
                 String maturacao, String potencialGuarda, String acidez,
                 String teorAlcoolico, String harmonizacao,
                 String comentarioGiulio, int notaGiulio) {
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.pais = pais;
        this.safra = safra;
        this.uva = uva;
        this.tipo = tipo;
        this.imagemUrl = imagemUrl;
        this.preco = preco;
        this.maturacao = maturacao;
        this.potencialGuarda = potencialGuarda;
        this.acidez = acidez;
        this.teorAlcoolico = teorAlcoolico;
        this.harmonizacao = harmonizacao;
        this.comentarioGiulio = comentarioGiulio;
        this.notaGiulio = notaGiulio;
        this.favorito = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRegiao() { return regiao; }
    public void setRegiao(String regiao) { this.regiao = regiao; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public int getSafra() { return safra; }
    public void setSafra(int safra) { this.safra = safra; }

    public String getUva() { return uva; }
    public void setUva(String uva) { this.uva = uva; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public String getMaturacao() { return maturacao; }
    public void setMaturacao(String maturacao) { this.maturacao = maturacao; }

    public String getPotencialGuarda() { return potencialGuarda; }
    public void setPotencialGuarda(String potencialGuarda) { this.potencialGuarda = potencialGuarda; }

    public String getAcidez() { return acidez; }
    public void setAcidez(String acidez) { this.acidez = acidez; }

    public String getTeorAlcoolico() { return teorAlcoolico; }
    public void setTeorAlcoolico(String teorAlcoolico) { this.teorAlcoolico = teorAlcoolico; }

    public String getHarmonizacao() { return harmonizacao; }
    public void setHarmonizacao(String harmonizacao) { this.harmonizacao = harmonizacao; }

    public String getComentarioGiulio() { return comentarioGiulio; }
    public void setComentarioGiulio(String comentarioGiulio) { this.comentarioGiulio = comentarioGiulio; }

    public int getNotaGiulio() { return notaGiulio; }
    public void setNotaGiulio(int notaGiulio) { this.notaGiulio = notaGiulio; }

    public int getEstoque() { return estoque; }
    public void setEstoque(int estoque) { this.estoque = estoque; }

    public boolean isDisponivel() { return estoque > 0; }

    public boolean isFavorito() { return favorito; }
    public void setFavorito(boolean favorito) { this.favorito = favorito; }

    public String getOrigemCompleta() {
        return regiao + ", " + pais;
    }

    public String getBadgeTipo() {
        return tipo != null ? tipo : "Tinto Seco";
    }
}
