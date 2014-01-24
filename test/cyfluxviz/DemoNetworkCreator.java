package cyfluxviz;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Creator;
import org.sbml.jsbml.History;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;

/** Demo example network generated in Java with JSBML.
 * FluxDistributions are encoded as ValFiles.
 * @author mkoenig
 * @date 2013-08-01
 */
public class DemoNetworkCreator {
	
	/** Create the SBMLDocument for Demo Network with JSBML. 
	 * 
	 * Bug: problems setting SBO terms due to the multiple dependencies on OBO within 
	 * 	the Cytoscape plugins. CySBML related bug.
	
	java.lang.NoSuchMethodError: org.biojava.ontology.Ontology.createTerm(Ljava/lang/String;)Lorg/biojava/ontology/Term;
	at org.biojava.ontology.obo.OboFileHandler.newKey(OboFileHandler.java:116)
	at org.biojava.ontology.obo.OboFileParser.triggerNewKey(OboFileParser.java:406)
	at org.biojava.ontology.obo.OboFileParser.parseOBO(OboFileParser.java:227)
	at org.biojava.ontology.io.OboParser.parseOBO(OboParser.java:96)
	at org.sbml.jsbml.SBO.<clinit>(SBO.java:595)
	at org.sbml.jsbml.AbstractSBase.setSBOTerm(AbstractSBase.java:1468)
	at cyfluxviz.examples.DemoNetworkCreator.createDemoSBMLDocument(DemoNetworkCreator.java:69)
	at cyfluxviz.examples.DemoNetworkCreator.createDemoSBMLFile(DemoNetworkCreator.java:157)
	at cyfluxviz.examples.DemoNetworkCreator.main(DemoNetworkCreator.java:164)
	 
	 */
	private static SBMLDocument createDemoSBMLDocument(){
		SBMLDocument doc = new SBMLDocument(2, 3);
		Model model = new Model("Koenig_demo", 2, 3);
		model.setName("Demo Network for CyFluxViz");
		doc.setModel(model);
		
		// Create model history
		History hist = new History();
		Creator creator = new Creator("Matthias", "Koenig", 
									"Charite Berlin", "matthias.koenig@charite.de");
		hist.addCreator(creator);
		model.setHistory(hist);
		
		
		// Compartments
		Compartment c_out = model.createCompartment("outside");
		c_out.setName("Outer Compartment");
		Compartment c_in = model.createCompartment("inside");
		c_in.setName("Inner Compartment");
		
		// Species
		String[] insideIds = {"A_in", "B_in", "C_in"};
		for (int k=0; k<insideIds.length; ++k){
			String id = insideIds[k];
			Species species = model.createSpecies(id, c_in);
			species.setName(id);
		}
		String[] outsideIds = {"A_out", "B_out", "C_out"};
		for (int k=0; k<outsideIds.length; ++k){
			String id = outsideIds[k];
			Species species = model.createSpecies(id, c_in);
			species.setName(id);
		}
		
		// Reactions
		// b1 : Import of A
		// Add a substrate (SBO: 15) and product (SBO: 11).
		Reaction b1 = model.createReaction("b1");
		b1.setReversible(false);
		SpeciesReference sR = b1.createReactant(model.getSpecies("A_out"));
		//sR.setSBOTerm((Integer) 15);
		sR = b1.createProduct(model.getSpecies("A_in"));
		//sR.setSBOTerm((Integer) 11);
		
		// b2 : Export of B
		Reaction b2 = model.createReaction("b2");
		b2.setReversible(false);
		sR = b2.createReactant(model.getSpecies("B_in"));
		//sR.setSBOTerm((Integer) 15);
		sR = b2.createProduct(model.getSpecies("B_out"));
		//sR.setSBOTerm((Integer) 11);
		
		// b3 : Export of C
		Reaction b3 = model.createReaction("b3");
		b3.setReversible(false);
		sR = b3.createReactant(model.getSpecies("C_in"));
		//sR.setSBOTerm((Integer) 15);
		sR = b3.createProduct(model.getSpecies("C_out"));
		//sR.setSBOTerm((Integer) 11);
		
		// v1 : A -> B
		Reaction v1 = model.createReaction("v1");
		b3.setReversible(false);
		sR = v1.createReactant(model.getSpecies("A_in"));
		//sR.setSBOTerm((Integer) 15);
		sR = v1.createProduct(model.getSpecies("B_in"));
		//sR.setSBOTerm((Integer) 11);
	
		// v2 : A -> C
		Reaction v2 = model.createReaction("v2");
		b3.setReversible(false);
		sR = v2.createReactant(model.getSpecies("A_in"));
		//sR.setSBOTerm((Integer) 15);
		sR = v2.createProduct(model.getSpecies("C_in"));
		//sR.setSBOTerm((Integer) 11);
		
		// v3 : C -> A
		Reaction v3 = model.createReaction("v3");
		b3.setReversible(false);
		sR = v3.createReactant(model.getSpecies("C_in"));
		//sR.setSBOTerm(15);
		sR = v3.createProduct(model.getSpecies("A_in"));
		//sR.setSBOTerm(11);
		
		// v4 : C -> A
		Reaction v4 = model.createReaction("v4");
		b3.setReversible(false);
		sR = v4.createReactant(model.getSpecies("C_in"));
		//sR.setSBOTerm(15);
		sR = v4.createProduct(model.getSpecies("B_in"));
		//sR.setSBOTerm(11);
		
		return doc;
	}
	
	/** Creates val files for flux distributions */
	private static void createValFiles(String fname) throws IOException{
		String[] ids1    = {"b1", "v1", "b2"};
		Double[] fluxes1 = { 1.0,  1.0,  1.0};
		createValFiles(ids1, fluxes1 , fname+"_FD_01.val");
	
		String[] ids2    = {"b1", "v1", "v2", "v4", "b2"};
		Double[] fluxes2 = { 1.0,  0.3,  0.7,  0.7,  1.0};
		createValFiles(ids2, fluxes2, fname+"_FD_02.val");
	
		String[] ids3    = {"b1", "v1", "v2", "b3", "v4", "b2"};
		Double[] fluxes3 = { 1.0,  0.3,  0.7,  0.4,  0.3,  0.7};
		createValFiles(ids3, fluxes3, fname+"_FD_03.val");	
	}

	private static void createValFiles(String[] ids, Double[] fluxes, String filename) throws IOException{
		String sep = "\t";
		String fd = "";
		for (int i=0; i<ids.length; ++i){
			fd += ids[i] + sep + fluxes[i] + "\n";
		}
		createValFile(filename, fd);
		System.out.println("VAL file written: " + filename);
	}
	

	private static void createValFile(String filename, String info) throws IOException{
		FileWriter fstream = new FileWriter(filename);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(info);
		out.close();
	}
	
	private static void createDemoSBMLFile(String filename) throws SBMLException, FileNotFoundException, XMLStreamException{
		SBMLDocument doc = createDemoSBMLDocument();
		SBMLWriter.write(doc, filename + ".xml", "CyFluxViz", "v0.92");
		System.out.println("SBML file written: " + filename + ".xml");
	}
	
	public static void main(String[] args){
		// [1] Create the SBML file for the demo network
		String filename = "Koenig_demo";
		try {
			createDemoSBMLFile(filename);
		} catch (SBMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* [2] Create the flux distributions
		 
		   Use the SBML network with FBA simulation software like FASIMU or other
		   to calculate Flux Distributions in the demo network.
		   For sake of simplicity I generated some Flux Distributions manually, 
		   stored as val files.
		*/
		try {
			createValFiles(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 /* [3] Use the SBML file and the val distributions in Cytoscape
		    [3.1] Load the SBML network with CySBML
		    [3.2] Load the val files via CyFluxViz Import Tab (import val files)
		    [3.3] Select the FluxDistribution to display in the network
		  */
	}
}
