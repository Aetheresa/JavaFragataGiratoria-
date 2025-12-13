// ayuda.js

// Alternar respuesta de FAQ
function toggleFAQ(element) {
    const answer = element.nextElementSibling;
    answer.style.display = (answer.style.display === "block") ? "none" : "block";
    const chevron = element.querySelector(".chevron");
    chevron.textContent = (chevron.textContent === "▼") ? "▲" : "▼";
}

// Scroll a sección específica
function scrollToSection(id) {
    const section = document.getElementById(id);
    if (section) {
        section.scrollIntoView({ behavior: "smooth" });
    }
}

// Funciones de contacto (ejemplos)
function openChat() {
    alert("Iniciando chat en vivo...");
    // Aquí se puede abrir el sistema de chat real
}

function openEmail() {
    window.location.href = "mailto:soporte@fragatagiratoria.com";
}

function openDocs() {
    window.open("/docs/documentacion.html", "_blank");
}

// Búsqueda dentro de la ayuda (simple)
const searchInput = document.getElementById("searchHelp");
if (searchInput) {
    searchInput.addEventListener("input", function() {
        const query = this.value.toLowerCase();
        document.querySelectorAll(".faq-item").forEach(item => {
            const text = item.textContent.toLowerCase();
            item.style.display = text.includes(query) ? "block" : "none";
        });
    });
}
