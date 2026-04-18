package mx.edu.upq.rest;

import lombok.RequiredArgsConstructor;
import mx.edu.upq.response.CityResponse;
import mx.edu.upq.service.ICityService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityRestController {

	private final ICityService cityService;

	@GetMapping
	public Map<String, Object> getCities(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "cityId") String sort,
			@RequestParam(defaultValue = "asc") String order) {

		Page<CityResponse> cityPage = cityService.findAllPagination(page, size, sort, order);

		Map<String, Object> response = new HashMap<>();
		response.put("total", cityPage.getTotalElements());
		response.put("rows", cityPage.getContent());
		return response;
	}

}
