<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activePage" value="" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Entrar — Vinheria Agnello</title>
    <%@ include file="/WEB-INF/jspf/head-tags.jspf" %>
</head>
<body data-flash="${sessionScope.flash}">
<c:remove var="flash" scope="session" />

<div class="app-viewport">

    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <div class="auth-container" data-aos="fade-up">
        <div class="logo-box"><img src="https://img.icons8.com/?size=100&id=wB7OSDpKBQsG&format=png&color=FFFFFF" alt=""></div>
        <h2>Bem-vindo de volta</h2>
        <p>Acesse sua adega pessoal e seu carrinho.</p>

        <c:if test="${not empty erro}">
            <div class="error-msg" data-aos="fade-down">${erro}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post" class="form" style="margin-top:18px;">
            <c:if test="${not empty param.redirect}">
                <input type="hidden" name="redirect" value="${param.redirect}">
            </c:if>

            <div class="form-group">
                <label for="email">E-mail</label>
                <input type="email" id="email" name="email" required autocomplete="email"
                       placeholder="seu@email.com" value="${emailPrefill != null ? emailPrefill : ''}">
            </div>

            <div class="form-group">
                <label for="senha">Senha</label>
                <input type="password" id="senha" name="senha" required autocomplete="current-password" placeholder="Sua senha">
            </div>

            <button type="submit" class="btn-wine btn-block" style="margin-top:6px;">Entrar</button>
        </form>

        <div class="auth-divider">ou</div>

        <a href="${pageContext.request.contextPath}/cadastro" class="btn-wine-outline btn-block">Criar minha conta</a>
    </div>

    <%@ include file="/WEB-INF/jspf/app-footer.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/page-scripts.jspf" %>
</body>
</html>
