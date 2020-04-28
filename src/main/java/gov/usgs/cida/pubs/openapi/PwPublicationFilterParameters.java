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
		name = "hasDoi",
		description = "",
		schema = @Schema(type = "boolean")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "g",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "linkType",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "noLinkType",
		description = "",
		array = @ArraySchema(schema = @Schema(type = "string"))
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "pubXDays",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "pubDateLow",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "pubDateHigh",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "modXDays",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "modDateLow",
		description = "",
		schema = @Schema(type = "string")
		)
@Parameter(
		in = ParameterIn.QUERY,
		name = "modDateHigh",
		description = "",
		schema = @Schema(type = "string")
		)
public @interface PwPublicationFilterParameters {
}
