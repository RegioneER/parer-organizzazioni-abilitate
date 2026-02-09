/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.org.abilitate.runner.rest;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.smallrye.common.annotation.Blocking;
import io.vertx.core.http.HttpServerRequest;
import it.eng.parer.org.abilitate.beans.IOrganizationService;
import it.eng.parer.org.abilitate.beans.exceptions.AppGenericRuntimeException;
import it.eng.parer.org.abilitate.beans.model.OrganizationResponse;
import it.eng.parer.org.abilitate.beans.utils.Costants.AppNameEnum;
import static it.eng.parer.org.abilitate.beans.utils.Costants.RESOURCE_ORGANIZATIONS;
import static it.eng.parer.org.abilitate.beans.utils.Costants.URL_API_BASE;
import it.eng.parer.org.abilitate.runner.rest.input.AppNameQuery;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Tag(name = "Lista organizzazioni abilitate", description = "Api che restituisce la lista delle organzizzazioni abilitate per applicazione")
@SecurityScheme(securitySchemeName = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer")
@RequestScoped
@Path(URL_API_BASE)
public class OrgAbilitateEndpoint {

    /* constants */
    private static final String ETAG = "org-abilitate-v1.0";

    @ConfigProperty(name = "quarkus.uuid")
    String instanceUUID;

    /* interfaces */
    private final IOrganizationService orgService;
    private final SecurityContext securityCtx;

    @Inject
    public OrgAbilitateEndpoint(IOrganizationService orgService, SecurityContext securityCtx) {
	this.orgService = orgService;
	this.securityCtx = securityCtx;
    }

    @Operation(summary = "Lista organizzazioni abilitate con filtro per applicazione", description = "Lista organizzazioni abilitate con filtro per applicazione tramite query string (ANY/SACER/SACER_PREINGEST)")
    @SecurityRequirement(name = "bearerAuth")
    @APIResponses(value = {
	    @APIResponse(responseCode = "200", description = "Lista organizzazioni recuperato con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationResponse.class))),
	    @APIResponse(responseCode = "400", description = "Richiesta non valida"),
	    @APIResponse(responseCode = "401", description = "Autenticazione fallita"),
	    @APIResponse(responseCode = "403", description = "Non autorizzato ad accedere al servizio"),
	    @APIResponse(responseCode = "405", description = "Invocazione non corretta"),
	    @APIResponse(responseCode = "500", description = "Errore generico (richiesta non valida secondo specifiche)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = AppGenericRuntimeException.class))) })
    @GET
    @Path(RESOURCE_ORGANIZATIONS)
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public Response listorg(@BeanParam @Valid AppNameQuery appNameQuery,
	    @Context HttpServerRequest request) {
	// do something .....
	OrganizationResponse results = getOrgResponseFromDto(appNameQuery, request);
	//
	return Response.ok(results)
		.lastModified(
			Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
		.tag(new EntityTag(ETAG)).build();
    }

    private OrganizationResponse getOrgResponseFromDto(AppNameQuery appNameQuery,
	    HttpServerRequest request) {
	AppNameEnum appNameEnum = AppNameEnum.valueOf(appNameQuery.appName.toUpperCase());
	String uri = URLDecoder.decode(request.uri(), Charset.defaultCharset());
	return orgService.listOrgsByAppName(securityCtx.getUserPrincipal().getName(), appNameEnum,
		uri);
    }
}
