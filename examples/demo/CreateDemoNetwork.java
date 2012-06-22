import java.io.BufferedWriter;
import java.io.File;
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


public class CreateDemoNetwork {
	
	/* Create the SBMLDocument for Demo Network */
	private static SBMLDocument createDemoSBMLDocument(){
		SBMLDocument doc = new SBMLDocument(2, 3);
		Model model = new Model("Koenig2012_demo", 2, 3);
		model.setName("Demo Network");
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
		sR.setSBOTerm(15);
		sR = b1.createProduct(model.getSpecies("A_in"));
		sR.setSBOTerm(11);
		
		// b2 : Export of B
		Reaction b2 = model.createReaction("b2");
		b2.setReversible(false);
		sR = b2.createReactant(model.getSpecies("B_in"));
		sR.setSBOTerm(15);
		sR = b2.createProduct(model.getSpecies("B_out"));
		sR.setSBOTerm(11);
		
		// b3 : Export of C
		Reaction b3 = model.createReaction("b3");
		b3.setReversible(false);
		sR = b3.createReactant(model.getSpecies("C_in"));
		sR.setSBOTerm(15);
		sR = b3.createProduct(model.getSpecies("C_out"));
		sR.setSBOTerm(11);
		
		// v1 : A -> B
		Reaction v1 = model.createReaction("v1");
		b3.setReversible(false);
		sR = v1.createReactant(model.getSpecies("A_in"));
		sR.setSBOTerm(15);
		sR = v1.createProduct(model.getSpecies("B_in"));
		sR.setSBOTerm(11);
	
		// v2 : A -> C
		Reaction v2 = model.createReaction("v2");
		b3.setReversible(false);
		sR = v2.createReactant(model.getSpecies("A_in"));
		sR.setSBOTerm(15);
		sR = v2.createProduct(model.getSpecies("C_in"));
		sR.setSBOTerm(11);
		
		// v3 : C -> A
		Reaction v3 = model.createReaction("v3");
		b3.setReversible(false);
		sR = v3.createReactant(model.getSpecies("C_in"));
		sR.setSBOTerm(15);
		sR = v3.createProduct(model.getSpecies("A_in"));
		sR.setSBOTerm(11);
		
		// v4 : C -> A
		Reaction v4 = model.createReaction("v4");
		b3.setReversible(false);
		sR = v4.createReactant(model.getSpecies("C_in"));
		sR.setSBOTerm(15);
		sR = v4.createProduct(model.getSpecies("B_in"));
		sR.setSBOTerm(11);
		
		return doc;
	}
	public static void createValFiles(String filename) throws IOException{;
		String fd1 = 
				"b1\t1.0\n" +
				"v1\t1.0\n" +
				"b2\t1.0\n"; 
		createValFile(filename + "_FD_01.val", fd1);
		
		String fd2 = 
				"b1\t1.0\n" +
				"v1\t0.3\n" +
				"v2\t0.7\n" +
				"v4\t0.7\n" +
				"b2\t1.0\n";
		createValFile(filename + "_FD_02.val", fd2);
		
		String fd3 = 
				"b1\t1.0\n" +
				"v1\t0.3\n" +
				"v2\t0.7\n" +
				"b3\t0.4\n" +
				"v4\t0.3\n" +
				"b2\t0.7\n";
		createValFile(filename + "_FD_03.val", fd3);
		
	}
	
	public static void createValFile(String filename, String info) throws IOException{
		FileWriter fstream = new FileWriter(filename);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(info);
		out.close();
	}
	
	public static void createDemoSBMLFile(String filename) throws SBMLException, FileNotFoundException, XMLStreamException{
		SBMLDocument doc = createDemoSBMLDocument();
		SBMLWriter.write(doc, filename + ".xml", "CySBML", "v1.2");
	}
	
	public static void main(String[] args) throws SBMLException, XMLStreamException, IOException{
		// [1] Create the SBML file for the demo network
		String filename = "Koenig2012_demo";
		createDemoSBMLFile(filename);
		
		/* [2] Create the flux distributions
		 
		   Use the SBML network with FBA simulation software like FASIMU or other
		   to calculate Flux Distributions in the demo network.
		   For sake of simplicity I generated some Flux Distributions manually, 
		   stored as val files.
		*/
		createValFiles(filename);

		 /* [3] Use the SBML file and the val distributions in Cytoscape
		    [3.1] Load the SBML network with CySBML
		    [3.2] Load the val files via CyFluxViz Import Tab (import val files)
		    [3.3] Select the FluxDistribution to display in the network
		  */
	}
}
