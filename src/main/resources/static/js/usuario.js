// usuariosUnified.js - Funcionalidades unificadas para Usuarios

document.addEventListener('DOMContentLoaded', () => {

    /* ===== 1. BÚSQUEDA EN SIDEBAR ===== */
    const sidebarSearch = document.querySelector('.search-input');
    const navButtons = document.querySelectorAll('.nav-button');

    if (sidebarSearch) {
        sidebarSearch.addEventListener('input', () => {
            const term = sidebarSearch.value.toLowerCase().trim();
            navButtons.forEach(btn => {
                const text = btn.textContent.toLowerCase();
                btn.style.backgroundColor = term && text.includes(term) ? '#ff7b00ff' : '';
            });
        });
    }

    /* ===== 2. TABLA DE USUARIOS ===== */
    const table = document.querySelector('.products-table');
    if (table) {
        const tbody = table.querySelector('tbody');
        if (tbody) {
            let rows = Array.from(tbody.querySelectorAll('tr'));

            // Búsqueda en tabla
            const tableSearch = document.querySelector('.table-search');
            if (tableSearch) {
                tableSearch.addEventListener('input', () => {
                    const filter = tableSearch.value.toLowerCase();
                    rows = Array.from(tbody.querySelectorAll('tr'));
                    rows.forEach(row => {
                        const cells = Array.from(row.querySelectorAll('td'));
                        const match = cells.some(td => td.textContent.toLowerCase().includes(filter));
                        row.style.display = match ? '' : 'none';
                    });
                });
            }

            // Hover de filas
            rows.forEach(row => {
                row.addEventListener('mouseenter', () => row.style.backgroundColor = '#f8f9fa');
                row.addEventListener('mouseleave', () => row.style.backgroundColor = '');
            });

            // Confirmación eliminación
            document.querySelectorAll('.btn-delete').forEach(btn => {
                btn.addEventListener('click', e => {
                    if (!confirm('¿Estás seguro de que quieres eliminar este usuario?')) {
                        e.preventDefault();
                    }
                });
            });
        }
    }

    /* ===== 3. FORMULARIOS CREAR / EDITAR USUARIO ===== */
    const form = document.querySelector('.pedido-form') || document.querySelector('form[th\\:object]');
    if (form) {
        const nombre = form.querySelector('[name="nombreUsuario"]');
        const email = form.querySelector('[name="email"]');
        const password = form.querySelector('[name="nuevaPassword"]');
        const rol = form.querySelector('[name="rol"]');

        // Validación simple
        form.addEventListener('submit', e => {
            if (!nombre.value || !email.value || !rol.value) {
                e.preventDefault();
                alert('Por favor completa todos los campos obligatorios (Nombre, Email, Rol).');
            }
        });

        // Interactividad en inputs
        [nombre, email, password, rol].forEach(input => {
            if (input) {
                input.addEventListener('focus', () => input.style.borderColor = '#ff8c00');
                input.addEventListener('blur', () => input.style.borderColor = '#d4af37');
            }
        });

        // Botón limpiar
        const btnClear = form.querySelector('.btn-clear');
        if (btnClear) {
            btnClear.addEventListener('click', () => form.reset());
        }
    }

});
