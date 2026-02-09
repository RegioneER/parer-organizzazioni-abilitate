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
package it.eng.parer.org.abilitate.beans.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.eng.parer.org.abilitate.beans.dto.OrganizationDto;

@JsonInclude(Include.NON_NULL)
public class OrganizationResponse {

    // multi request
    private String path;
    private Integer totale;
    private List<OrganizationDto> organizzazioni;

    public OrganizationResponse() {
	super();
    }

    public OrganizationResponse(List<OrganizationDto> organizzazioni, Integer totaleOrganiz,
	    String uri) {
	super();
	this.organizzazioni = organizzazioni;
	this.totale = totaleOrganiz;
	this.path = uri;
    }

    public String getPath() {
	return path;
    }

    public Integer getTotale() {
	return totale;
    }

    public List<OrganizationDto> getOrganizzazioni() {
	return organizzazioni;
    }

}
