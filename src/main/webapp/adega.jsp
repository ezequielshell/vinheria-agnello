<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activePage" value="adega" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Adega — Vinheria Agnello</title>
    <%@ include file="/WEB-INF/jspf/head-tags.jspf" %>
</head>
<body data-flash="${sessionScope.flash}">
<c:remove var="flash" scope="session" />

<div class="app-viewport">

    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <div class="page-title-section" data-aos="fade-right">
        <h2>
            <c:choose>
                <c:when test="${not empty termoBusca}">Resultados para "${termoBusca}"</c:when>
                <c:when test="${not empty filtroTipo}">Vinhos ${filtroTipo}s</c:when>
                <c:otherwise>Nossa Adega</c:otherwise>
            </c:choose>
        </h2>
        <p>${totalResultados} vinho${totalResultados != 1 ? 's' : ''} encontrado${totalResultados != 1 ? 's' : ''}</p>
    </div>

    <div class="search-container">
        <form action="${pageContext.request.contextPath}/adega" method="get" class="search-box">
            <img src="https://img.icons8.com/?size=100&id=e4NkZ7kWAD7f&format=png&color=999999" alt="Buscar" class="search-icon">
            <input type="text" name="busca" placeholder="Buscar região, uva ou safra..."
                   value="${termoBusca != null ? termoBusca : ''}">
            <button type="submit" class="btn-filter" aria-label="Buscar">
                <img src="https://img.icons8.com/?size=100&id=e4NkZ7kWAD7f&format=png&color=FFFFFF" alt="">
            </button>
        </form>
    </div>

    <div class="filter-chips">
        <a href="${pageContext.request.contextPath}/adega"
           class="chip ${empty filtroTipo && empty termoBusca ? 'active' : ''}">Todos</a>
        <a href="${pageContext.request.contextPath}/adega?tipo=Tinto"
           class="chip ${filtroTipo == 'Tinto' ? 'active' : ''}">Tintos</a>
        <a href="${pageContext.request.contextPath}/adega?tipo=Branco"
           class="chip ${filtroTipo == 'Branco' ? 'active' : ''}">Brancos</a>
        <a href="${pageContext.request.contextPath}/adega?tipo=Rosé"
           class="chip ${filtroTipo == 'Rosé' ? 'active' : ''}">Rosés</a>
    </div>

    <div class="wine-list" style="padding-top: 12px;">
        <c:choose>
            <c:when test="${empty vinhos}">
                <div class="empty-state" data-aos="fade-up">
                    <div class="empty-icon">&#127863;</div>
                    <h3>Nenhum vinho encontrado</h3>
                    <p>Tente outro termo ou explore nossos filtros.</p>
                    <a href="${pageContext.request.contextPath}/adega" class="btn-wine">Ver todos os vinhos</a>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="v" items="${vinhos}" varStatus="loop">
                    <a href="${pageContext.request.contextPath}/detalhe?id=${v.id}" class="wine-list-item"
                       data-aos="fade-up" data-aos-delay="${(loop.index % 4) * 70}">
                        <img src="${v.imagemUrl}" alt="${v.nome}" class="wine-thumb">
                        <div class="wine-info">
                            <div class="wine-name">${v.nome}</div>
                            <div class="wine-origin">${v.origemCompleta} &bull; ${v.safra}</div>
                            <div class="wine-meta">
                                <span class="badge">${v.badgeTipo}</span>
                                <span class="badge gold">${v.uva}</span>
                                <c:if test="${v.estoque <= 10}">
                                    <span class="badge" style="color:#b91c1c;background:#fee2e2;">Últimas ${v.estoque}</span>
                                </c:if>
                            </div>
                        </div>
                        <div class="wine-list-price">R$ ${String.format("%.0f", v.preco)}</div>
                    </a>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <%@ include file="/WEB-INF/jspf/app-footer.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/page-scripts.jspf" %>
</body>
</html>
