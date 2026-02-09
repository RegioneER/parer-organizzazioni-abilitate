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

/**
 *
 */
package it.eng.parer.org.abilitate.runner.rest.input;

import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import it.eng.parer.org.abilitate.beans.utils.Costants.AppNameEnum;
import it.eng.parer.org.abilitate.beans.validator.AppNameValidator.ValidAppName;
import jakarta.ws.rs.QueryParam;

/* custom validator */
@ValidAppName
public class AppNameQuery {

    @Parameter(name = "appName", description = "nome applicazione (SACER / SACER_PREINGEST) ", allowEmptyValue = true, example = "SACER", schema = @Schema(type = SchemaType.STRING, implementation = AppNameEnum.class), required = false, in = ParameterIn.QUERY)
    @QueryParam("appName")
    public String appName = AppNameEnum.ANY.name(); // default value

}
