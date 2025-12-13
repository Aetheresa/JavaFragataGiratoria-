document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.querySelector('.table-search');
    const tableRows = document.querySelectorAll('.products-table tbody tr');

    if (searchInput) {
        searchInput.addEventListener('input', () => {
            const filter = searchInput.value.toLowerCase();
            tableRows.forEach(row => {
                const cellsText = row.textContent.toLowerCase();
                row.style.display = cellsText.includes(filter) ? '' : 'none';
            });
        });
    }
});

document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('.platillo-form');

    if(form){
        form.addEventListener('submit', (e) => {
            const nombre = document.getElementById('nombre').value.trim();
            const precio = document.getElementById('precio').value.trim();

            if(!nombre || !precio){
                alert('Por favor, completa los campos obligatorios.');
                e.preventDefault();
            }
        });
    }
});

