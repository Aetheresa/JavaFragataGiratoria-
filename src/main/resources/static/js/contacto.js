document.addEventListener("DOMContentLoaded", () => {

    // Auto ocultar alertas
    document.querySelectorAll(".auto-hide").forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = "0";
            alert.style.transition = "opacity 0.8s ease";
            setTimeout(() => alert.remove(), 500);
        }, 6000);
    });

    // Animación focus en inputs
    const inputs = document.querySelectorAll("input, textarea");

    inputs.forEach(input => {
        input.addEventListener("focus", () => {
            input.style.transform = "scale(1.01)";
        });

        input.addEventListener("blur", () => {
            input.style.transform = "scale(1)";
        });
    });

    // Validación visual rápida
    const form = document.querySelector("form");

    if (form) {
        form.addEventListener("submit", (e) => {
            let valid = true;

            inputs.forEach(input => {
                if (input.hasAttribute("required") && !input.value.trim()) {
                    valid = false;
                    input.style.borderColor = "#dc3545";
                    input.style.boxShadow = "0 0 6px rgba(220,53,69,0.6)";
                } else {
                    input.style.borderColor = "#f5d487";
                    input.style.boxShadow = "0 0 6px rgba(245,212,135,0.4)";
                }
            });

            if (!valid) {
                e.preventDefault();
            }
        });
    }
});
