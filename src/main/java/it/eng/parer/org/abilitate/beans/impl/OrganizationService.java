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
package it.eng.parer.org.abilitate.beans.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.org.abilitate.beans.IOrganizationDao;
import it.eng.parer.org.abilitate.beans.IOrganizationService;
import it.eng.parer.org.abilitate.beans.dto.AmbienteDto;
import it.eng.parer.org.abilitate.beans.dto.EnteDto;
import it.eng.parer.org.abilitate.beans.dto.OrganizationDto;
import it.eng.parer.org.abilitate.beans.dto.StrutturaDto;
import it.eng.parer.org.abilitate.beans.dto.VersatoreDto;
import it.eng.parer.org.abilitate.beans.exceptions.AppGenericRuntimeException;
import it.eng.parer.org.abilitate.beans.exceptions.ErrorCategory;
import it.eng.parer.org.abilitate.beans.model.OrganizationResponse;
import it.eng.parer.org.abilitate.beans.utils.Costants.AppNameEnum;
import it.eng.parer.org.abilitate.beans.utils.Costants.OrganizEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class OrganizationService implements IOrganizationService {

    private static final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    @Inject
    IOrganizationDao orgDao;

    @Override
    @Transactional(value = TxType.REQUIRED, rollbackOn = {
	    AppGenericRuntimeException.class })
    public OrganizationResponse listOrgsByAppName(String nmUserid, AppNameEnum appName,
	    String uri) {
	try {
	    // Inizializzo le informazioni da restituire
	    List<OrganizationDto> dto = new ArrayList<>();
	    Integer numLastLevelOrg = 0;

	    switch (appName) {
	    case SACER: {
		numLastLevelOrg = elabSacerOrgs(nmUserid, dto);
		break;
	    }
	    case SACER_PREINGEST: {
		numLastLevelOrg = elabSacerPreingestOrgs(nmUserid, dto);
		break;
	    }
	    default:
		// SACER
		int numLastLevelOrgSacer = elabSacerOrgs(nmUserid, dto);
		// SACER_PREINGEST
		int numLastLevelOrgPing = elabSacerPreingestOrgs(nmUserid, dto);
		// totale
		numLastLevelOrg = numLastLevelOrgSacer + numLastLevelOrgPing;
	    }

	    log.atInfo().log("OrganizzazioniAbilitate - Recuperate {} organizzazioni",
		    numLastLevelOrg);
	    // Ritorna la response
	    return new OrganizationResponse(dto, numLastLevelOrg, uri);

	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().category(ErrorCategory.INTERNAL_ERROR)
		    .cause(e)
		    .message(
			    "Errore estrazione lista organizzazioni per nmUserid {0} e appName {1}",
			    nmUserid, appName)
		    .build();
	}
    }

    /**
     * Generazione risultati organizzazioni per appname = SACER_PREINGEST
     *
     * @param nmUserid        id utente che invoca il servizio
     * @param dto             organizzazioni {@link OrganizationDto}
     * @param numLastLevelOrg totale risultati
     *
     * @return
     */
    private Integer elabSacerOrgs(String nmUserid, List<OrganizationDto> dto) {
	log.atInfo().log("OrganizzazioniAbilitate - Recupero le strutture SACER");
	// Utilizzo una treemap per sfruttare l'ordinamento naturale
	Map<String, Map<String, List<String>>> mappaSacer = new TreeMap<>(
		String.CASE_INSENSITIVE_ORDER);
	// Recupero le organizzazioni di ULTIMO LIVELLO SACER (strutture)
	Stream<Object[]> result = orgDao.findLastLevelOrgs(nmUserid, OrganizEnum.STRUTTURA);
	// Le "organizzo" in oggetti Map e ricavo il numero di strutture
	int totale = getSacerOrganizationsMap(result, mappaSacer);
	// Passaggio jpa -> dto
	dto.add(populateSacerOrganizations(mappaSacer));
	return totale;
    }

    /**
     * Generazione risultati organizzazioni per appname = SACER
     *
     * @param nmUserid        id utente che invoca il servizio
     * @param dto             organizzazioni {@link OrganizationDto}
     * @param numLastLevelOrg totale risultati
     *
     * @return
     */
    private Integer elabSacerPreingestOrgs(String nmUserid, List<OrganizationDto> dto) {
	log.atInfo().log("OrganizzazioniAbilitate - Recupero i versatori PING");
	// Utilizzo una treemap per sfruttare l'ordinamento naturale
	Map<String, List<String>> mappaPing = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	// Recupero le organizzazioni di ULTIMO LIVELLO PING (versatori)
	Stream<Object[]> result = orgDao.findLastLevelOrgs(nmUserid, OrganizEnum.VERSATORE);
	// Le "organizzo" in oggetti Map e ricavo il numero di versatori
	int totale = getPingOrganizationsMap(result, mappaPing);
	// Passaggio jpa -> dto
	dto.add(populatePingOrganizations(mappaPing));
	return totale;
    }

    /**
     * Data una lista di oggetti rappresentanti le organizzazioni SACER di ultimo livello, ne
     * restituisce una mappa e il numero
     *
     * @param struttureAsStream lista delle strutture cui l'utente è abilitato
     *
     * @param mappaSacer        mappa delle organizzazioni
     *
     * @return totale
     */
    private int getSacerOrganizationsMap(Stream<Object[]> struttureAsStream,
	    Map<String, Map<String, List<String>>> mappaSacer) {
	AtomicInteger numLastLevelOrg = new AtomicInteger();
	// forEach
	struttureAsStream.forEach(struttura -> {
	    //
	    String nmStrut = (String) struttura[0];
	    String nmEnte = (String) struttura[1];
	    String nmAmbiente = (String) struttura[2];

	    // Se la mappa globale contiene l'ambiente trattato, recupera la sua mappa enti,
	    // altrimenti creane una nuova
	    Map<String, List<String>> mappaEnte = mappaSacer.compute(nmAmbiente,
		    (key, value) -> value != null ? mappaSacer.get(nmAmbiente)
			    : new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
	    // Se la mappa enti contiene l'ente trattato, recupera la sua lista strutture,
	    // altrimenti creane una nuova
	    List<String> listaStruttura = mappaEnte.compute(nmEnte,
		    (key, value) -> value != null ? mappaEnte.get(nmEnte) : new ArrayList<>());

	    // Aggiunto la struttura all'ente e poi l'ente alla mappa degli enti
	    listaStruttura.add(nmStrut);
	    // Inrementa contatore
	    numLastLevelOrg.incrementAndGet();
	    //
	    mappaEnte.put(nmEnte, listaStruttura);
	    mappaSacer.put(nmAmbiente, mappaEnte);
	});

	return numLastLevelOrg.get();
    }

    /**
     * Converte la mappa delle organizzazioni SACER in oggetti DTO
     *
     * @param mappaAmbiente mappa gerarchica delle organizzazioni sacer abilitate
     *
     * @return oggetto DTO contentente le organizzazioni sacer abilitate
     */
    private OrganizationDto populateSacerOrganizations(
	    Map<String, Map<String, List<String>>> mappaAmbiente) {
	List<AmbienteDto> listaAmbienteDto = new ArrayList<>();
	for (Map.Entry<String, Map<String, List<String>>> ambienteEntry : mappaAmbiente
		.entrySet()) {
	    String nmAmbiente = ambienteEntry.getKey();
	    Map<String, List<String>> mappaEnte = ambienteEntry.getValue();

	    List<EnteDto> listaEnteDto = new ArrayList<>();

	    for (Map.Entry<String, List<String>> enteEntry : mappaEnte.entrySet()) {
		// Converti la lista di String in lista di StrutturaDto
		List<StrutturaDto> listaStrutturaDto = getStrutturaDtoList(enteEntry.getValue());
		EnteDto enteDto = new EnteDto(enteEntry.getKey(), listaStrutturaDto);
		listaEnteDto.add(enteDto);
	    }

	    AmbienteDto ambienteDto = new AmbienteDto(nmAmbiente, listaEnteDto, null);

	    listaAmbienteDto.add(ambienteDto);
	}
	return new OrganizationDto(AppNameEnum.SACER.name(), listaAmbienteDto);

    }

    private List<StrutturaDto> getStrutturaDtoList(List<String> strutture) {
	List<StrutturaDto> strutturaDtoList = new ArrayList<>();
	for (String struttura : strutture) {
	    StrutturaDto strutturaDto = new StrutturaDto(struttura);
	    strutturaDtoList.add(strutturaDto);
	}
	return strutturaDtoList;
    }

    /**
     * Data una lista di oggetti rappresentanti le organizzazioni PING di ultimo livello, ne
     * restituisce una mappa e il numero
     *
     * @param versatoriObjList lista dei versatori cui l'utente è abilitato
     * @param mappaPing        mappa delle strutture
     *
     * @return totale
     */
    private int getPingOrganizationsMap(Stream<Object[]> versatoriObjList,
	    Map<String, List<String>> mappaPing) {
	AtomicInteger numLastLevelOrg = new AtomicInteger();
	// forEach
	versatoriObjList.forEach(versatore -> {
	    //
	    String nmVersatore = (String) versatore[0];
	    String nmAmbienteVersatore = (String) versatore[1];
	    List<String> listaVersatori = new ArrayList<>();

	    if (mappaPing.containsKey(nmAmbienteVersatore)) {
		listaVersatori = mappaPing.get(nmAmbienteVersatore);
	    }

	    listaVersatori.add(nmVersatore);
	    numLastLevelOrg.incrementAndGet();
	    mappaPing.put(nmAmbienteVersatore, listaVersatori);
	});
	return numLastLevelOrg.get();
    }

    /**
     * Converte la mappa delle organizzazioni PING in oggetti DTO
     *
     * @param mappaAmbientiVersatori mappa gerarchica delle organizzazioni ping abilitate
     *
     * @return oggetto DTO contentente le organizzazioni ping abilitate
     */
    private OrganizationDto populatePingOrganizations(
	    Map<String, List<String>> mappaAmbientiVersatori) {
	List<AmbienteDto> listaAmbienteVersatoreDto = new ArrayList<>();
	for (Map.Entry<String, List<String>> ambienteVersatoreEntry : mappaAmbientiVersatori
		.entrySet()) {
	    String nmAmbienteVersatore = ambienteVersatoreEntry.getKey();
	    List<String> listaVersatore = ambienteVersatoreEntry.getValue();

	    // Converti la lista di String in lista di VersatoreDto
	    List<VersatoreDto> listaVersatoreDto = getVersatoreDtoList(listaVersatore);
	    AmbienteDto ambienteVersatoreDto = new AmbienteDto(nmAmbienteVersatore, null,
		    listaVersatoreDto);
	    listaAmbienteVersatoreDto.add(ambienteVersatoreDto);
	}
	return new OrganizationDto(AppNameEnum.SACER_PREINGEST.name(), listaAmbienteVersatoreDto);
    }

    private List<VersatoreDto> getVersatoreDtoList(List<String> versatori) {
	List<VersatoreDto> versatoreDtoList = new ArrayList<>();
	for (String versatore : versatori) {
	    VersatoreDto versatoreDto = new VersatoreDto(versatore);
	    versatoreDtoList.add(versatoreDto);
	}
	return versatoreDtoList;
    }

}
