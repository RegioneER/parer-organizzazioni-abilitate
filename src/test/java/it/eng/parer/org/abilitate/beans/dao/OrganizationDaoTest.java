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

package it.eng.parer.org.abilitate.beans.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.org.abilitate.Profiles;
import it.eng.parer.org.abilitate.beans.IOrganizationDao;
import it.eng.parer.org.abilitate.beans.utils.Costants.OrganizEnum;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.Core.class)
class OrganizationDaoTest {

    static final String USERID = "test_microservizi"; // userid locale al db snap/test

    @Inject
    IOrganizationDao dao;

    @Test
    void findOrgsByAppNamePreingest_ok() {
	assertDoesNotThrow(() -> dao.findLastLevelOrgs(USERID, OrganizEnum.VERSATORE));
    }

    @Test
    void findOrgsByAppNameSacer_ok() {
	assertDoesNotThrow(() -> dao.findLastLevelOrgs(USERID, OrganizEnum.STRUTTURA));
    }

}
