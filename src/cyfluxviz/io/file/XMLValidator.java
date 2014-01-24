package cyfluxviz.io.file;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.xml.sax.SAXException;

import cyfluxviz.CyFluxVizPlugin;

/** Validate XML against the xsd schema file. 
 * Clearly define what is allowed and parsed in the XML file.
 * TODO: hard code the rules for the XML 
 */
public class XMLValidator {
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
	public static String FLUX_TYPE_EDGE = "edgeFlux";
	
	public static String CONCENTRATION_LIST = "listOfConcentrations";
	public static String CONCENTRATION = "concentration";
	public static String CONCENTRATION_ID = "id";
	public static String CONCENTRATION_VALUE = "concentrationValue";
	public static String CONCENTRATION_TYPE = "type";
	public static String CONCENTRATION_TYPE_NODE = "nodeConcentration";
	
	
	
	
	//TODO: not working to provide the file;
	public static File XSD; 
	//public static final File XSD = new File("D:/Workspace/java/Cytoscape2Plugins/CyFluxViz/CyFluxViz.xsd");

	
	public void validate(File file) throws IOException, SAXException {
		// TODO: the same fucking problems with packing resources TODO: WTF
		String fileName = CyFluxVizPlugin.class.getClassLoader().getResource("cyfluxviz/io/file/CyFluxViz.xsd").toString();
		System.out.println(XSD);
		XSD = new File(fileName);
		
		
		
		Source xmlFile = new StreamSource(file);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(XSD);
		Validator validator = schema.newValidator();
		try {
			validator.validate(xmlFile);
			System.out.println(xmlFile.getSystemId() + " is valid");
		} catch (SAXException e) {
			System.out.println(xmlFile.getSystemId() + " is NOT valid");
			System.out.println("Reason: " + e.getLocalizedMessage());
		}

	}
	
	public static void main(String[] args) throws IOException, SAXException{
		// Validate one of the XML files against the schema
		File test = new File("D:/Workspace/java/Cytoscape2Plugins/CyFluxViz/examples/demo_kinetic/Koenig2013_demo_Fluxes.xml");
		XMLValidator v = new XMLValidator();
		v.validate(test);	
	}
	
}
