package gov.usgs.cida.pubs.openapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.CONTRIBUTING_OFFICE,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.CONTRIBUTOR,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.DOI,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.END_YEAR,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.HAS_DOI,
		description = "",
		schema = @Schema(type = "boolean")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.INDEX_ID,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.IPDS_ID,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = MpPublicationDao.LIST_ID,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PubsConstantsHelper.CONTENT_PARAMETER_NAME,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PersonContributorDao.ORCID,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.ORDER_BY,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = BaseDao.PAGE_NUMBER,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = BaseDao.PAGE_ROW_START,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = BaseDao.PAGE_SIZE,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.PROD_ID,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.PUB_ABSTRACT,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.Q,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.REPORT_NUMBER,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.SERIES_NAME,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.START_YEAR,
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.SUBTYPE_NAME,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.TITLE,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.TYPE_NAME,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = PublicationDao.YEAR,
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
public @interface PublicationFilterParameters {
}
