package org.adempiere.mm.attributes.countryattribute.impl;

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


import org.adempiere.exceptions.AdempiereException;
import org.adempiere.mm.attributes.countryattribute.ICountryAware;
import org.adempiere.mm.attributes.countryattribute.ICountryAwareFactory;
import org.adempiere.model.InterfaceWrapperHelper;
import org.compiere.model.I_C_BPartner_Location;
import org.compiere.model.I_C_Country;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_OrderLine;

import de.metas.util.Check;

public class OrderLineCountryAware implements ICountryAware
{
	public static final ICountryAwareFactory factory = new ICountryAwareFactory()
	{
		@Override
		public ICountryAware createCountryAware(Object model)
		{
			final I_C_OrderLine orderLine = InterfaceWrapperHelper.create(model, I_C_OrderLine.class);
			final ICountryAware countryAware = new OrderLineCountryAware(orderLine);
			return countryAware;
		}
	};

	private final I_C_OrderLine orderLine;

	private OrderLineCountryAware(final I_C_OrderLine orderLine)
	{
		super();
		Check.assumeNotNull(orderLine, "Order line not null");
		this.orderLine = orderLine;
	}

	@Override
	public int getAD_Client_ID()
	{
		return orderLine.getAD_Client_ID();
	}

	@Override
	public int getAD_Org_ID()
	{
		return orderLine.getAD_Org_ID();
	}

	@Override
	public boolean isSOTrx()
	{
		final I_C_Order order = getOrder();
		return order.isSOTrx();
	}

	@Override
	public I_C_Country getC_Country()
	{
		final I_C_Order order = getOrder();

		final I_C_BPartner_Location bpLocation = order.getC_BPartner_Location();
		if (bpLocation == null)
		{
			return null;
		}
		return bpLocation.getC_Location().getC_Country();
	}

	private I_C_Order getOrder()
	{
		final I_C_Order order = orderLine.getC_Order();
		if (order == null)
		{
			throw new AdempiereException("Order not set for" + orderLine);
		}

		return order;
	}

}
