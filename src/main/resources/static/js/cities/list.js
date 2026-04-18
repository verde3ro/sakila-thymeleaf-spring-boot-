// Manejar el cambio de tamaño de página
const pageSizeSelect = document.getElementById('pageSizeSelect');
// Confirmación de eliminación con Bootbox (usando JavaScript nativo para los eventos)
const deleteButtons = document.querySelectorAll('.btn-delete');

if (pageSizeSelect) {
	// Se mantiene function() porque necesitamos 'this' para obtener el valor del select
	pageSizeSelect.addEventListener('change', function () {
		const newSize = this.value;
		const url = new URL(window.location.href);
		url.searchParams.set('size', newSize);
		url.searchParams.set('page', '0');
		window.location.href = url.toString();
	});
}

deleteButtons.forEach(button => {
	button.addEventListener('click', (e) => {
		e.preventDefault();
		const link = e.currentTarget; // Elemento que disparó el evento (el enlace/botón)
		const deleteUrl = link.getAttribute('href');
		const cityName = link.dataset.city;

			bootbox.confirm({
				title: "Confirmar eliminación",
				message: "¿Está seguro de eliminar la ciudad <strong>" + cityName + "</strong>?",
				buttons: {
					cancel: {
						label: '<i class="bi bi-x-circle"></i> Cancelar',
						className: 'btn-secondary'
					},
					confirm: {
						label: '<i class="bi bi-trash"></i> Eliminar',
						className: 'btn-danger'
					}
				},
				callback: (result) => {
					if (result) {
						window.location.href = deleteUrl;
					}
				}
			});
	});
});
