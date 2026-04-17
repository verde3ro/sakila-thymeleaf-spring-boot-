package mx.edu.upq.service;

import mx.edu.upq.request.CityRequest;
import mx.edu.upq.response.CityResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICityService {
	/**
	 * Obtiene todas las ciudades sin paginación.
	 * @return lista de CityResponse
	 */
	List<CityResponse> findAll();

	Page<CityResponse> findAllPagination(int page, int size, String sortField, String sortOrder);

	CityResponse findById(short id);

	void create(CityRequest request);

	void update(CityRequest request);

	void delete(short id);

	String generateExcel();

}
