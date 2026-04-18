// Manejar el cambio de tamaño de página
document.addEventListener('DOMContentLoaded', function () {
	const pageSizeSelect = document.getElementById('pageSizeSelect');

	if (pageSizeSelect) {
		pageSizeSelect.addEventListener('change', function () {
			const newSize = this.value;
			const url = new URL(window.location.href);
			url.searchParams.set('size', newSize);
			url.searchParams.set('page', '0');
			window.location.href = url.toString();
		});
	}

	// Confirmación de eliminación con Bootbox (requiere jQuery y Bootbox cargados)
	if (typeof $ !== 'undefined' && $.fn.bootbox) {
		$('.btn-delete').on('click', function (e) {
			e.preventDefault();
			const $link = $(this);
			const deleteUrl = $link.attr('href');
			const cityName = $link.data('city');

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
				callback: function (result) {
					if (result) {
						window.location.href = deleteUrl;
					}
				}
			});
		});
	}

});
