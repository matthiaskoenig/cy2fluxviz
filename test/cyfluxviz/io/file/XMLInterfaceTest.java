package cyfluxviz.io.file;



/*TODO: rewrite tests for the AbstractFluxDisReader subclasses. */
public class XMLInterfaceTest {

	/**
	// @Rule
	//public TemporaryFolder folder = new TemporaryFolder();
	
	@BeforeClass
	public static void testSetup() {
	}

	@AfterClass
	public static void testCleanup() {
	    // Teardown for data used by the unit tests
	}


	@Test
	public void testReadFluxDistributionsFromXMLFile() {
		String xmlFilename = TestSettings.EXAMPLE_FOLDER + "/demo_kinetic/Koenig2013_demo_Fluxes.xml";
		Collection<FluxDis> cfd = XMLInterface.readFluxDistributionsFromXML(xmlFilename);
		assertNotNull(cfd);
		
		//TODO: full test of the file content
	}
	
	@Test
	public void testLoadValAndWriteXML(){
		
		// Load some val files as FluxDistributionCollection
		System.out.println("*** Load Val Files ***");
		Set<FluxDis> fdSet = new HashSet<FluxDis>();
		
		String folder = TestSettings.EXAMPLE_FOLDER + "/erythrocyte/val_fluxes";
		String[] filenames = {
				"All", "ATP_regeneration", "ATPase", "DPGase", "GSHox", "PPrPT", "PrPP_alt"
		};
		for (int k=0; k<filenames.length; ++k){
			String valFilename = folder + "/" + filenames[k] + ".val";
			File file = new File(valFilename);
			
			
			// import the FluxDistribution
			VALInterface valImporter = new VALInterface(file);
			FluxDis fd = valImporter.getFluxDistribution();
			System.out.println(fd);
			assertNotNull(fd);
			fdSet.add(fd);
		}
		
		// Transform to XML and Save
		System.out.println("*** Export XML ***");
		String xmlFilename = TestSettings.TMP_FOLDER + "/" + "fdTest.xml";
		XMLInterface.writeXMLFileForFluxDistributions(xmlFilename, fdSet);
		
		// Import the generated XML as FluxDistributions again
		System.out.println("*** Import XML ***");
		Collection<FluxDis> fdCollection = XMLInterface.readFluxDistributionsFromXML(xmlFilename);
		for (FluxDis fd: fdCollection){
			System.out.println(fd);
		}
	}
	*/
}
