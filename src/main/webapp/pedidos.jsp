<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activePage" value="" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Meus pedidos — Vinheria Agnello</title>
    <%@ include file="/WEB-INF/jspf/head-tags.jspf" %>
</head>
<body data-flash="${sessionScope.flash}">
<c:remove var="flash" scope="session" />

<div class="app-viewport">

    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <div class="page-title-section" data-aos="fade-right">
        <h2>Meus Pedidos</h2>
        <p>Histórico das suas compras.</p>
    </div>

    <c:choose>
        <c:when test="${empty pedidos}">
            <div class="empty-state" data-aos="fade-up">
                <div class="empty-icon">&#128190;</div>
                <h3>Nenhum pedido ainda</h3>
                <p>Quando você finalizar uma compra, ela aparecerá aqui.</p>
                <a href="${pageContext.request.contextPath}/adega" class="btn-wine">Explorar a Adega</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="wine-list" style="padding-top:8px;">
                <c:forEach var="p" items="${pedidos}" varStatus="loop">
                    <a href="${pageContext.request.contextPath}/pedido?id=${p.id}" class="wine-list-item"
                       data-aos="fade-up" data-aos-delay="${loop.index * 60}">
                        <div style="width:56px; height:56px; border-radius:14px;
                                    background: linear-gradient(135deg, var(--wine-700), var(--wine-500));
                                    display:flex; align-items:center; justify-content:center; color:#fff;
                                    font-family:'Playfair Display',serif; font-size:18px;">
                            #${p.id}
                        </div>
                        <div class="wine-info">
                            <div class="wine-name">${p.criadoEmFormatadoLongo}</div>
                            <div class="wine-origin">${p.status} &bull; ${p.formaPagamento}</div>
                        </div>
                        <div class="wine-list-price">R$ ${String.format("%.2f", p.total)}</div>
                    </a>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

    <%@ include file="/WEB-INF/jspf/app-footer.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/page-scripts.jspf" %>
</body>
</html>
