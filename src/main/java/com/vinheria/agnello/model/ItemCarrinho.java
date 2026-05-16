package com.vinheria.agnello.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ItemCarrinho implements Serializable {

    private static final long serialVersionUID = 1L;

    private Vinho vinho;
    private int quantidade;

    public ItemCarrinho() {}

    public ItemCarrinho(Vinho vinho, int quantidade) {
        this.vinho = vinho;
        this.quantidade = quantidade;
    }

    public Vinho getVinho() { return vinho; }
    public void setVinho(Vinho vinho) { this.vinho = vinho; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getSubtotal() {
        return BigDecimal.valueOf(vinho.getPreco()).multiply(BigDecimal.valueOf(quantidade));
    }
}
