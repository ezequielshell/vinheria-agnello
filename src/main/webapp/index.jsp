<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activePage" value="home" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Vinheria Agnello — Curadoria de vinhos</title>
    <%@ include file="/WEB-INF/jspf/head-tags.jspf" %>
</head>
<body data-flash="${sessionScope.flash}">
<c:remove var="flash" scope="session" />

<div class="app-viewport">

    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <section class="hero" data-aos="fade-up">
        <p class="hero__eyebrow">Curadoria do Giulio</p>
        <h1 class="hero__title">A arte de descobrir <em>grandes vinhos</em></h1>
        <p class="hero__desc">
            <c:choose>
                <c:when test="${sessionScope.logado}">
                    Bem-vindo de volta, ${sessionScope.usuario.primeiroNome}. Sua adega curada espera por você.
                </c:when>
                <c:otherwise>
                    Uma seleção pessoal entre 8 rótulos de produtores reverenciados, com a curadoria do sommelier Giulio.
                </c:otherwise>
            </c:choose>
        </p>
        <div class="hero__stats">
            <div class="hero__stat" data-aos="fade-up" data-aos-delay="100">
                <div class="hero__stat-value">${totalVinhos}</div>
                <div class="hero__stat-label">Rótulos</div>
            </div>
            <div class="hero__stat" data-aos="fade-up" data-aos-delay="200">
                <div class="hero__stat-value">6</div>
                <div class="hero__stat-label">Países</div>
            </div>
            <div class="hero__stat" data-aos="fade-up" data-aos-delay="300">
                <div class="hero__stat-value">★ 4.6</div>
                <div class="hero__stat-label">Nota Giulio</div>
            </div>
        </div>
    </section>

    <div class="search-container" data-aos="fade-up">
        <form action="${pageContext.request.contextPath}/adega" method="get" class="search-box">
            <img src="https://img.icons8.com/?size=100&id=e4NkZ7kWAD7f&format=png&color=999999" alt="Buscar" class="search-icon">
            <input type="text" name="busca" placeholder="Buscar região, uva ou safra...">
            <button type="submit" class="btn-filter" aria-label="Buscar">
                <img src="https://img.icons8.com/?size=100&id=e4NkZ7kWAD7f&format=png&color=FFFFFF" alt="">
            </button>
        </form>
    </div>

    <c:if test="${not empty vinhoDestaque}">
        <div class="section-divider" data-aos="fade-right">
            <h3>O Destaque da Semana</h3>
            <div class="line"></div>
        </div>

        <div class="featured-card" data-aos="zoom-in-up">
            <div class="featured-card__top">
                <img src="${vinhoDestaque.imagemUrl}" alt="${vinhoDestaque.nome}" class="featured-card__img">
                <div class="featured-card__info">
                    <div class="row-between">
                        <h2>${vinhoDestaque.nome}</h2>
                        <c:choose>
                            <c:when test="${sessionScope.logado}">
                                <c:set var="favAtivo" value="${sessionScope.favoritosIds != null && sessionScope.favoritosIds.contains(vinhoDestaque.id)}" />
                                <button type="button"
                                        class="btn-favorito js-fav ${favAtivo ? 'btn-favorito--ativo' : ''}"
                                        data-vinho-id="${vinhoDestaque.id}"
                                        aria-label="${favAtivo ? 'Remover dos favoritos' : 'Adicionar aos favoritos'}">
                                    ${favAtivo ? '&#9829;' : '&#9825;'}
                                </button>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/login?redirect=home" class="btn-favorito" title="Entre para favoritar">&#9825;</a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <p class="featured-card__sub">${vinhoDestaque.origemCompleta} &bull; ${vinhoDestaque.safra}</p>
                    <div class="badges">
                        <span class="badge">${vinhoDestaque.badgeTipo}</span>
                        <span class="badge gold">${vinhoDestaque.uva}</span>
                    </div>
                    <div class="featured-card__price">R$ ${String.format("%.2f", vinhoDestaque.preco)}<small> /un.</small></div>
                </div>
            </div>
            <div class="featured-card__actions">
                <a href="${pageContext.request.contextPath}/detalhe?id=${vinhoDestaque.id}" class="btn-wine flex-1">Ver Detalhes</a>
                <c:choose>
                    <c:when test="${sessionScope.logado && vinhoDestaque.disponivel}">
                        <form action="${pageContext.request.contextPath}/carrinho" method="post" class="flex-1 js-add-cart" style="margin:0;">
                            <input type="hidden" name="acao" value="adicionar">
                            <input type="hidden" name="vinhoId" value="${vinhoDestaque.id}">
                            <input type="hidden" name="origem" value="home">
                            <button type="submit" class="btn-wine-outline btn-block">+ Carrinho</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login?redirect=home" class="btn-wine-outline flex-1">+ Carrinho</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="giulio-box" data-aos="fade-up">
            <div class="giulio-header">
                <img src="https://i.pravatar.cc/150?u=giulio" alt="Giulio">
                <div>
                    <h3>A Voz do Giulio</h3>
                    <small>Sommelier &amp; Curador</small>
                </div>
            </div>
            <p class="giulio-text">${vinhoDestaque.comentarioGiulio}</p>
            <div class="giulio-rating" aria-label="Nota ${vinhoDestaque.notaGiulio} de 5">
                <c:forEach begin="1" end="${vinhoDestaque.notaGiulio}">&#9733;</c:forEach>
                <c:forEach begin="${vinhoDestaque.notaGiulio + 1}" end="5">&#9734;</c:forEach>
            </div>
        </div>
    </c:if>

    <div class="section-divider" data-aos="fade-right">
        <h3>Explore a Adega</h3>
        <div class="line"></div>
        <a href="${pageContext.request.contextPath}/adega">Ver todos</a>
    </div>

    <div class="wine-list">
        <c:forEach var="v" items="${vinhos}" varStatus="loop" end="3">
            <a href="${pageContext.request.contextPath}/detalhe?id=${v.id}" class="wine-list-item"
               data-aos="fade-up" data-aos-delay="${loop.index * 80}">
                <img src="${v.imagemUrl}" alt="${v.nome}" class="wine-thumb">
                <div class="wine-info">
                    <div class="wine-name">${v.nome}</div>
                    <div class="wine-origin">${v.origemCompleta} &bull; ${v.safra}</div>
                    <div class="wine-meta">
                        <span class="badge">${v.badgeTipo}</span>
                        <span class="badge gold">${v.uva}</span>
                    </div>
                </div>
                <div class="wine-list-price">R$ ${String.format("%.0f", v.preco)}</div>
            </a>
        </c:forEach>
    </div>

    <%@ include file="/WEB-INF/jspf/app-footer.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/page-scripts.jspf" %>
</body>
</html>
