<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activePage" value="carrinho" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Checkout — Vinheria Agnello</title>
    <%@ include file="/WEB-INF/jspf/head-tags.jspf" %>
</head>
<body data-flash="${sessionScope.flash}">
<c:remove var="flash" scope="session" />

<div class="app-viewport">

    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <div class="page-title-section" data-aos="fade-right">
        <h2>Finalizar Pedido</h2>
        <p>Revise endereço, pagamento e confirme.</p>
    </div>

    <form action="${pageContext.request.contextPath}/checkout" method="post" class="form" style="padding: 0 18px;">

        <%-- ENDEREÇO --%>
        <div class="cart-summary" data-aos="fade-up" style="margin: 0 0 16px;">
            <h4>Endereço de entrega</h4>

            <c:forEach var="e" items="${enderecos}" varStatus="loop">
                <label style="display:flex; gap:12px; padding:10px 0; border-bottom: 1px dashed var(--border); cursor:pointer;">
                    <input type="radio" name="enderecoId" value="${e.id}" ${loop.first && !empty enderecos ? 'checked' : ''}>
                    <div>
                        <strong>${e.logradouro}, ${e.numero}</strong>
                        <c:if test="${not empty e.complemento}"> - ${e.complemento}</c:if>
                        <div style="font-size:12px; color:var(--ink-500);">${e.bairro}, ${e.cidade}/${e.uf} &bull; CEP ${e.cep}
                            <c:if test="${e.principal}"><span class="badge gold" style="margin-left:8px;">Principal</span></c:if>
                        </div>
                    </div>
                </label>
            </c:forEach>

            <label style="display:flex; gap:12px; padding:10px 0; cursor:pointer;">
                <input type="radio" name="enderecoId" value="novo" ${empty enderecos ? 'checked' : ''}>
                <strong>+ Usar novo endereço</strong>
            </label>

            <input type="hidden" name="novoEndereco" id="novo-endereco-flag" value="">

            <div id="novo-endereco-block" style="display:none; margin-top: 12px; padding-top: 14px; border-top: 1px dashed var(--border);">
                <div class="form-row">
                    <div class="form-group">
                        <label for="cep">CEP</label>
                        <input type="text" id="cep" name="cep" maxlength="9" placeholder="01234-567" data-required="true">
                    </div>
                    <div class="form-group">
                        <label for="uf">UF</label>
                        <input type="text" id="uf" name="uf" maxlength="2" placeholder="SP" data-required="true">
                    </div>
                </div>
                <div class="form-group">
                    <label for="logradouro">Logradouro</label>
                    <input type="text" id="logradouro" name="logradouro" data-required="true">
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="numero">Número</label>
                        <input type="text" id="numero" name="numero" data-required="true">
                    </div>
                    <div class="form-group">
                        <label for="complemento">Complemento</label>
                        <input type="text" id="complemento" name="complemento">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="bairro">Bairro</label>
                        <input type="text" id="bairro" name="bairro" data-required="true">
                    </div>
                    <div class="form-group">
                        <label for="cidade">Cidade</label>
                        <input type="text" id="cidade" name="cidade" data-required="true">
                    </div>
                </div>
                <label style="display:flex; align-items:center; gap:8px; margin-top:4px; font-size:13px;">
                    <input type="checkbox" name="principal"> Definir como endereço principal
                </label>
            </div>
        </div>

        <%-- PAGAMENTO --%>
        <div class="cart-summary" data-aos="fade-up" style="margin: 0 0 16px;">
            <h4>Forma de pagamento</h4>
            <label style="display:flex; gap:12px; padding:10px 0; border-bottom:1px dashed var(--border); cursor:pointer;">
                <input type="radio" name="formaPagamento" value="PIX" checked>
                <div>
                    <strong>PIX</strong>
                    <div style="font-size:12px; color:var(--ink-500);">5% de desconto à vista (simulado).</div>
                </div>
            </label>
            <label style="display:flex; gap:12px; padding:10px 0; border-bottom:1px dashed var(--border); cursor:pointer;">
                <input type="radio" name="formaPagamento" value="CARTAO_CREDITO">
                <div>
                    <strong>Cartão de crédito</strong>
                    <div style="font-size:12px; color:var(--ink-500);">Em até 6× sem juros.</div>
                </div>
            </label>
            <label style="display:flex; gap:12px; padding:10px 0; cursor:pointer;">
                <input type="radio" name="formaPagamento" value="BOLETO">
                <div>
                    <strong>Boleto bancário</strong>
                    <div style="font-size:12px; color:var(--ink-500);">Vencimento em 3 dias úteis.</div>
                </div>
            </label>
        </div>

        <%-- RESUMO --%>
        <div class="cart-summary" data-aos="fade-up" style="margin: 0 0 18px;">
            <h4>Resumo</h4>
            <c:forEach var="i" items="${itens}">
                <div class="summary-row">
                    <span>${i.quantidade}× ${i.vinho.nome}</span>
                    <span>R$ ${String.format("%.2f", i.subtotal)}</span>
                </div>
            </c:forEach>
            <div class="summary-row">
                <span>Subtotal</span>
                <span>R$ ${String.format("%.2f", subtotal)}</span>
            </div>
            <div class="summary-row">
                <span>Frete</span>
                <c:choose>
                    <c:when test="${frete == 0}"><span class="summary-frete-gratis">Grátis</span></c:when>
                    <c:otherwise><span>R$ ${String.format("%.2f", frete)}</span></c:otherwise>
                </c:choose>
            </div>
            <div class="summary-row total">
                <span>Total</span>
                <span>R$ ${String.format("%.2f", total)}</span>
            </div>
            <button type="submit" class="btn-wine btn-block" style="margin-top:14px;">Confirmar Pedido</button>
            <a href="${pageContext.request.contextPath}/carrinho" class="btn-ghost btn-block" style="margin-top:8px;">Voltar ao carrinho</a>
        </div>
    </form>

    <%@ include file="/WEB-INF/jspf/app-footer.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/page-scripts.jspf" %>
</body>
</html>
