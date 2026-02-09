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

package it.eng.parer.org.abilitate.beans.impl;

import static it.eng.parer.org.abilitate.beans.exceptions.ErrorCategory.INTERNAL_ERROR;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.org.abilitate.Profiles;
import it.eng.parer.org.abilitate.beans.IOrganizationService;
import it.eng.parer.org.abilitate.beans.exceptions.AppGenericRuntimeException;
import it.eng.parer.org.abilitate.beans.model.OrganizationResponse;
import it.eng.parer.org.abilitate.beans.utils.Costants.AppNameEnum;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

@QuarkusTest
@TestProfile(Profiles.Core.class)
class OrganizationServiceTest {

    static final String USERID = "test_microservizi"; // userid locale al db snap/test

    @Inject
    IOrganizationService service;

    @Test
    void listOrgsAny_ok() {
	OrganizationResponse result = assertDoesNotThrow(
		() -> service.listOrgsByAppName(USERID, AppNameEnum.ANY, StringUtils.EMPTY));
	assertTrue(!result.getOrganizzazioni().isEmpty());
    }

    @Test
    void listOrgsSacer_ok() {
	OrganizationResponse result = assertDoesNotThrow(
		() -> service.listOrgsByAppName(USERID, AppNameEnum.SACER, StringUtils.EMPTY));
	assertTrue(!result.getOrganizzazioni().isEmpty());
    }

    @Test
    void listOrgsPreingest_ok() {
	OrganizationResponse result = assertDoesNotThrow(() -> service.listOrgsByAppName(USERID,
		AppNameEnum.SACER_PREINGEST, StringUtils.EMPTY));
	assertTrue(!result.getOrganizzazioni().isEmpty());
    }

    @Test
    void listOrgsNullAppName_ko() {
	AppGenericRuntimeException exe = assertThrows(AppGenericRuntimeException.class,
		() -> service.listOrgsByAppName(USERID, null, StringUtils.EMPTY));
	assertEquals(INTERNAL_ERROR, exe.getCategory());
	assertInstanceOf(NullPointerException.class, exe.getCause());

    }

    @Test
    void listOrgsEmptyUserid_ko() {
	assertThrows(ConstraintViolationException.class, () -> service
		.listOrgsByAppName(StringUtils.EMPTY, AppNameEnum.ANY, StringUtils.EMPTY));
    }

}
