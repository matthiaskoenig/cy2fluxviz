package cyfluxviz.io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.io.File;

import cyfluxviz.FluxDis;


public class XMLInterface {
	public static String FD_COLLECTION = "fluxDistributionCollection";
	public static String FD_LIST = "listOfFluxDistributions";
	
	public static String FD = "fluxDistribution";
	public static String FD_ID = "id";
	public static String FD_NAME = "name";
	public static String FD_NETWORK_ID = "networkId";
	
	public static String FLUX_LIST = "listOfFluxes";
	public static String FLUX = "flux";
	public static String FLUX_ID = "id";
	public static String FLUX_VALUE = "fluxValue";
	public static String FLUX_TYPE = "type";
	public static String FLUX_TYPE_NODE = "nodeFlux";
	

	// XML EXPORT //
	
	public static void writeXMLFileForFluxDistributions(String filename, Collection<FluxDis> fdCollection){
		Document doc = createXMLDocumentFromFluxDistributions(fdCollection);
		try {
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;

			transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filename));

			// Output to console for testing
			StreamResult resultConsole = new StreamResult(System.out);

			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	private static Document createXMLDocumentFromFluxDistributions(Collection<FluxDis> fdCollection){
		Document doc = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(FD_COLLECTION);
			doc.appendChild(rootElement);
			
			Element fdListNode = doc.createElement(FD_LIST);
			rootElement.appendChild(fdListNode);
			
			Set<FluxDis> fdSet = new HashSet<FluxDis>(fdCollection);
			for (FluxDis fd: fdSet){
				addDomForFluxDistribution(doc, fdListNode, fd);
			}
		} catch (ParserConfigurationException e) {
			doc = null;
			e.printStackTrace();
		}
		return doc;
	}
	
	private static void addDomForFluxDistribution(Document doc, Element fdListElement, FluxDis fd){
		Element fdNode = doc.createElement(FD);
		fdListElement.appendChild(fdNode);
		fdNode.setAttribute(FD_ID, fd.getId());
		fdNode.setAttribute(FD_NAME, fd.getName());
		fdNode.setAttribute(FD_NETWORK_ID, fd.getNetworkId());
		
		Element fluxListElement = doc.createElement(FLUX_LIST);
		fdNode.appendChild(fluxListElement);
		
		HashMap<String, Double> nodeFluxes = fd.getNodeFluxes();
		for (String nodeId : nodeFluxes.keySet()){
			Element nodeFlux = doc.createElement(FLUX);
			nodeFlux.setAttribute(FLUX_ID, nodeId);
			nodeFlux.setAttribute(FLUX_VALUE, nodeFluxes.get(nodeId).toString());
			nodeFlux.setAttribute(FLUX_TYPE, FLUX_TYPE_NODE);
			fluxListElement.appendChild(nodeFlux);
		}
	}
	
	// XML IMPORT //
	
	public static Collection<FluxDis> readFluxDistributionsFromXML(String filename){
		List<FluxDis> fdCollection = new LinkedList<FluxDis>();
		
		try {
			File xmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			NodeList fdList = doc.getElementsByTagName(FD);
			for (int k=0; k< fdList.getLength(); ++k){
				Node fdNode = fdList.item(k);
				FluxDis fd = readFluxDistributionFromNode(fdNode);
				fdCollection.add(fd); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fdCollection;
	}
	
	private static FluxDis readFluxDistributionFromNode(Node fdNode){
		NamedNodeMap map = fdNode.getAttributes();
		String id = map.getNamedItem(FD_ID).getTextContent(); 
		String name = map.getNamedItem(FD_NAME).getTextContent();
		String networkId = map.getNamedItem(FD_NETWORK_ID).getTextContent();
		
		HashMap<String, Double> nodeFluxes = new HashMap<String, Double>();
		// Get the listOfFluxes
		
		// TODO: handle errors in the xml files
		Node fluxList = fdNode.getFirstChild();
		NodeList fluxes = fluxList.getChildNodes();
		if (fluxes != null && fluxes.getLength() > 0){
			// TODO: handle the type 
			for (int k=0; k<fluxes.getLength(); ++k){
				Node flux = fluxes.item(k);
				NamedNodeMap fluxAttMap = flux.getAttributes();
				String fluxId = fluxAttMap.getNamedItem(FLUX_ID).getTextContent();
				String fluxValueString = fluxAttMap.getNamedItem(FLUX_VALUE).getTextContent();
				Double fluxValue = Double.parseDouble(fluxValueString);
				nodeFluxes.put(fluxId, fluxValue);
			}
		}
		
		// Use the ValImporter to generate the node fluxes
		ValFluxDistributionImporter valImporter = new ValFluxDistributionImporter(
									id, name, networkId, nodeFluxes);
		return valImporter.getFluxDistribution();
	}
	
	
	public static void main(String[] args){
		// Load some val files as FluxDistributionCollection
		System.out.println("*** Load Val Files ***");
		Set<FluxDis> fdSet = new HashSet<FluxDis>();
		
		String folder = "/home/mkoenig/Desktop/CyFluxViz/examples/Erythrocyte/val_FluxDistributions";
		String[] filenames = { 
				"All", "ATP_regeneration", "ATPase", "DPGase"
		};
		for (int k=0; k<filenames.length; ++k){
			String valFilename = folder + "/" + filenames[k] + ".val";
			File file = new File(valFilename);
			// import the FluxDistribution
			ValFluxDistributionImporter valImporter = new ValFluxDistributionImporter(file);
			FluxDis fd = valImporter.getFluxDistribution();
			System.out.println(fd);
			fdSet.add(fd);
		}
		
		// Transform to XML and Save
		System.out.println("*** Export XML ***");
		String xmlFilename = "fluxDistributionTest.xml";
		writeXMLFileForFluxDistributions(xmlFilename, fdSet);
		
		// Import the generated XML as FluxDistributions again
		System.out.println("*** Import XML ***");
		Collection<FluxDis> fdCollection = readFluxDistributionsFromXML(xmlFilename);
		for (FluxDis fd: fdCollection){
			System.out.println(fd);
		}
	}
}
