package gov.usgs.cida.pubs.openapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
		in = ParameterIn.QUERY,
		name = "contributingOffice",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "contributor",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "doi",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "endYear",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "hasDoi",
		description = "",
		schema = @Schema(type = "boolean")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "indexId",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "ipdsId",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "listId",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "mimeType",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "orcid",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "orderBy",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "page_number",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "page_row_start",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "page_size",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "prodId",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "pubAbstract",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "q",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "reportNumber",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "seriesName",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "startYear",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "subtypeName",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "title",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "typeName",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "year",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
public @interface PublicationFilterParameters {
}
