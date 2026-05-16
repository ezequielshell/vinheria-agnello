package com.vinheria.agnello.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private long usuarioId;
    private LocalDateTime criadoEm;
    private String status;
    private BigDecimal total;
    private String formaPagamento;
    private String enderecoEntrega;
    private List<ItemPedido> itens = new ArrayList<>();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    /** Para JSP — fmt:formatDate não aceita LocalDateTime. */
    public String getCriadoEmFormatado() {
        if (criadoEm == null) return "";
        return criadoEm.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getCriadoEmFormatadoLongo() {
        if (criadoEm == null) return "";
        return criadoEm.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR")));
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }

    public int getQuantidadeTotal() {
        return itens.stream().mapToInt(ItemPedido::getQuantidade).sum();
    }
}
