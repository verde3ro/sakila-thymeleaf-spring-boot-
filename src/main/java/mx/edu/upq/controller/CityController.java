package mx.edu.upq.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.edu.upq.exception.InternalException;
import mx.edu.upq.request.CityRequest;
import mx.edu.upq.response.CityResponse;
import mx.edu.upq.service.ICityService;
import mx.edu.upq.service.ICountryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;


@Controller
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {

	private final ICityService cityService;
	private final ICountryService countryService;

	/**
	 * Lista de ciudades con paginación y ordenamiento.
	 * Acceso permitido a USER y ADMIN.
	 */
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping
	public String list(Model model,
	                   @RequestParam(defaultValue = "0") int page,
	                   @RequestParam(defaultValue = "10") int size,
	                   @RequestParam(defaultValue = "cityId") String sortField,
	                   @RequestParam(defaultValue = "asc") String sortOrder) {

		Page<CityResponse> cityPage = cityService.findAllPagination(page, size, sortField, sortOrder);

		model.addAttribute("cities", cityPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", cityPage.getTotalPages());
		model.addAttribute("totalItems", cityPage.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortOrder", sortOrder);
		model.addAttribute("reverseSortOrder", sortOrder.equals("asc") ? "desc" : "asc");
		model.addAttribute("size", size);

		return "cities/list";
	}

	/**
	 * Muestra formulario para nueva ciudad.
	 * Solo ADMIN.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("cityRequest", new CityRequest());
		model.addAttribute("countries", countryService.findAll());
		return "cities/form";
	}

	/**
	 * Procesa el guardado (creación o edición) de una ciudad.
	 * Solo ADMIN.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public String save(@Valid @ModelAttribute("cityRequest") CityRequest request,
	                   BindingResult result,
	                   Model model,
	                   RedirectAttributes redirectAttributes) {
		// Si hay errores de validación, regresar al formulario con los países
		if (result.hasErrors()) {
			model.addAttribute("countries", countryService.findAll());
			return "cities/form";
		}

		try {
			if (request.getCityId() == null) {
				cityService.create(request);
				redirectAttributes.addFlashAttribute("successMessage", "Ciudad creada exitosamente");
			} else {
				cityService.update(request);
				redirectAttributes.addFlashAttribute("successMessage", "Ciudad actualizada exitosamente");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar: " + e.getMessage());
		}

		return "redirect:/cities";
	}

	/**
	 * Carga el formulario de edición con los datos existentes.
	 * Solo ADMIN.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Short id, Model model, RedirectAttributes redirectAttributes) {
		try {
			CityResponse response = cityService.findById(id);
			// Convertir CityResponse a CityRequest para el formulario
			CityRequest request = new CityRequest();
			request.setCityId((short) response.getCityId());
			request.setCity(response.getCity());
			request.setLastUpdate(response.getLastUpdate());
			request.setCountryId((short) response.getCountryId());

			model.addAttribute("cityRequest", request);
			model.addAttribute("countries", countryService.findAll());
			return "cities/form";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", String.format("Ciudad no encontrada, %s", e.getMessage()));
			return "redirect:/cities";
		}
	}

	/**
	 * Elimina una ciudad por ID.
	 * Solo ADMIN.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Short id, RedirectAttributes redirectAttributes) {
		try {
			cityService.delete(id);
			redirectAttributes.addFlashAttribute("successMessage", "Ciudad eliminada");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", String.format("No se pudo eliminar la ciudad, %s", e.getMessage()));
		}
		return "redirect:/cities";
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/download/excel")
	public ResponseEntity<byte[]> downloadExcel() {
		try {
			String base64 = cityService.generateExcel();
			byte[] excelBytes = Base64.getDecoder().decode(base64);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDisposition(ContentDisposition.attachment()
					.filename("ciudades.xlsx")
					.build());
			headers.setContentLength(excelBytes.length);

			return ResponseEntity.ok()
					.headers(headers)
					.body(excelBytes);
		} catch (Exception e) {
			throw new InternalException("Error al generar el archivo Excel: " + e.getMessage());
		}
	}

}
