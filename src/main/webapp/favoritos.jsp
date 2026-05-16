<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activePage" value="favoritos" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Favoritos — Vinheria Agnello</title>
    <%@ include file="/WEB-INF/jspf/head-tags.jspf" %>
</head>
<body data-flash="${sessionScope.flash}">
<c:remove var="flash" scope="session" />

<div class="app-viewport">

    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <div class="page-title-section" data-aos="fade-right">
        <h2>Meus Favoritos</h2>
        <p>
            <c:choose>
                <c:when test="${sessionScope.logado}">Os rótulos que você marcou para degustar.</c:when>
                <c:otherwise>Entre para salvar seus vinhos favoritos.</c:otherwise>
            </c:choose>
        </p>
    </div>

    <c:choose>
        <c:when test="${!sessionScope.logado}">
            <div class="empty-state" data-aos="fade-up">
                <div class="empty-icon">&#128274;</div>
                <h3>Acesso necessário</h3>
                <p>Faça login para acompanhar seus vinhos favoritos em qualquer dispositivo.</p>
                <a href="${pageContext.request.contextPath}/login?redirect=favoritos" class="btn-wine">Fazer Login</a>
            </div>
        </c:when>
        <c:when test="${empty favoritos}">
            <div class="empty-state" data-aos="fade-up">
                <div class="empty-icon">&#9825;</div>
                <h3>Nenhum favorito ainda</h3>
                <p>Explore a adega e marque os vinhos que mais te interessam.</p>
                <a href="${pageContext.request.contextPath}/adega" class="btn-wine">Explorar a Adega</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="wine-list" style="padding-top: 8px;" data-fav-list>
                <c:forEach var="v" items="${favoritos}" varStatus="loop">
                    <div class="wine-list-item" style="position:relative;"
                         data-fav-card data-aos="fade-up" data-aos-delay="${(loop.index % 4) * 70}">
                        <a href="${pageContext.request.contextPath}/detalhe?id=${v.id}"
                           style="display:flex; align-items:center; gap:14px; flex:1; color:inherit;">
                            <img src="${v.imagemUrl}" alt="${v.nome}" class="wine-thumb">
                            <div class="wine-info">
                                <div class="wine-name">${v.nome}</div>
                                <div class="wine-origin">${v.origemCompleta} &bull; ${v.safra}</div>
                                <div class="wine-meta">
                                    <span class="badge">${v.badgeTipo}</span>
                                    <span class="badge gold">${v.uva}</span>
                                </div>
                            </div>
                        </a>
                        <button type="button"
                                class="btn-favorito btn-favorito--ativo js-fav"
                                data-vinho-id="${v.id}"
                                aria-label="Remover dos favoritos">&#9829;</button>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

    <%@ include file="/WEB-INF/jspf/app-footer.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/page-scripts.jspf" %>
</body>
</html>
