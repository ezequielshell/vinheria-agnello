package com.vinheria.agnello.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ItemPedido implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private long pedidoId;
    private long vinhoId;
    private String vinhoNome;
    private String vinhoImagem;
    private int quantidade;
    private BigDecimal precoUnitario;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getPedidoId() { return pedidoId; }
    public void setPedidoId(long pedidoId) { this.pedidoId = pedidoId; }

    public long getVinhoId() { return vinhoId; }
    public void setVinhoId(long vinhoId) { this.vinhoId = vinhoId; }

    public String getVinhoNome() { return vinhoNome; }
    public void setVinhoNome(String vinhoNome) { this.vinhoNome = vinhoNome; }

    public String getVinhoImagem() { return vinhoImagem; }
    public void setVinhoImagem(String vinhoImagem) { this.vinhoImagem = vinhoImagem; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
