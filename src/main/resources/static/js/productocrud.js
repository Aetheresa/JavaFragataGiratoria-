// productocrud.js - Funcionalidades para CRUD de Productos

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

/* ===== 2. TABLA DE PRODUCTOS ===== */
const table = document.querySelector('.products-table');
if (!table) return;

const tbody = table.querySelector('tbody');
if (!tbody) return; // Protección extra por si no existe tbody

let tableRows = Array.from(tbody.querySelectorAll('tr'));

/* ===== 3. BÚSQUEDA EN TABLA ===== */
const tableSearch = document.querySelector('.table-search');
if (tableSearch) {
    tableSearch.addEventListener('input', function () {
        const filter = this.value.toLowerCase();

        // Actualizar las filas cada vez por si hay cambios dinámicos
        tableRows = Array.from(tbody.querySelectorAll('tr'));

        tableRows.forEach(row => {
            const cells = Array.from(row.querySelectorAll('td'));
            const match = cells.some(td => td.textContent.toLowerCase().includes(filter));
            row.style.display = match ? '' : 'none';
        });
    });
}

    /* ===== 4. HOVER EN FILAS ===== */
    tableRows.forEach(row => {
        row.addEventListener('mouseenter', () => row.style.backgroundColor = '#f8f9fa');
        row.addEventListener('mouseleave', () => row.style.backgroundColor = '');
    });

    /* ===== 5. ORDENAR TABLA POR COLUMNAS ===== */
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

    /* ===== 6. CONFIRMACIÓN AL ELIMINAR PRODUCTO ===== */
    document.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', e => {
            if (!confirm('¿Eliminar este producto? Esta acción no se puede deshacer.')) {
                e.preventDefault();
            }
        });
    });

    /* ===== 7. FILTRO POR ESTADO DE STOCK ===== */
    const stockFilter = document.querySelector('.stock-filter');
    if (stockFilter) {
        stockFilter.addEventListener('change', function() {
            const val = this.value;
            tableRows.forEach(row => {
                if (row.classList.contains('no-products-row')) return;
                const status = row.querySelector('.status-badge')?.textContent.trim() || '';
                if (val === 'all' || (val === 'low' && status === 'BAJO STOCK') || (val === 'ok' && status === 'NORMAL')) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        });
    }

    /* ===== 8. EXPORTAR DATOS (Ejemplo) ===== */
    window.exportTableData = function(format) {
        const data = tableRows
            .filter(r => r.style.display !== 'none')
            .map(r => Array.from(r.querySelectorAll('td')).map(td => td.textContent.trim()));

        alert(`Exportando ${data.length} productos en formato ${format.toUpperCase()}`);
    };

    console.log('CRUD de Productos inicializado');
});

document.addEventListener('DOMContentLoaded', () => {

    const form = document.getElementById('crearProductoForm');

    form.addEventListener('submit', (e) => {
        // Validación simple antes de enviar
        const nombre = document.getElementById('nombre').value.trim();
        const precio = document.getElementById('precio').value;
        const stock = document.getElementById('stock').value;

        if (!nombre || precio < 0 || stock < 0) {
            e.preventDefault();
            alert('Por favor completa todos los campos correctamente.');
        }
    });

    // Interactividad: resaltar inputs al enfocarlos
    const inputs = form.querySelectorAll('input, textarea, select');
    inputs.forEach(input => {
        input.addEventListener('focus', () => input.style.borderColor = '#ff8c00');
        input.addEventListener('blur', () => input.style.borderColor = '#ccc');
    });

});

document.addEventListener('DOMContentLoaded', () => {
    const productForm = document.getElementById('productForm');
    const imageInput = document.getElementById('image');
    const imagePreview = document.getElementById('imagePreview');
    const noImageText = document.getElementById('noImageText');
    const cancelBtn = document.getElementById('cancelBtn');

    // Preview de imagen en tiempo real
    imageInput.addEventListener('change', () => {
        const file = imageInput.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = e => {
                imagePreview.src = e.target.result;
                imagePreview.style.display = 'block';
                noImageText.style.display = 'none';
            };
            reader.readAsDataURL(file);
        } else {
            imagePreview.src = '';
            imagePreview.style.display = 'none';
            noImageText.style.display = 'block';
        }
    });

    // Cancelar - limpia el formulario y preview
    cancelBtn.addEventListener('click', e => {
        e.preventDefault();
        productForm.reset();
        imagePreview.src = '';
        imagePreview.style.display = 'none';
        noImageText.style.display = 'block';
    });

    // Validación y envío básico del formulario
    productForm.addEventListener('submit', e => {
        e.preventDefault();

        if (!productForm.checkValidity()) {
            alert('Por favor, completa todos los campos requeridos correctamente.');
            return;
        }

        // Aquí iría la lógica para enviar datos a backend (AJAX, fetch, etc.)
        alert('Producto creado correctamente (simulado).');

        productForm.reset();
        imagePreview.src = '';
        imagePreview.style.display = 'none';
        noImageText.style.display = 'block';
    });
});

