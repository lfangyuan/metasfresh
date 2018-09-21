package de.metas.adempiere.service;

/*
 * #%L
 * de.metas.swat.base
 * %%
 * Copyright (C) 2015 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */


import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import de.metas.adempiere.model.I_C_CountryArea;
import de.metas.adempiere.model.I_C_CountryArea_Assign;
import de.metas.util.ISingletonService;

public interface ICountryAreaBL extends ISingletonService
{
	String COUNTRYAREAKEY_EU = "EU";

	/**
	 * Checks if the given country is a member of a specific area at a certain date.
	 * 
	 * @param countryAreaKey Area key (e.g. "EU").
	 * @param countryCode Country ISO code (e.g. "DE")
	 */
	boolean isMemberOf(Properties ctx, String countryAreaKey, String countryCode, Timestamp date);

	/**
	 * @return country area assignments for given countryArea and countryId
	 */
	List<I_C_CountryArea_Assign> retrieveCountryAreaAssignments(I_C_CountryArea countryArea, int countryId);

	void validate(I_C_CountryArea_Assign assignment);
}
