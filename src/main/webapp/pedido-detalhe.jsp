<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activePage" value="" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Pedido #${pedido.id} — Vinheria Agnello</title>
    <%@ include file="/WEB-INF/jspf/head-tags.jspf" %>
</head>
<body data-flash="${sessionScope.flash}">
<c:remove var="flash" scope="session" />

<div class="app-viewport">

    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <div style="text-align:center; padding: 32px 18px 8px;" data-aos="zoom-in">
        <div style="font-size:64px;">&#127863;</div>
        <h2 style="font-family:'Playfair Display',serif; color:var(--wine-700); font-size:24px; margin-top:8px;">
            Pedido confirmado!
        </h2>
        <p style="color:var(--ink-500); font-size:13px;">Obrigado pela confiança. Logo o Giulio enviará as instruções de entrega.</p>
    </div>

    <div class="cart-summary" data-aos="fade-up">
        <h4>Pedido #${pedido.id}</h4>
        <div class="summary-row"><span>Status</span><span>${pedido.status}</span></div>
        <div class="summary-row">
            <span>Data</span>
            <c:if test="${not empty pedido.criadoEm}">
                <span>${pedido.criadoEmFormatado}</span>
            </c:if>
        </div>
        <div class="summary-row"><span>Pagamento</span><span>${pedido.formaPagamento}</span></div>
        <p style="font-size:12px; color:var(--ink-500); margin-top:12px;">
            <strong>Entrega:</strong> ${pedido.enderecoEntrega}
        </p>
    </div>

    <div class="cart-summary" data-aos="fade-up">
        <h4>Itens</h4>
        <c:forEach var="i" items="${pedido.itens}">
            <div class="summary-row" style="align-items:center;">
                <span style="display:flex; gap:10px; align-items:center;">
                    <img src="${i.vinhoImagem}" alt="" style="width:36px; height:50px; object-fit:cover; border-radius:6px;">
                    ${i.quantidade}× ${i.vinhoNome}
                </span>
                <span>R$ ${String.format("%.2f", i.subtotal)}</span>
            </div>
        </c:forEach>
        <div class="summary-row total">
            <span>Total</span>
            <span>R$ ${String.format("%.2f", pedido.total)}</span>
        </div>
    </div>

    <div style="padding: 0 18px 24px;" data-aos="fade-up">
        <a href="${pageContext.request.contextPath}/pedidos" class="btn-wine-outline btn-block">Meus pedidos</a>
        <a href="${pageContext.request.contextPath}/adega" class="btn-wine btn-block" style="margin-top:10px;">Continuar comprando</a>
    </div>

    <%@ include file="/WEB-INF/jspf/app-footer.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/page-scripts.jspf" %>
</body>
</html>
