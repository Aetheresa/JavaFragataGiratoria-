// productocrud.js - Funcionalidades para CRUD de Productos

document.addEventListener('DOMContentLoaded', function() {
    // 1. Búsqueda en Sidebar
    const searchInput = document.getElementById('search-input');
    const navButtons = document.querySelectorAll('.nav-button');
    
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase().trim();
            
            navButtons.forEach(button => {
                const buttonText = button.textContent.toLowerCase();
                
                if (searchTerm === '') {
                    button.style.backgroundColor = '';
                } else if (buttonText.includes(searchTerm)) {
                    button.style.backgroundColor = '#ff8c00';
                } else {
                    button.style.backgroundColor = '';
                }
            });
        });
    }
    
    // 2. Búsqueda en Tabla de Productos
    function initTableSearch() {
        const tableRows = document.querySelectorAll('#products-body tr');
        const searchInputTable = document.querySelector('.search-table');
        
        if (searchInputTable && tableRows.length > 0) {
            searchInputTable.addEventListener('input', function() {
                const filter = this.value.toLowerCase();
                
                tableRows.forEach(row => {
                    const text = row.textContent.toLowerCase();
                    row.style.display = text.includes(filter) ? '' : 'none';
                });
            });
        }
    }
    
    // 3. Resaltar filas de la tabla al pasar el mouse
    function initTableHover() {
        const tableRows = document.querySelectorAll('#products-body tr');
        
        tableRows.forEach(row => {
            row.addEventListener('mouseenter', function() {
                this.style.backgroundColor = '#f8f9fa';
            });
            
            row.addEventListener('mouseleave', function() {
                this.style.backgroundColor = '';
            });
        });
    }
    
    // 4. Ordenar tabla por columnas (opcional)
    function initTableSorting() {
        const tableHeaders = document.querySelectorAll('.products-table th');
        
        tableHeaders.forEach((header, index) => {
            header.style.cursor = 'pointer';
            
            header.addEventListener('click', function() {
                sortTable(index);
            });
        });
    }
    
    function sortTable(columnIndex) {
        const table = document.querySelector('.products-table');
        const tbody = table.querySelector('tbody');
        const rows = Array.from(tbody.querySelectorAll('tr'));
        
        // Excluir la fila de "no hay productos"
        const filteredRows = rows.filter(row => !row.classList.contains('no-products-row'));
        
        if (filteredRows.length === 0) return;
        
        const isAscending = !table.querySelector('th').classList.contains('sorted-asc');
        
        filteredRows.sort((rowA, rowB) => {
            const cellA = rowA.cells[columnIndex].textContent.trim();
            const cellB = rowB.cells[columnIndex].textContent.trim();
            
            // Intentar convertir a número si es posible
            const numA = isNaN(cellA) ? cellA : parseFloat(cellA);
            const numB = isNaN(cellB) ? cellB : parseFloat(cellB);
            
            if (isAscending) {
                return numA > numB ? 1 : -1;
            } else {
                return numA < numB ? 1 : -1;
            }
        });
        
        // Limpiar tbody y agregar filas ordenadas
        tbody.innerHTML = '';
        filteredRows.forEach(row => tbody.appendChild(row));
        
        // Agregar la fila de "no hay productos" si existía
        const noProductsRow = rows.find(row => row.classList.contains('no-products-row'));
        if (noProductsRow) {
            tbody.appendChild(noProductsRow);
        }
        
        // Actualizar indicadores de orden
        table.querySelectorAll('th').forEach(th => {
            th.classList.remove('sorted-asc', 'sorted-desc');
        });
        
        tableHeaders[columnIndex].classList.add(isAscending ? 'sorted-asc' : 'sorted-desc');
    }
    
    // 5. Confirmación para eliminar productos
    function initDeleteConfirmations() {
        const deleteButtons = document.querySelectorAll('.btn-delete');
        
        deleteButtons.forEach(button => {
            button.addEventListener('click', function(e) {
                if (!confirm('¿Estás seguro de que quieres eliminar este producto? Esta acción no se puede deshacer.')) {
                    e.preventDefault();
                }
            });
        });
    }
    
    // 6. Filtrar por estado de stock
    function initStockFilter() {
        const filterSelect = document.querySelector('.stock-filter');
        
        if (filterSelect) {
            filterSelect.addEventListener('change', function() {
                const filterValue = this.value;
                const tableRows = document.querySelectorAll('#products-body tr');
                
                tableRows.forEach(row => {
                    if (row.classList.contains('no-products-row')) return;
                    
                    const statusBadge = row.querySelector('.status-badge');
                    const statusText = statusBadge ? statusBadge.textContent.trim() : '';
                    
                    if (filterValue === 'all' || 
                        (filterValue === 'low' && statusText === 'BAJO STOCK') ||
                        (filterValue === 'ok' && statusText === 'NORMAL')) {
                        row.style.display = '';
                    } else {
                        row.style.display = 'none';
                    }
                });
            });
        }
    }
    
    // 7. Inicializar todas las funcionalidades
    function initAll() {
        initTableSearch();
        initTableHover();
        initTableSorting();
        initDeleteConfirmations();
        initStockFilter();
        
        console.log('CRUD de Productos - JavaScript inicializado');
    }
    
    // Inicializar cuando el DOM esté listo
    initAll();
    
    // 8. Exportar datos (ejemplo adicional)
    window.exportTableData = function(format) {
        const table = document.querySelector('.products-table');
        let data = [];
        
        // Obtener encabezados
        const headers = [];
        table.querySelectorAll('thead th').forEach(th => {
            headers.push(th.textContent.trim());
        });
        
        // Obtener datos de las filas
        table.querySelectorAll('tbody tr').forEach(row => {
            if (row.style.display === 'none') return;
            
            const rowData = [];
            row.querySelectorAll('td').forEach(td => {
                rowData.push(td.textContent.trim());
            });
            data.push(rowData);
        });
        
        console.log('Exportando datos en formato:', format);
        console.log('Encabezados:', headers);
        console.log('Datos:', data);
        
        // Aquí iría la lógica real de exportación
        alert(`Exportando ${data.length} productos en formato ${format.toUpperCase()}`);
    };
});

// 9. Funciones utilitarias
function formatNumber(number) {
    return new Intl.NumberFormat('es-ES').format(number);
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES');
}