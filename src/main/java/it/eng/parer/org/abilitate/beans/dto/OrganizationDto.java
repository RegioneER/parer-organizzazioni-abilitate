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

package it.eng.parer.org.abilitate.beans.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class OrganizationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String applicazione;
    private List<AmbienteDto> ambienti;

    public OrganizationDto() {
	super();
    }

    public OrganizationDto(String applicazione, List<AmbienteDto> ambienti) {
	super();
	this.applicazione = applicazione;
	this.ambienti = ambienti;
    }

    public String getApplicazione() {
	return applicazione;
    }

    public List<AmbienteDto> getAmbienti() {
	return ambienti;
    }

}
