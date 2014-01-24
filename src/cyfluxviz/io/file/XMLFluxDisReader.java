package cyfluxviz.io.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cyfluxviz.FluxDis;

public class XMLFluxDisReader extends AbstractFluxDisReader{

	public XMLFluxDisReader() {
		super();
		extension = "xml";
	}

	@Override
	public Collection<FluxDis> read(File file) throws IOException {
		return readFluxDistributionsFromXML(file);
	}
	
	////////   FLUXDISTRIBUTION READERS   //////////////////////////////////////////
	
	/** Reads FluxDistributions from XML file. */
	private static Collection<FluxDis> readFluxDistributionsFromXML(File xmlFile){
		List<FluxDis> fdCollection = new LinkedList<FluxDis>();
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			NodeList fdList = doc.getElementsByTagName(XMLValidator.FD);
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
	

	private static Node getFluxListNodeFromFluxDistributionNode(Node fdNode){
		return getChildNodeFromNodeByName(fdNode, XMLValidator.FLUX_LIST);
	}
	
	private static Node getConcentrationListNodeFromFluxDistributionNode(Node fdNode){
		return getChildNodeFromNodeByName(fdNode, XMLValidator.CONCENTRATION_LIST);
	}
	
	private static Node getChildNodeFromNodeByName(Node fdNode, String name){
		Node listNode = null;
		NodeList nList = fdNode.getChildNodes();
		for (int i=0; i<nList.getLength(); ++i){
			Node node = nList.item(i);
			if (node.getNodeName().equals(name)){
				listNode = node;
				break;
			}
		}
		return listNode;
	}

	
	/** Main function for generating the FluxDis.
	 * Id is not parsed properly. 
	 */
	private static FluxDis readFluxDistributionFromNode(Node fdNode){
		NamedNodeMap map = fdNode.getAttributes();
		
		//TODO: id is not used ! needed ?
		//String id = map.getNamedItem(XMLValidator.FD_ID).getTextContent(); 
		
		String name = map.getNamedItem(XMLValidator.FD_NAME).getTextContent();
		String networkId = map.getNamedItem(XMLValidator.FD_NETWORK_ID).getTextContent();
		
		// listOfFluxes
		Node fluxList = getFluxListNodeFromFluxDistributionNode(fdNode);
		NodeList fluxes = fluxList.getChildNodes();
		
		System.out.println("-------------------------------------------------");
		System.out.println("# " + name + " #");
		
		// Read the node & edge fluxes
		HashMap<String, Double> nodeFluxes = new HashMap<String, Double>();
		HashMap<String, Double> edgeFluxes = new HashMap<String, Double>();
		if (fluxes != null && fluxes.getLength() > 0){
			for (int k=0; k<fluxes.getLength(); ++k){
				Node flux = fluxes.item(k);
				if (flux.getNodeName().equals(XMLValidator.FLUX)){				
					NamedNodeMap fluxAttMap = flux.getAttributes();
					String fluxType = fluxAttMap.getNamedItem(XMLValidator.FLUX_TYPE).getTextContent();
					String fluxId = fluxAttMap.getNamedItem(XMLValidator.FLUX_ID).getTextContent();
					String fluxValueString = fluxAttMap.getNamedItem(XMLValidator.FLUX_VALUE).getTextContent();
					Double fluxValue = Double.parseDouble(fluxValueString);
					if (fluxType.equals(XMLValidator.FLUX_TYPE_NODE)){ 
						nodeFluxes.put(fluxId, fluxValue);
						System.out.println("nodeFlux: " + fluxId + " -> " + fluxValue);
					} else if (fluxType.equals(XMLValidator.FLUX_TYPE_EDGE)){
						edgeFluxes.put(fluxId, fluxValue);
						System.out.println("edgeFlux: " + fluxId + " -> " + fluxValue);
					}
				}
			}
		}
		
		// listOfConcentrations
		Node concentrationList = getConcentrationListNodeFromFluxDistributionNode(fdNode);
		HashMap<String, Double> nodeConcentrations = new HashMap<String, Double>();
		if (concentrationList != null){
			NodeList concentrations = concentrationList.getChildNodes();
			if (concentrations != null && concentrations.getLength() > 0){
				for (int k=0; k<concentrations.getLength(); ++k){
					
					Node concentration = concentrations.item(k);
					if (concentration.getNodeName().equals(XMLValidator.CONCENTRATION)){				
						NamedNodeMap concentrationAttMap = concentration.getAttributes();
						String concentrationType = concentrationAttMap.getNamedItem(XMLValidator.CONCENTRATION_TYPE).getTextContent();
						String concentrationId = concentrationAttMap.getNamedItem(XMLValidator.CONCENTRATION_ID).getTextContent();
						String concentrationValueString = concentrationAttMap.getNamedItem(XMLValidator.CONCENTRATION_VALUE).getTextContent();
						Double concentrationValue = Double.parseDouble(concentrationValueString);
						if (concentrationType.equals(XMLValidator.CONCENTRATION_TYPE_NODE)){ 
							nodeConcentrations.put(concentrationId, concentrationValue);
							System.out.println("nodeConcentration: " + concentrationId + " -> " + concentrationValue);
						} 
					}
				}
			}
		}
		
		System.out.println("nodeFluxes: #" + nodeFluxes.size());
		System.out.println("edgeFluxes: #" + edgeFluxes.size());
		System.out.println("nodeConcentrations: #" + nodeConcentrations.size());
		
		return new FluxDis(name, networkId, nodeFluxes, edgeFluxes, nodeConcentrations);
	}
}
