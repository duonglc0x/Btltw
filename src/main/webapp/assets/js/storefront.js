(function () {
    'use strict';
    var toggle = document.querySelector('.menu-toggle');
    var menu = document.getElementById('store-menu');
    if (toggle && menu) {
        function closeMenu() {
            toggle.setAttribute('aria-expanded', 'false');
            toggle.setAttribute('aria-label', 'Mở menu');
            menu.classList.remove('is-open');
        }
        toggle.addEventListener('click', function () {
            var open = toggle.getAttribute('aria-expanded') !== 'true';
            toggle.setAttribute('aria-expanded', String(open));
            toggle.setAttribute('aria-label', open ? 'Đóng menu' : 'Mở menu');
            menu.classList.toggle('is-open', open);
        });
        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape' && toggle.getAttribute('aria-expanded') === 'true') {
                closeMenu();
                toggle.focus();
            }
        });
        document.addEventListener('click', function (event) {
            if (!event.target.closest('.store-nav')) closeMenu();
        });
    }
    var path = window.location.pathname.replace(/\/$/, '');
    document.querySelectorAll('[data-nav]').forEach(function (link) {
        var destination = new URL(link.href).pathname;
        if (path === destination || (link.dataset.nav === 'product' && path.indexOf(destination + '/') === 0)) {
            link.setAttribute('aria-current', 'page');
        }
    });
    var script = document.querySelector('script[src*="/assets/js/storefront.js"]');
    var assets = script ? new URL('../images/', script.src).href : '';
    document.querySelectorAll('img[data-fallback]').forEach(function (image) {
        function fallback() {
            image.removeEventListener('error', fallback);
            image.removeAttribute('onerror');
            image.src = assets + (image.dataset.fallback === 'avatar' ? 'avatar.svg' : 'product-placeholder.svg');
        }
        image.addEventListener('error', fallback);
        if (image.complete && image.naturalWidth === 0) fallback();
    });
    document.querySelectorAll('.form-group').forEach(function (group, index) {
        var label = group.querySelector('label');
        var field = group.querySelector('input:not([type="hidden"]), select, textarea');
        if (label && field && !label.htmlFor && !label.contains(field)) {
            if (!field.id) field.id = 'field-' + index;
            label.htmlFor = field.id;
        }
    });
}());
