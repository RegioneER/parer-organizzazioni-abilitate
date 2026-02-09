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

import static io.restassured.RestAssured.given;
import static it.eng.parer.org.abilitate.beans.utils.Costants.COD_ERR_INTERNAL;
import static it.eng.parer.org.abilitate.beans.utils.Costants.URL_GET_ORGANIZATIONS;
import static org.hamcrest.Matchers.hasKey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import it.eng.parer.org.abilitate.Profiles;
import it.eng.parer.org.abilitate.beans.IOrganizationService;
import it.eng.parer.org.abilitate.beans.exceptions.AppGenericRuntimeException;
import it.eng.parer.org.abilitate.beans.exceptions.ErrorCategory;

@QuarkusTest
@TestProfile(Profiles.EndToEnd.class)
class OrgAbilitateEndpointExceptionTest {

    @InjectMock
    IOrganizationService serviceMock;

    @Test
    @TestSecurity(user = "test_microservizi", roles = { "versatore" })
    void errorRequest() {
	when(serviceMock.listOrgsByAppName(any(), any(), any())).thenThrow(appGenericRuntimeException());
	given().when().get(URL_GET_ORGANIZATIONS).then().statusCode(500).body("$", hasKey(COD_ERR_INTERNAL));
    }

    private AppGenericRuntimeException appGenericRuntimeException() {
	return AppGenericRuntimeException.builder().category(ErrorCategory.INTERNAL_ERROR)
		.message("Errore generico").build();
    }
}
