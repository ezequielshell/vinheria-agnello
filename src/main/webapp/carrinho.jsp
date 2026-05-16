<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activePage" value="carrinho" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Carrinho — Vinheria Agnello</title>
    <%@ include file="/WEB-INF/jspf/head-tags.jspf" %>
</head>
<body data-flash="${sessionScope.flash}">
<c:remove var="flash" scope="session" />

<div class="app-viewport">

    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <div class="page-title-section" data-aos="fade-right">
        <h2>Meu Carrinho</h2>
        <p>
            <c:choose>
                <c:when test="${empty itens}">Sua adega ainda está vazia.</c:when>
                <c:otherwise>${itens.size()} rótulo${itens.size() != 1 ? 's' : ''} selecionado${itens.size() != 1 ? 's' : ''}.</c:otherwise>
            </c:choose>
        </p>
    </div>

    <c:choose>
        <c:when test="${empty itens}">
            <div class="empty-state" data-aos="fade-up">
                <div class="empty-icon">&#128722;</div>
                <h3>Carrinho vazio</h3>
                <p>Explore a adega e adicione os vinhos que vão para sua mesa.</p>
                <a href="${pageContext.request.contextPath}/adega" class="btn-wine">Explorar a Adega</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="cart-list">
                <c:forEach var="i" items="${itens}" varStatus="loop">
                    <div class="cart-item" data-aos="fade-up" data-aos-delay="${loop.index * 60}">
                        <img src="${i.vinho.imagemUrl}" alt="${i.vinho.nome}">
                        <div class="cart-item__info">
                            <div class="cart-item__name">${i.vinho.nome}</div>
                            <div class="cart-item__meta">${i.vinho.origemCompleta} &bull; ${i.vinho.safra}</div>
                            <div class="cart-item__bottom">
                                <form action="${pageContext.request.contextPath}/carrinho" method="post"
                                      style="margin:0; display:flex; align-items:center; gap:8px;">
                                    <input type="hidden" name="acao" value="atualizar">
                                    <input type="hidden" name="vinhoId" value="${i.vinho.id}">
                                    <div class="qty">
                                        <button type="button" data-step="-1" aria-label="Diminuir">&minus;</button>
                                        <input type="number" name="quantidade" value="${i.quantidade}" min="1" max="${i.vinho.estoque}" onchange="this.form.submit()">
                                        <button type="button" data-step="1" aria-label="Aumentar">+</button>
                                    </div>
                                </form>
                                <div class="cart-item__price">
                                    R$ ${String.format("%.2f", i.subtotal)}
                                    <small>${i.quantidade} × R$ ${String.format("%.2f", i.vinho.preco)}</small>
                                </div>
                            </div>
                        </div>
                        <form action="${pageContext.request.contextPath}/carrinho" method="post" style="margin:0;">
                            <input type="hidden" name="acao" value="remover">
                            <input type="hidden" name="vinhoId" value="${i.vinho.id}">
                            <button type="submit" class="cart-remove" aria-label="Remover">&times;</button>
                        </form>
                    </div>
                </c:forEach>
            </div>

            <div class="cart-summary" data-aos="fade-up">
                <h4>Resumo do pedido</h4>
                <div class="summary-row">
                    <span>Subtotal</span>
                    <span>R$ ${String.format("%.2f", subtotal)}</span>
                </div>
                <div class="summary-row">
                    <span>Frete</span>
                    <c:choose>
                        <c:when test="${frete == 0}">
                            <span class="summary-frete-gratis">Grátis</span>
                        </c:when>
                        <c:otherwise>
                            <span>R$ ${String.format("%.2f", frete)}</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <c:if test="${frete > 0}">
                    <p class="form-helper">Acima de R$ 500,00 o frete é por nossa conta.</p>
                </c:if>
                <div class="summary-row total">
                    <span>Total</span>
                    <span>R$ ${String.format("%.2f", total)}</span>
                </div>
                <a href="${pageContext.request.contextPath}/checkout" class="btn-wine btn-block" style="margin-top:14px;">Finalizar Pedido</a>
                <a href="${pageContext.request.contextPath}/adega" class="btn-ghost btn-block" style="margin-top:8px;">Continuar comprando</a>
            </div>
        </c:otherwise>
    </c:choose>

    <%@ include file="/WEB-INF/jspf/app-footer.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/page-scripts.jspf" %>
</body>
</html>
