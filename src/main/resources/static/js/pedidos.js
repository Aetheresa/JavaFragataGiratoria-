// pedidos.js

// Asegúrate de que el DOM esté completamente cargado antes de ejecutar cualquier script.
document.addEventListener("DOMContentLoaded", () => {

    // ===== VALIDACIÓN DEL FORMULARIO =====
    const form = document.querySelector('.pedido-form');
    
    // Función para mostrar mensajes de error
    function showError(message, field) {
        const errorDiv = document.createElement('div');
        errorDiv.classList.add('error-message');
        errorDiv.textContent = message;
        field.parentElement.appendChild(errorDiv);
    }

    // Función para quitar el mensaje de error
    function clearErrorMessages() {
        const errorMessages = document.querySelectorAll('.error-message');
        errorMessages.forEach(msg => msg.remove());
    }

    form.addEventListener('submit', (e) => {
        e.preventDefault(); // Evita que el formulario se envíe de forma predeterminada.

        clearErrorMessages(); // Limpiar cualquier error previo.

        let valid = true;

        const fecha = document.querySelector('#fecha');
        const cantidad = document.querySelector('#cantidad');
        const precioUnitario = document.querySelector('#precioUnitario');
        const subtotal = document.querySelector('#subtotal');
        const total = document.querySelector('#total');
        const estado = document.querySelector('#estado');

        // Validaciones simples
        if (!fecha.value) {
            showError("La fecha del pedido es obligatoria.", fecha);
            valid = false;
        }

        if (!cantidad.value || cantidad.value <= 0) {
            showError("La cantidad debe ser mayor que cero.", cantidad);
            valid = false;
        }

        if (!precioUnitario.value || precioUnitario.value <= 0) {
            showError("El precio unitario debe ser un número mayor que cero.", precioUnitario);
            valid = false;
        }

        if (!subtotal.value || subtotal.value <= 0) {
            showError("El subtotal debe ser mayor que cero.", subtotal);
            valid = false;
        }

        if (!total.value || total.value <= 0) {
            showError("El total debe ser mayor que cero.", total);
            valid = false;
        }

        if (!estado.value) {
            showError("El estado del pedido es obligatorio.", estado);
            valid = false;
        }

        // Si todo es válido, se puede enviar el formulario
        if (valid) {
            form.submit(); // Envía el formulario si es válido.
        }
    });

    // ===== CALCULAR SUBTOTAL Y TOTAL =====
    // Agregar un evento para el cálculo automático del subtotal y total
    const cantidadInput = document.querySelector('#cantidad');
    const precioInput = document.querySelector('#precioUnitario');
    const subtotalInput = document.querySelector('#subtotal');
    const totalInput = document.querySelector('#total');

    function calcularSubtotalYTotal() {
        const cantidad = parseFloat(cantidadInput.value);
        const precio = parseFloat(precioInput.value);
        if (!isNaN(cantidad) && !isNaN(precio)) {
            const subtotal = cantidad * precio;
            subtotalInput.value = subtotal.toFixed(2); // 2 decimales
            totalInput.value = subtotal.toFixed(2); // Por ahora, total es igual a subtotal
        }
    }

    cantidadInput.addEventListener('input', calcularSubtotalYTotal);
    precioInput.addEventListener('input', calcularSubtotalYTotal);

    // ===== CONFIRMAR ELIMINADO =====
    // Confirmar la eliminación de un pedido
    const deleteLinks = document.querySelectorAll('.btn-delete');

    deleteLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            const confirmDelete = confirm('¿Estás seguro de eliminar este pedido?');

            if (!confirmDelete) {
                e.preventDefault(); // Si no se confirma, no se elimina el pedido
            }
        });
    });

});
