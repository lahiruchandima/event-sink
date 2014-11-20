/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.event.sink.config;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.wso2.carbon.event.sink.EventSinkConstants;

/**
 * Creates the XML string to be stored in the Registry
 */
public class EventSinkConfigXml {

    private org.apache.axiom.om.OMFactory fac = OMAbstractFactory.getOMFactory();


    public OMElement buildEventSink(String username, String password, String receiverUrl, String authenticatorUrl) {
        OMElement eventSinkElement = fac.createOMElement(EventSinkConstants.EVENT_SINK_Q);

        OMElement receiverUrlElement = fac.createOMElement(EventSinkConstants.RECEIVER_URL_Q);
        receiverUrlElement.setText(receiverUrl);
        eventSinkElement.addChild(receiverUrlElement);

        OMElement authenticatorUrlElement = fac.createOMElement(EventSinkConstants.AUTHENTICATOR_URL_Q);
        authenticatorUrlElement.setText(authenticatorUrl);
        eventSinkElement.addChild(authenticatorUrlElement);

        OMElement usernameElement = fac.createOMElement(EventSinkConstants.USERNAME_Q);
        usernameElement.setText(username);
        eventSinkElement.addChild(usernameElement);

        OMElement passwordElement = fac.createOMElement(EventSinkConstants.PASSWORD_Q);
        passwordElement.setText(password);
        eventSinkElement.addChild(passwordElement);

        return eventSinkElement;
    }
}