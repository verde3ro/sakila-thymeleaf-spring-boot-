package mx.edu.upq.mapper;

import mx.edu.upq.model.City;
import mx.edu.upq.request.CityRequest;
import mx.edu.upq.response.CityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper para convertir entre entidad City, CityRequest y CityResponse.
 * Se aprovecha MapStruct para evitar código manual.
 */
@Mapper(componentModel = "spring")
public interface ICityMapper {

	// City → CityResponse (mapea el nombre del país desde la relación)
	@Mapping(source = "country.countryId", target = "countryId")
	@Mapping(source = "country.country", target = "countryName")
	CityResponse toResponse(City city);

	// CityRequest → City (ignoramos country porque se asigna manualmente en el servicio)
	@Mapping(target = "country", ignore = true)
	@Mapping(target = "lastUpdate", ignore = true)
	// Se establece en el servicio
	City toEntity(CityRequest request);

	// Actualiza una entidad existente a partir del request
	@Mapping(target = "cityId", ignore = true)
	@Mapping(target = "country", ignore = true)
	@Mapping(target = "lastUpdate", ignore = true)
	void updateEntityFromRequest(CityRequest request, @MappingTarget City city);

}
