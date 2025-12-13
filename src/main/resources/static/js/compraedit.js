// Espera a que el DOM esté completamente cargado
document.addEventListener('DOMContentLoaded', function() {

    // Obtener los elementos del formulario
    const form = document.querySelector('.producto-form');
    const cancelButton = document.querySelector('.btn-cancel');
    const resetButton = document.querySelector('.btn-clear');

    // Función para validar los campos antes de enviar el formulario
    function validateForm(event) {
        const descripcion = document.querySelector('#descripcion');
        const fecha = document.querySelector('#fecha');
        const total = document.querySelector('#total');
        
        // Verificar que los campos no estén vacíos
        if (!descripcion.value || !fecha.value || !total.value) {
            alert('Por favor, completa todos los campos.');
            event.preventDefault(); // Previene el envío si algún campo está vacío
        } else {
            // Validación de total como número positivo
            if (parseFloat(total.value) <= 0) {
                alert('El total debe ser un número mayor que cero.');
                event.preventDefault();
            }
        }
    }

    // Función para confirmar la cancelación del formulario
    function confirmCancel(event) {
        const confirmation = confirm("¿Estás seguro de que deseas cancelar? Los cambios no guardados se perderán.");
        if (!confirmation) {
            event.preventDefault(); // Si el usuario no confirma, no se cancela
        }
    }

    // Función para limpiar el formulario (opcional)
    function clearForm() {
        if (confirm('¿Estás seguro de que deseas limpiar el formulario?')) {
            form.reset();
        }
    }

    // Agregar eventos
    if (form) {
        form.addEventListener('submit', validateForm); // Validar antes de enviar
    }

    if (cancelButton) {
        cancelButton.addEventListener('click', confirmCancel); // Confirmar cancelación
    }

    if (resetButton) {
        resetButton.addEventListener('click', clearForm); // Limpiar el formulario
    }
});
