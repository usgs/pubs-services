package gov.usgs.cida.pubs.openapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.HAS_DOI,
		description = "",
		schema = @Schema(type = "boolean")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PwPublicationDao.G,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.LINK_TYPE,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.NO_LINK_TYPE,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PwPublicationDao.PUB_X_DAYS,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PwPublicationDao.PUB_DATE_LOW,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PwPublicationDao.PUB_DATE_HIGH,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PwPublicationDao.MOD_X_DAYS,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PwPublicationDao.MOD_DATE_LOW,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PwPublicationDao.MOD_DATE_HIGH,
		description = "",
		schema = @Schema(type = "string")
		)
public @interface PwPublicationFilterParameters {
}
