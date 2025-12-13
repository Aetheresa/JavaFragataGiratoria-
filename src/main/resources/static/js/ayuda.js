// === TOGGLE SIDEBAR ===
const toggleBtn = document.getElementById("sidebar-toggle");
const sidebar = document.getElementById("sidebar");
const layout = document.querySelector(".layout-container");

toggleBtn.addEventListener("click", () => {
    sidebar.classList.toggle("collapsed");
    layout.classList.toggle("sidebar-collapsed");
});

// === CAMBIO DE SECCIONES ===
const buttons = document.querySelectorAll("[data-section]");
const sections = document.querySelectorAll(".section");

buttons.forEach(btn => {
    btn.addEventListener("click", () => {
        const sectionId = "section-" + btn.dataset.section;

        buttons.forEach(b => b.classList.remove("active"));
        btn.classList.add("active");

        sections.forEach(s => s.classList.remove("active"));
        document.getElementById(sectionId).classList.add("active");
    });
});
