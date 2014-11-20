package org.wso2.carbon.event.sink.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.event.sink.config.services.utils.CryptographyManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.ServerConstants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * Created by vijithae on 11/17/14.
 */
public class EventSinkXmlWriter {
	private static final Log log = LogFactory.getLog(EventSinkXmlWriter.class);

	/**
	 * Obtain corresponding tenant Event Sink deployment directory path
	 *
	 * @return directorypath
	 */
	public String getTenantDeployementDirectoryPath() {
		String filePath = "";
		int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
		String tenantFilePath = CarbonUtils.getCarbonTenantsDirPath();
		if (tenantId > 0 && !(tenantId == -1234)) {
			filePath = tenantFilePath + File.separator + tenantId + File.separator + "event-sinks";
		} else if (tenantId == -1234) {
			String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
			filePath = carbonHome + File.separator + "repository" + File.separator + "deployment" + File.separator +
			           "server" + File.separator + "event-sinks";
		}

		return filePath;
	}

	/**
	 * Writes given Event Sink details to xml file
	 *
	 * @param eventSink the Event Sink to be write
	 */
	public void writeEventSink(EventSink eventSink) {
		String filePath = "";
		filePath = this.getTenantDeployementDirectoryPath();
		this.createEventSinkDirectory(filePath);
		EventSinkConfigXml eventSinkConfigXml = new EventSinkConfigXml();
		try {
			BufferedWriter bufferedWriter =
					new BufferedWriter(new FileWriter(new File(filePath, eventSink.getName() + ".xml")));
			String unFormattedXml = eventSinkConfigXml
					.buildEventSink(eventSink.getUsername(), encryptAndBase64Encode(eventSink.getPassword()),
					                eventSink.getReceiverUrl(), eventSink.getAuthenticatorUrl()).toString();

			///formatting xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(unFormattedXml));
			final Document document = db.parse(is);
			OutputFormat format = new OutputFormat(document);
			format.setLineWidth(100);
			format.setIndenting(true);
			format.setIndent(4);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);
			///

			bufferedWriter.write(out.toString());
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (FileNotFoundException e) {
			log.error("Failed to open file to write event sink. File: " + filePath + ", error: " +
			          e.getLocalizedMessage());
		} catch (IOException e) {
			log.error("Failed to write event sink to file. File: " + filePath + ", error: " + e.getLocalizedMessage());
		} catch (ParserConfigurationException e) {
			log.error("Internal error occurred while writing event sink. Failed to format XML. error: " +
			          e.getLocalizedMessage());
		} catch (SAXException e) {
			log.error(
					"Internal error occurred while writing event sink. Invalid XML. error: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Updates given Event Sink details
	 *
	 * @param eventSink the Event Sink to be updated
	 */
	public void updateEventSink(EventSink eventSink) {
		String filePath = "";
		filePath = this.getTenantDeployementDirectoryPath();
		File eventSinkFile = new File(filePath + File.separator + eventSink.getName() + ".xml");
		if (eventSinkFile.exists()) {
			try {
				eventSinkFile.delete();
				writeEventSink(eventSink);
			} catch (Exception e) {
				log.error("Error occured while updating event-sink xmml file");
			}
		}
	}

	private void createEventSinkDirectory(String filePath) {
		File eventSinksDir = new File(filePath);

		// if the directory does not exist, create it
		if (!eventSinksDir.exists()) {
			try {
				eventSinksDir.mkdir();
			} catch (Exception e) {
				log.error("Error occured while creating event-sinks directory");
			}
		}
	}

	/**
	 * Encrypts and encodes a string
	 *
	 * @param plainText the String to be converted
	 * @return Encrypted and endoed String
	 */
	public String encryptAndBase64Encode(String plainText) {
		CryptographyManager cryptographyManager = new CryptographyManager();
		return cryptographyManager.encryptAndBase64Encode(plainText);
	}
}