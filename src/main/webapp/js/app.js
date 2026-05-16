/**
 * Vinheria Agnello — UI scripts
 * - AOS init (scroll reveal)
 * - Quantity stepper
 * - Toast
 * - Address selector no checkout
 * - AJAX para favoritar e adicionar ao carrinho (sem refresh)
 */
(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        initAOS();
        initQty();
        initToast();
        initCheckoutAddressToggle();
        initFavoritos();
        initAddCart();
    });

    function initAOS() {
        if (typeof AOS === 'undefined') return;
        AOS.init({
            duration: 550,
            easing: 'ease-out-cubic',
            once: true,
            offset: 24,
            disableMutationObserver: true
        });
        window.addEventListener('load', function () {
            AOS.refresh();
        });
    }

    function initQty() {
        document.querySelectorAll('.qty').forEach(function (wrap) {
            var input = wrap.querySelector('input[type="number"]');
            if (!input) return;
            wrap.querySelectorAll('button[data-step]').forEach(function (btn) {
                btn.addEventListener('click', function (ev) {
                    ev.preventDefault();
                    var step = parseInt(btn.getAttribute('data-step'), 10) || 1;
                    var val = parseInt(input.value, 10) || 1;
                    var next = Math.max(1, Math.min(99, val + step));
                    input.value = next;
                    input.dispatchEvent(new Event('change', { bubbles: true }));
                });
            });
        });
    }

    function initToast() {
        var msg = document.body.getAttribute('data-flash');
        if (msg) {
            showToast(msg);
            document.body.removeAttribute('data-flash');
        }
    }

    window.showToast = function (msg) {
        var el = document.createElement('div');
        el.className = 'toast';
        el.textContent = msg;
        document.body.appendChild(el);
        requestAnimationFrame(function () { el.classList.add('show'); });
        setTimeout(function () {
            el.classList.remove('show');
            setTimeout(function () { el.remove(); }, 400);
        }, 3000);
    };

    function initCheckoutAddressToggle() {
        var radios = document.querySelectorAll('input[name="enderecoId"]');
        var novoBlock = document.getElementById('novo-endereco-block');
        var novoFlag = document.getElementById('novo-endereco-flag');
        if (radios.length === 0 || !novoBlock) return;

        function update() {
            var selected = document.querySelector('input[name="enderecoId"]:checked');
            var isNew = selected && selected.value === 'novo';
            novoBlock.style.display = isNew ? 'block' : 'none';
            if (novoFlag) novoFlag.value = isNew ? 'on' : '';
            novoBlock.querySelectorAll('input').forEach(function (i) {
                if (i.dataset.required === 'true') i.required = isNew;
            });
        }
        radios.forEach(function (r) { r.addEventListener('change', update); });
        update();
    }

    /* ============================================================
       AJAX: Favoritar
       ============================================================ */
    function initFavoritos() {
        document.addEventListener('click', function (ev) {
            var btn = ev.target.closest('.js-fav');
            if (!btn) return;
            ev.preventDefault();

            if (btn.dataset.busy === '1') return;
            btn.dataset.busy = '1';

            var vinhoId = btn.getAttribute('data-vinho-id');
            var isAtivo = btn.classList.contains('btn-favorito--ativo');
            var acao = isAtivo ? 'remover' : 'adicionar';

            var params = new URLSearchParams();
            params.append('vinhoId', vinhoId);
            params.append('acao', acao);

            ajax(contextUrl('favoritos'), params)
                .then(function (data) {
                    if (!data.ok) {
                        if (data.loginRequired) {
                            window.location.href = data.redirect || 'login';
                            return;
                        }
                        showToast('Não foi possível salvar.');
                        return;
                    }
                    setFavoritoEstado(vinhoId, !!data.favorito);
                    if (data.favorito) showToast('Adicionado aos favoritos.');
                    else showToast('Removido dos favoritos.');

                    // Se estiver na página de favoritos, anima saída do card
                    if (!data.favorito) {
                        var card = btn.closest('[data-fav-card]');
                        if (card) {
                            card.style.transition = 'opacity .25s, transform .25s';
                            card.style.opacity = '0';
                            card.style.transform = 'translateX(40px)';
                            setTimeout(function () { card.remove(); checkEmptyFavList(); }, 260);
                        }
                    }
                })
                .catch(function () { showToast('Erro de rede.'); })
                .finally(function () { btn.dataset.busy = '0'; });
        });
    }

    function setFavoritoEstado(vinhoId, ativo) {
        document.querySelectorAll('.js-fav[data-vinho-id="' + vinhoId + '"]').forEach(function (b) {
            b.classList.toggle('btn-favorito--ativo', ativo);
            b.innerHTML = ativo ? '&#9829;' : '&#9825;';
            b.setAttribute('aria-label', ativo ? 'Remover dos favoritos' : 'Adicionar aos favoritos');
            b.setAttribute('title', ativo ? 'Remover dos favoritos' : 'Adicionar aos favoritos');
        });
    }

    function checkEmptyFavList() {
        var list = document.querySelector('[data-fav-list]');
        if (!list) return;
        if (list.querySelectorAll('[data-fav-card]').length === 0) {
            list.innerHTML =
                '<div class="empty-state">' +
                '  <div class="empty-icon">&#9825;</div>' +
                '  <h3>Nenhum favorito ainda</h3>' +
                '  <p>Explore a adega e marque os vinhos que mais te interessam.</p>' +
                '  <a href="adega" class="btn-wine">Explorar a Adega</a>' +
                '</div>';
        }
    }

    /* ============================================================
       AJAX: Adicionar ao carrinho
       ============================================================ */
    function initAddCart() {
        document.addEventListener('submit', function (ev) {
            var form = ev.target.closest('.js-add-cart');
            if (!form) return;
            ev.preventDefault();
            if (form.dataset.busy === '1') return;
            form.dataset.busy = '1';

            var params = new URLSearchParams(new FormData(form));
            if (!params.get('acao')) params.append('acao', 'adicionar');

            var btn = form.querySelector('button[type="submit"]');
            var originalLabel = btn ? btn.innerHTML : '';
            if (btn) { btn.disabled = true; btn.innerHTML = 'Adicionando...'; }

            ajax(form.getAttribute('action') || contextUrl('carrinho'), params)
                .then(function (data) {
                    if (!data.ok) {
                        if (data.loginRequired) {
                            window.location.href = data.redirect || 'login';
                            return;
                        }
                        showToast('Erro ao adicionar ao carrinho.');
                        return;
                    }
                    updateCartBadge(data.totalCarrinho);
                    showToast('Vinho adicionado ao carrinho.');
                    if (btn) btn.innerHTML = 'Adicionado &#10003;';
                    setTimeout(function () {
                        if (btn) { btn.disabled = false; btn.innerHTML = originalLabel; }
                    }, 1200);
                })
                .catch(function () {
                    showToast('Erro de rede.');
                    if (btn) { btn.disabled = false; btn.innerHTML = originalLabel; }
                })
                .finally(function () { form.dataset.busy = '0'; });
        });
    }

    function updateCartBadge(total) {
        var n = parseInt(total, 10) || 0;
        // Badges no header
        document.querySelectorAll('[data-cart-badge]').forEach(function (badge) {
            if (n > 0) {
                badge.textContent = n;
                badge.style.display = '';
                badge.classList.remove('hidden');
            } else {
                badge.style.display = 'none';
            }
        });

        // Se não existe nenhum badge ainda (primeira adição), cria um no link do carrinho
        if (n > 0 && document.querySelectorAll('[data-cart-badge]').length === 0) {
            var headerCartLink = document.querySelector('a[href$="/carrinho"].btn-icon');
            if (headerCartLink) {
                var b = document.createElement('span');
                b.className = 'badge-count';
                b.setAttribute('data-cart-badge', '');
                b.textContent = n;
                headerCartLink.appendChild(b);
            }
            var navCartLink = document.querySelector('a.nav-btn[href$="/carrinho"]');
            if (navCartLink) {
                var nb = document.createElement('span');
                nb.className = 'nav-badge';
                nb.setAttribute('data-cart-badge', '');
                nb.textContent = n;
                navCartLink.appendChild(nb);
            }
        }
    }

    /* ============================================================
       Util: chamada AJAX (sempre envia x-www-form-urlencoded)
       ============================================================ */
    function ajax(url, body) {
        var payload = body instanceof URLSearchParams
            ? body.toString()
            : new URLSearchParams(body || {}).toString();

        return fetch(url, {
            method: 'POST',
            body: payload,
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json',
                'X-Requested-With': 'fetch',
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
            }
        }).then(function (res) {
            return res.json().catch(function () { return { ok: false }; });
        });
    }

    function contextUrl(path) {
        // Resolve relative ao contexto do app (ex.: /vinheria-agnello/path)
        var base = document.querySelector('base');
        if (base) return base.getAttribute('href').replace(/\/$/, '') + '/' + path;
        // Fallback: usa a URL atual como base relativa
        var loc = window.location.pathname;
        // tira o último segmento (página atual) e usa o resto como contexto
        var idx = loc.lastIndexOf('/');
        return loc.substring(0, idx + 1) + path;
    }
})();
