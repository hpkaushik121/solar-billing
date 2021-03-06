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

package com.sapienter.jbilling.client.user;

import java.io.IOException;
import java.util.logging.Level;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.sapienter.jbilling.server.user.IUserSessionBean;
import com.sapienter.jbilling.server.notification.NotificationNotFoundException;
import com.sapienter.jbilling.server.util.Context;

public class ForgetPasswordAction extends Action {
	// Actionmethod for processing "forget password" operation
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	Logger log = Logger.getLogger(ForgetPasswordAction.class);
    	ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
    	
    	UserLoginForm info = (UserLoginForm) form;
        String username = info.getUserName().trim();
        final int entityId = Integer.parseInt(info.getEntityId().trim());
    	
        log.debug("Received ForgetPasswordAction Request with userName " + 
        		username + " entityId " + entityId);
        
        // now do the call to the business object
        // get the value from a Session EJB 
        try {
            IUserSessionBean myRemoteSession = (IUserSessionBean) 
                    Context.getBean(Context.Name.USER_SESSION);
            try {
                myRemoteSession.sendLostPassword(entityId, username);
            } catch( EntityNotFoundException enfe) {
            	log.debug(enfe);
            }
    	} catch( NotificationNotFoundException e) {
    		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "forgetPassword.errors.notificationnotactivated"));
    	} catch (RuntimeException rte) {
    		java.util.logging.Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
    			"error handling forgotten password for user " + username + ", company " + entityId, rte);
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("all.internal"));
        }
    	
    	if (!errors.isEmpty()) {
    		saveErrors(request, errors);
            return (new ActionForward(mapping.getInput()));
    	} else {
            messages.add(ActionMessages.GLOBAL_MESSAGE, 
                    new ActionMessage("forgetPassword.ok"));
            saveMessages(request, messages);
        }
    	
    	return mapping.findForward("success");
    }
}
