/* ============================================================
   CINERESERVE — Interactive Vanilla JS Module
   Scroll-reveal, smooth scrolling, mobile menu, toast system
   ============================================================ */
(function () {
    'use strict';

    /* ---- 1. Scroll-Reveal via IntersectionObserver ---- */
    function initScrollReveal() {
        const targets = document.querySelectorAll('[data-reveal]');
        if (!targets.length) return;

        /* Respect prefers-reduced-motion */
        if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
            targets.forEach(function (el) { el.classList.add('revealed'); });
            return;
        }

        var idx = 0;
        var observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    /* stagger delay based on data-reveal-delay or sequential order */
                    var delay = entry.target.getAttribute('data-reveal-delay');
                    if (!delay) {
                        delay = idx * 80;
                        idx++;
                    }
                    setTimeout(function () {
                        entry.target.classList.add('revealed');
                    }, parseInt(delay, 10));
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });

        targets.forEach(function (el) { observer.observe(el); });
    }

    /* ---- 2. Smooth Scroll for Anchor Links ---- */
    function initSmoothScroll() {
        document.addEventListener('click', function (e) {
            var link = e.target.closest('a[href^="#"]');
            if (!link) return;
            var id = link.getAttribute('href');
            if (id === '#' || id.length < 2) return;
            var target = document.querySelector(id);
            if (!target) return;
            e.preventDefault();
            target.scrollIntoView({ behavior: 'smooth', block: 'start' });
            /* Update URL without scroll */
            history.pushState(null, '', id);
        });
    }

    /* ---- 3. Active Nav Indicator on Scroll ---- */
    function initActiveNavIndicator() {
        var sections = document.querySelectorAll('main section[id]');
        if (!sections.length) return;

        var navLinks = document.querySelectorAll('.navbar .nav-link');

        var observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    var secId = entry.target.id;
                    navLinks.forEach(function (l) {
                        l.classList.remove('active-nav');
                        var href = l.getAttribute('href') || '';
                        if (href === '#' + secId) {
                            l.classList.add('active-nav');
                        }
                    });
                }
            });
        }, { threshold: 0.3 });

        sections.forEach(function (s) { observer.observe(s); });
    }

    /* ---- 4. Toast Notification System ---- */
    window.showToast = function (message, type) {
        type = type || 'info';
        var container = document.querySelector('.toast-container');
        if (!container) {
            container = document.createElement('div');
            container.className = 'toast-container';
            document.body.appendChild(container);
        }

        var toast = document.createElement('div');
        toast.style.cssText =
            'background: var(--bg-surface); border: 1px solid var(--border); ' +
            'border-radius: 12px; padding: 14px 20px; margin-top: 8px; ' +
            'color: var(--text-primary); font-size: 0.9rem; max-width: 360px; ' +
            'box-shadow: 0 8px 30px rgba(0,0,0,0.5); ' +
            'transform: translateX(120%); transition: transform 0.35s cubic-bezier(0.22,1,0.36,1), opacity 0.3s ease; ' +
            'opacity: 0; display: flex; align-items: center; gap: 10px;';

        var colors = { success: '#22c55e', error: '#ef4444', info: '#3b82f6', warning: '#eab308' };
        var dotColor = colors[type] || colors.info;
        toast.innerHTML =
            '<span style="width:8px;height:8px;border-radius:50%;background:' + dotColor + ';flex-shrink:0"></span>' +
            '<span>' + message + '</span>';

        container.appendChild(toast);

        requestAnimationFrame(function () {
            toast.style.transform = 'translateX(0)';
            toast.style.opacity = '1';
        });

        setTimeout(function () {
            toast.style.transform = 'translateX(120%)';
            toast.style.opacity = '0';
            setTimeout(function () { toast.remove(); }, 350);
        }, 4000);
    };

    /* ---- 5. Auto-dismiss Bootstrap Alerts ---- */
    function initAlertAutoDismiss() {
        var alerts = document.querySelectorAll('.alert-dismissible');
        alerts.forEach(function (alert) {
            setTimeout(function () {
                var closeBtn = alert.querySelector('.btn-close');
                if (closeBtn) closeBtn.click();
            }, 6000);
        });
    }

    /* ---- 6. Navbar Scroll Shadow ---- */
    function initNavShadow() {
        var nav = document.querySelector('.navbar');
        if (!nav) return;
        window.addEventListener('scroll', function () {
            if (window.scrollY > 30) {
                nav.style.boxShadow = '0 4px 20px rgba(0,0,0,0.5)';
                nav.style.borderBottomColor = 'rgba(42,42,46,0.6)';
            } else {
                nav.style.boxShadow = 'none';
                nav.style.borderBottomColor = '';
            }
        }, { passive: true });
    }

    /* ---- Initialize Everything on DOM Ready ---- */
    document.addEventListener('DOMContentLoaded', function () {
        initScrollReveal();
        initSmoothScroll();
        initActiveNavIndicator();
        initAlertAutoDismiss();
        initNavShadow();
    });
})();
