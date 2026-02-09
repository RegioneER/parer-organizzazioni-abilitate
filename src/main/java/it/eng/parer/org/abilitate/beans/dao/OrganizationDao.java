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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.org.abilitate.beans.dao;

import java.util.stream.Stream;

import org.hibernate.jpa.HibernateHints;

import it.eng.parer.org.abilitate.beans.IOrganizationDao;
import it.eng.parer.org.abilitate.beans.utils.Costants.OrganizEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class OrganizationDao implements IOrganizationDao {

    private static final String FIND_ORG_QUERY = "select org.nmOrganiz, orgPadre.nmOrganiz, orgNonno.nmOrganiz "
	    + " from UsrOrganizIam org JOIN org.usrOrganizIamPadre orgPadre "
	    + " LEFT JOIN orgPadre.usrOrganizIamPadre orgNonno JOIN org.aplTipoOrganiz tipoOrganiz "
	    + " JOIN org.aplApplic applic JOIN org.usrAbilOrganizs abilOrganiz "
	    + " JOIN abilOrganiz.usrUsoUserApplic usoUserApplic JOIN usoUserApplic.usrUser utente "
	    + " where tipoOrganiz.nmTipoOrganiz = :nmTipoOrganiz and utente.nmUserid = :nmUserid "
	    + " order by upper(orgNonno.nmOrganiz), upper(orgPadre.nmOrganiz), upper(org.nmOrganiz) ";

    @Inject
    EntityManager entityManager;

    @Override
    public Stream<Object[]> findLastLevelOrgs(String nmUserid, OrganizEnum nmTipoOrganiz) {
	TypedQuery<Object[]> query = entityManager.createQuery(FIND_ORG_QUERY, Object[].class);

	// hibernate hint
	query.setHint(HibernateHints.HINT_READ_ONLY, true);
	query.setHint(HibernateHints.HINT_CACHEABLE, true);

	// params
	query.setParameter("nmUserid", nmUserid);
	query.setParameter("nmTipoOrganiz", nmTipoOrganiz.name());

	return query.getResultStream();
    }

}
