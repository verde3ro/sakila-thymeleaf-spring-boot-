// Inicialización de Flatpickr para el campo de fecha y hora
const datePickers = document.querySelectorAll('.datetime-picker');
const form = document.querySelector('form');

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
if (form) {
	form.addEventListener('submit', (event) => {
		if (!form.checkValidity()) {
			event.preventDefault();
			event.stopPropagation();
		}
		form.classList.add('was-validated');
	});
}
