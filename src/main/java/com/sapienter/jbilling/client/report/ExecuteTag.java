/*
    jBilling - The Enterprise Open Source Billing System
    Copyright (C) 2003-2009 Enterprise jBilling Software Ltd. and Emiliano Conde

    This file is part of jbilling.

    jbilling is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jbilling is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.sapienter.jbilling.client.report;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.sql.rowset.CachedRowSet;

import com.sapienter.jbilling.client.list.ListTagBase;
import com.sapienter.jbilling.client.util.Constants;
import com.sapienter.jbilling.server.report.IReportSessionBean;
import com.sapienter.jbilling.server.report.ReportDTOEx;
import com.sapienter.jbilling.server.util.Context;

/**
 * Prepares the result set so the InsertDataRowTag can then render
 * the data in a table
 * 
 * @author emilc
 *
 * @jsp:tag name="reportExecute"
 *          body-content="JSP"
 */
public class ExecuteTag extends ListTagBase {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspException {
        int retValue = SKIP_BODY;

        log = Logger.getLogger(ExecuteTag.class);
        ActionMessages errors = new ActionMessages();

        try {
            // I'll get the entity id first from the session
            HttpSession session = pageContext.getSession();
            // the session is never null, even when it times out what it's deleted
            // is its contents
            ReportDTOEx report = (ReportDTOEx) session.getAttribute(
                    Constants.SESSION_REPORT_DTO);
            if (report == null) { //means that the session has timed out
                log.fatal(
                    "The session timed out, but the filter didn't catch it");
                throw new JspException("CustomerListTag: session timed out.");
            }
            
            CachedRowSet queryResults = (CachedRowSet) session.getAttribute(
                    Constants.SESSION_REPORT_RESULT);

            if (queryResults == null) {
                // Now I'll call the session bean to get the CachedRowSet with
                // the results of the query
                IReportSessionBean myRemoteSession = (IReportSessionBean) 
                        Context.getBean(Context.Name.REPORT_SESSION);
    
                queryResults = myRemoteSession.execute(report);
                
                session.setAttribute(Constants.SESSION_REPORT_RESULT, 
                        queryResults);
            } else {
                // let's rewind, this will change when the web-paging is done
                queryResults.beforeFirst();
            }
            // we leave the cursor ready to render data
            if (queryResults.next()) {
                retValue = EVAL_BODY_INCLUDE;
            }

        } catch (RuntimeException | SQLException e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, 
                    new ActionMessage("all.internal"));
            pageContext.getRequest().setAttribute(Globals.ERROR_KEY, 
                    errors);

            log.error("Exception at execute report tag", e);
        }

        return retValue;

    }

}
