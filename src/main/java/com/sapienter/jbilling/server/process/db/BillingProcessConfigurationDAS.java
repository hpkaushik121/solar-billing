/*
    jBilling - The Enterprise Open Source Billing System
    Copyright (C) 2003-2009 Enterprise jBilling Software Ltd. and Emiliano Conde

    This file is part of jBilling.

    jBilling is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jBilling is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with jBilling.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.sapienter.jbilling.server.process.db;

import java.util.Date;

import com.google.common.collect.ImmutableMap;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

/**
 * 
 * @author abimael
 *
 */
public class BillingProcessConfigurationDAS extends AbstractDAS<BillingProcessConfigurationDTO> {

	public BillingProcessConfigurationDTO create(CompanyDTO entity,
			Date nextRunDate, Integer generateReport) {
		BillingProcessConfigurationDTO nuevo = new BillingProcessConfigurationDTO();
		nuevo.setEntity(entity);
		nuevo.setNextRunDate(nextRunDate);
		nuevo.setGenerateReport(generateReport);
		
		return save(nuevo);
	}

	public BillingProcessConfigurationDTO findByEntity(CompanyDTO entity) {
		return uniqueResult(BillingProcessConfigurationDTO.class, ImmutableMap.of("entity", entity));
	}

}
