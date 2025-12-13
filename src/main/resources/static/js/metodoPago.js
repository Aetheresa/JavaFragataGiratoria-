// metodosPagoUnified.js - Funcionalidades unificadas para Métodos de Pago

document.addEventListener('DOMContentLoaded', function() {

    /* ===== 1. BÚSQUEDA EN SIDEBAR ===== */
    const sidebarSearch = document.querySelector('.search-input');
    const navButtons = document.querySelectorAll('.nav-button');

    if (sidebarSearch) {
        sidebarSearch.addEventListener('input', function() {
            const term = this.value.toLowerCase().trim();
            navButtons.forEach(btn => {
                const text = btn.textContent.toLowerCase();
                btn.style.backgroundColor = term && text.includes(term) ? '#ff7b00ff' : '';
            });
        });
    }

    /* ===== 2. TABLA DE MÉTODOS DE PAGO ===== */
    const table = document.querySelector('.products-table');
    if (table) {
        const tbody = table.querySelector('tbody');
        if (tbody) {
            let tableRows = Array.from(tbody.querySelectorAll('tr'));

            /* Búsqueda en tabla */
            const tableSearch = document.querySelector('.table-search');
            if (tableSearch) {
                tableSearch.addEventListener('input', function () {
                    const filter = this.value.toLowerCase();
                    tableRows = Array.from(tbody.querySelectorAll('tr'));
                    tableRows.forEach(row => {
                        const cells = Array.from(row.querySelectorAll('td'));
                        const match = cells.some(td => td.textContent.toLowerCase().includes(filter));
                        row.style.display = match ? '' : 'none';
                    });
                });
            }

            /* Hover en filas */
            tableRows.forEach(row => {
                row.addEventListener('mouseenter', () => row.style.backgroundColor = '#f8f9fa');
                row.addEventListener('mouseleave', () => row.style.backgroundColor = '');
            });

            /* Ordenar tabla por columnas */
            const tableHeaders = table.querySelectorAll('th');
            tableHeaders.forEach((th, idx) => {
                th.style.cursor = 'pointer';
                th.addEventListener('click', () => sortTable(idx));
            });

            function sortTable(colIndex) {
                const rowsArray = tableRows.filter(r => r.style.display !== 'none');
                if (rowsArray.length === 0) return;
                const isNumeric = !isNaN(rowsArray[0].cells[colIndex].textContent.trim());
                rowsArray.sort((a, b) => {
                    const aText = a.cells[colIndex].textContent.trim();
                    const bText = b.cells[colIndex].textContent.trim();
                    if (isNumeric) return parseFloat(aText) - parseFloat(bText);
                    return aText.localeCompare(bText);
                });
                rowsArray.forEach(row => tbody.appendChild(row));
            }

            /* Confirmación eliminar */
            document.querySelectorAll('.btn-delete').forEach(btn => {
                btn.addEventListener('click', e => {
                    if (!confirm('¿Eliminar este método de pago? Esta acción no se puede deshacer.')) {
                        e.preventDefault();
                    }
                });
            });
        }
    }

    /* ===== 3. FORMULARIOS DE CREAR Y EDITAR ===== */
    const form = document.querySelector('.producto-form, .pedido-form');
    if (form) {
        const nombreMetodo = form.querySelector('#nombreMetodo');
        const descripcion = form.querySelector('#descripcion');

        /* Validación básica */
        form.addEventListener('submit', (e) => {
            if (!nombreMetodo.value.trim()) {
                e.preventDefault();
                alert('Por favor ingresa el nombre del método de pago.');
            }
        });

        /* Interactividad inputs */
        [nombreMetodo, descripcion].forEach(input => {
            input.addEventListener('focus', () => input.style.borderColor = '#ff8c00');
            input.addEventListener('blur', () => input.style.borderColor = '#d4af37');
        });

        /* Botón limpiar (solo para crear) */
        const btnClear = form.querySelector('.btn-clear');
        if (btnClear) {
            btnClear.addEventListener('click', () => form.reset());
        }
    }

});
