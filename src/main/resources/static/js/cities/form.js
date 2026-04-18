// Inicialización de Flatpickr para el campo de fecha y hora
document.addEventListener('DOMContentLoaded', function() {
	// Seleccionar todos los campos con la clase datetime-picker
	const datePickers = document.querySelectorAll('.datetime-picker');

	if (datePickers.length > 0 && typeof flatpickr !== 'undefined') {
		flatpickr(datePickers, {
			enableTime: true,
			dateFormat: "Y-m-dTH:i",      // Formato ISO: 2026-04-15T12:35
			time_24hr: true,
			locale: "es",
			allowInput: true,
			clickOpens: true,
			theme: "bootstrap5"           // Tema Bootstrap 5 (si está disponible)
		});
	}

	// Validación de Bootstrap: añadir clase was-validated al enviar
	const form = document.querySelector('form');
	if (form) {
		form.addEventListener('submit', function(event) {
			if (!form.checkValidity()) {
				event.preventDefault();
				event.stopPropagation();
			}
			form.classList.add('was-validated');
		}, false);
	}
});
