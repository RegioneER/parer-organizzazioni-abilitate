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

package it.eng.parer.org.abilitate.beans;

import java.util.stream.Stream;

import it.eng.parer.org.abilitate.beans.utils.Costants.OrganizEnum;

public interface IOrganizationDao {

    /**
     * Restituisce la stream con lista delle organizzazioni di ultimo livello abilitate
     *
     * @param nmUserid      utente che richiede l'elenco delle organizzazioni
     * @param nmTipoOrganiz il tipo di organizzazione di ultimo livello (struttura sacer o versatore
     *                      ping)
     *
     * @return un array di Object contenente i dati delle organizzazioni
     */
    Stream<Object[]> findLastLevelOrgs(String nmUserid, OrganizEnum nmTipoOrganiz);

}
