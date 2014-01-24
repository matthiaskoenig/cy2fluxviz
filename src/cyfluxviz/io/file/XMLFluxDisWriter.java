package cyfluxviz.io.file;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cyfluxviz.FluxDis;

public class XMLFluxDisWriter extends AbstractFluxDisWriter{

	public XMLFluxDisWriter(String fileName) {
		super(fileName);
	}
	
	public XMLFluxDisWriter(File file) {
		super(file);
	}

	@Override
	public void write(Collection<FluxDis> fds){
		writeXMLFile(fds);
	}
	
	/** Write the XMLFile. */
	private void writeXMLFile(Collection<FluxDis> fdCollection){
		Document doc = createXMLDocument(fdCollection);
		try {
			
			// Generate transformer and set transformer properties
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			// Generate XML
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			// Output to console for testing
			// StreamResult resultConsole = new StreamResult(System.out);
			transformer.transform(source, result);
			
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	/** Create XML Dom for FluxDistributions. */
	private static Document createXMLDocument(Collection<FluxDis> fds){
		Document doc = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// Create the root node
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(XMLValidator.FD_COLLECTION);
			doc.appendChild(rootElement);
			
			Element fdListNode = doc.createElement(XMLValidator.FD_LIST);
			rootElement.appendChild(fdListNode);
			
			// Add the single FluxDistributions
			for (FluxDis fd: fds){
				Element fdNode = createFluxDisNode(doc, fd);
				fdListNode.appendChild(fdNode);
			}
			
		} catch (ParserConfigurationException e) {
			doc = null;
			e.printStackTrace();
		}
		return doc;
	}
	
	
	/** Creates the DOM node for a single FluxDistribution. 
	 * TODO: handle the time issue, testing of exporter. */
	private static Element createFluxDisNode(Document doc, FluxDis fd){
		Element fdNode = doc.createElement(XMLValidator.FD);
		fdNode.setAttribute(XMLValidator.FD_ID, fd.getId().toString());
		fdNode.setAttribute(XMLValidator.FD_NAME, fd.getName());
		fdNode.setAttribute(XMLValidator.FD_NETWORK_ID, fd.getNetworkId());
		
		// Write node fluxes
		Element fluxListElement = doc.createElement(XMLValidator.FLUX_LIST);
		fdNode.appendChild(fluxListElement);
		
		HashMap<String, Double> nodeFluxes = fd.getNodeFluxes();
		for (String nodeId : nodeFluxes.keySet()){
			Element nodeFlux = doc.createElement(XMLValidator.FLUX);
			nodeFlux.setAttribute(XMLValidator.FLUX_ID, nodeId);
			nodeFlux.setAttribute(XMLValidator.FLUX_VALUE, nodeFluxes.get(nodeId).toString());
			nodeFlux.setAttribute(XMLValidator.FLUX_TYPE, XMLValidator.FLUX_TYPE_NODE);
			fluxListElement.appendChild(nodeFlux);
		}
		
		// Write node concentrations
		Element concentrationListElement = doc.createElement(XMLValidator.CONCENTRATION_LIST);
		fdNode.appendChild(concentrationListElement);
		
		HashMap<String, Double> nodeConcentrations = fd.getNodeConcentrations();
		for (String nodeId : nodeConcentrations.keySet()){
			Element nodeC = doc.createElement(XMLValidator.CONCENTRATION);
			nodeC.setAttribute(XMLValidator.CONCENTRATION_ID, nodeId);
			nodeC.setAttribute(XMLValidator.CONCENTRATION_VALUE, nodeConcentrations.get(nodeId).toString());
			nodeC.setAttribute(XMLValidator.CONCENTRATION_TYPE, XMLValidator.CONCENTRATION_TYPE_NODE);
			concentrationListElement.appendChild(nodeC);
		}
		return fdNode;
	}
	
}
