document.addEventListener("DOMContentLoaded", () => {

    /* =========================
       FILTRO DE BÚSQUEDA SIDEBAR
    ========================= */
    const searchInput = document.querySelector(".search-input");
    const navButtons = document.querySelectorAll(".menu .nav-button");

    if (searchInput) {
        searchInput.addEventListener("input", () => {
            const term = searchInput.value.toLowerCase();

            navButtons.forEach(btn => {
                const text = btn.textContent.toLowerCase();
                btn.style.display = text.includes(term) ? "block" : "none";
            });
        });
    }


    /* =========================
       BOTÓN ACTIVO AUTOMÁTICO
    ========================= */
    const currentPath = window.location.pathname;

    document.querySelectorAll(".nav-button").forEach(link => {
        if (link.getAttribute("href") === currentPath) {
            link.classList.add("active");
        } else {
            link.classList.remove("active");
        }
    });


    /* =========================
       ANIMACIÓN DE CONTADORES
    ========================= */
    const counters = document.querySelectorAll(".stat-card h2");

    counters.forEach(counter => {
        const target = Number(counter.textContent);
        counter.textContent = "0";

        let current = 0;
        const increment = Math.max(1, Math.ceil(target / 60));

        const updateCounter = () => {
            current += increment;
            if (current >= target) {
                counter.textContent = target;
            } else {
                counter.textContent = current;
                requestAnimationFrame(updateCounter);
            }
        };

        updateCounter();
    });


    /* =========================
       CONFIRMACIÓN DE CIERRE
    ========================= */
    const logoutLinks = document.querySelectorAll('a[href="/logout"]');

    logoutLinks.forEach(link => {
        link.addEventListener("click", e => {
            const confirmExit = confirm("¿Seguro que deseas cerrar sesión?");
            if (!confirmExit) {
                e.preventDefault();
            }
        });
    });


    /* =========================
       ATAJOS DE TECLADO (UX)
    ========================= */
    document.addEventListener("keydown", e => {
        // Enfocar búsqueda con /
        if (e.key === "/") {
            e.preventDefault();
            searchInput?.focus();
        }

        // Salir con ESC
        if (e.key === "Escape") {
            searchInput?.blur();
        }
    });


    /* =========================
       EFECTO HOVER DINÁMICO
    ========================= */
    document.querySelectorAll(".stat-card").forEach(card => {
        card.addEventListener("mouseenter", () => {
            card.style.transform = "translateY(-6px)";
            card.style.transition = "0.3s";
        });

        card.addEventListener("mouseleave", () => {
            card.style.transform = "translateY(0)";
        });
    });

});
