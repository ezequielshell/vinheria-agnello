<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activePage" value="adega" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>${vinho.nome} — Vinheria Agnello</title>
    <%@ include file="/WEB-INF/jspf/head-tags.jspf" %>
</head>
<body data-flash="${sessionScope.flash}">
<c:remove var="flash" scope="session" />

<div class="app-viewport">

    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <c:if test="${not empty vinho}">

        <div class="detail-hero" data-aos="fade-down">
            <img src="${vinho.imagemUrl}" alt="${vinho.nome}">
        </div>

        <div class="px-container mt-16" data-aos="fade-up">
            <div class="row-between" style="align-items: flex-start;">
                <div class="flex-1" style="min-width:0;">
                    <h2 style="font-family:'Playfair Display',serif; font-size:26px; color:var(--wine-700); line-height:1.15;">${vinho.nome}</h2>
                    <p class="featured-card__sub" style="margin-top:6px;">${vinho.origemCompleta} &bull; ${vinho.safra}</p>
                </div>
                <c:choose>
                    <c:when test="${sessionScope.logado}">
                        <c:set var="favAtivo" value="${sessionScope.favoritosIds != null && sessionScope.favoritosIds.contains(vinho.id)}" />
                        <button type="button"
                                class="btn-favorito js-fav ${favAtivo ? 'btn-favorito--ativo' : ''}"
                                data-vinho-id="${vinho.id}"
                                aria-label="${favAtivo ? 'Remover dos favoritos' : 'Adicionar aos favoritos'}">
                            ${favAtivo ? '&#9829;' : '&#9825;'}
                        </button>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login?redirect=detalhe?id=${vinho.id}" class="btn-favorito" title="Entre para favoritar">&#9825;</a>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="badges" style="margin-top:14px;">
                <span class="badge">${vinho.badgeTipo}</span>
                <span class="badge gold">${vinho.uva}</span>
                <c:if test="${vinho.estoque <= 10 && vinho.estoque > 0}">
                    <span class="badge" style="color:#b91c1c;background:#fee2e2;">Últimas ${vinho.estoque} unidades</span>
                </c:if>
                <c:if test="${vinho.estoque == 0}">
                    <span class="badge" style="color:#fff;background:#1f1612;">Esgotado</span>
                </c:if>
            </div>

            <div class="row-between" style="margin-top:16px;">
                <div class="featured-card__price">R$ ${String.format("%.2f", vinho.preco)}<small> /un.</small></div>
            </div>

            <c:choose>
                <c:when test="${sessionScope.logado && vinho.disponivel}">
                    <form action="${pageContext.request.contextPath}/carrinho" method="post" class="js-add-cart" style="margin-top:18px; display:flex; gap:10px; align-items:center;">
                        <input type="hidden" name="acao" value="adicionar">
                        <input type="hidden" name="vinhoId" value="${vinho.id}">
                        <input type="hidden" name="origem" value="detalhe?id=${vinho.id}">
                        <div class="qty">
                            <button type="button" data-step="-1" aria-label="Diminuir">&minus;</button>
                            <input type="number" name="quantidade" value="1" min="1" max="${vinho.estoque}">
                            <button type="button" data-step="1" aria-label="Aumentar">+</button>
                        </div>
                        <button type="submit" class="btn-wine flex-1">Adicionar ao Carrinho</button>
                    </form>
                </c:when>
                <c:when test="${!vinho.disponivel}">
                    <button class="btn-wine btn-block" disabled style="opacity:0.5; cursor:not-allowed; margin-top:18px;">Indisponível</button>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login?redirect=detalhe?id=${vinho.id}"
                       class="btn-wine btn-block" style="margin-top:18px;">Entre para Comprar</a>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="giulio-box" data-aos="fade-up">
            <div class="giulio-header">
                <img src="https://i.pravatar.cc/150?u=giulio" alt="Giulio">
                <div>
                    <h3>A Voz do Giulio</h3>
                    <small>Sommelier &amp; Curador</small>
                </div>
            </div>
            <p class="giulio-text">${vinho.comentarioGiulio}</p>
            <div class="giulio-rating" aria-label="Nota ${vinho.notaGiulio} de 5">
                <c:forEach begin="1" end="${vinho.notaGiulio}">&#9733;</c:forEach>
                <c:forEach begin="${vinho.notaGiulio + 1}" end="5">&#9734;</c:forEach>
            </div>
        </div>

        <div class="tech-section" data-aos="fade-up">
            <h4 class="section-title">A Arte da Criação</h4>
            <div class="tech-grid">
                <div class="tech-card" data-aos="zoom-in" data-aos-delay="0">
                    <span class="tech-label">Maturação</span>
                    <span class="tech-value">${vinho.maturacao}</span>
                </div>
                <div class="tech-card" data-aos="zoom-in" data-aos-delay="80">
                    <span class="tech-label">Potencial</span>
                    <span class="tech-value">${vinho.potencialGuarda}</span>
                </div>
                <div class="tech-card" data-aos="zoom-in" data-aos-delay="160">
                    <span class="tech-label">Acidez</span>
                    <span class="tech-value">${vinho.acidez}</span>
                </div>
                <div class="tech-card" data-aos="zoom-in" data-aos-delay="240">
                    <span class="tech-label">Álcool</span>
                    <span class="tech-value">${vinho.teorAlcoolico}</span>
                </div>
            </div>
            <div class="harmonizacao-box" data-aos="fade-up">
                <h5>Harmonização Sugerida</h5>
                <p>${vinho.harmonizacao}</p>
            </div>
        </div>

        <div class="px-container mb-16" data-aos="fade-up">
            <div style="background: linear-gradient(135deg, var(--wine-900) 0%, var(--wine-600) 100%);
                        border-radius: var(--radius-lg); padding: 22px; color:#fff; box-shadow: var(--shadow-md);">
                <h4 style="font-family:'Playfair Display',serif; font-size:18px; margin-bottom:6px;">Origem: ${vinho.regiao}</h4>
                <p style="font-size:13px; opacity:0.85; line-height:1.6;">
                    ${vinho.pais} &bull; Safra ${vinho.safra} &bull; ${vinho.uva}
                </p>
                <p style="font-size:11px; opacity:0.6; margin-top:10px; text-transform:uppercase; letter-spacing:1.5px;">
                    Mapa interativo na Sprint 3
                </p>
            </div>
        </div>
    </c:if>

    <c:if test="${empty vinho}">
        <div class="empty-state">
            <div class="empty-icon">&#127863;</div>
            <h3>Vinho não encontrado</h3>
            <p>O vinho que você procura não está no nosso catálogo.</p>
            <a href="${pageContext.request.contextPath}/adega" class="btn-wine">Explorar a Adega</a>
        </div>
    </c:if>

    <%@ include file="/WEB-INF/jspf/app-footer.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/page-scripts.jspf" %>
</body>
</html>
