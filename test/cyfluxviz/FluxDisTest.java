package cyfluxviz;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FluxDisTest {
	private static FluxDis fd1;
	private static FluxDis fd2;
	
	@BeforeClass
	public static void testSetup() {
		HashMap<String, Double> nodeFluxes = new HashMap<String, Double>();
		HashMap<String, Double> edgeFluxes = new HashMap<String, Double>();
		HashMap<String, FluxDirection> edgeDirections = new HashMap<String, FluxDirection>();
		HashMap<String, Double> nodeConcentrations = new HashMap<String, Double>();
		
		nodeFluxes.put("n1", 1.0);
		nodeFluxes.put("n2", 2.0);
		nodeFluxes.put("n3", 3.0);
		nodeFluxes.put("n4", 4.0);
		edgeFluxes.put("e1", 1.0);
		edgeFluxes.put("e2", 2.0);
		edgeFluxes.put("e3", 3.0);
		edgeDirections.put("e1", FluxDirection.FORWARD);
		edgeDirections.put("e2", FluxDirection.REVERSE);
		edgeDirections.put("e3", FluxDirection.FORWARD);
		nodeConcentrations.put("n1", 1.0);
		nodeConcentrations.put("n2", 2.0);
		nodeConcentrations.put("n3", -1.0);
		nodeConcentrations.put("n4", -1.0);
		nodeConcentrations.put("n5", 0.0);
		
		fd1 = new FluxDis("test1", "network1", nodeFluxes, edgeFluxes, nodeConcentrations);
		fd2 = new FluxDis("test2", "network2", nodeFluxes, edgeFluxes, nodeConcentrations);
		
	}

	@AfterClass
	public static void testCleanup() {
	    // Teardown for data used by the unit tests
	}
	
	@Test
	public void testCompareTo() {
		assertTrue(fd1.compareTo(fd1) == 0);
		assertTrue(fd2.compareTo(fd2) == 0);
		assertTrue(fd1.compareTo(fd2) < 0);
		assertTrue(fd2.compareTo(fd1) > 0);
	}

	@Test
	public void testGetId() {
		assertEquals(new Integer(1), fd1.getId());
		assertEquals(new Integer(2), fd2.getId());
	}

	@Test
	public void testGetName() {
		assertEquals("test1", fd1.getName());
		assertEquals("test2", fd2.getName());
	}

	@Test
	public void testGetNetworkId() {
		assertEquals("network1", fd1.getNetworkId());
		assertEquals("network2", fd2.getNetworkId());
	}

	@Test
	public void testGetFluxStatistics() {
		assertNotNull(fd1.getFluxStatistics());
		assertNotNull(fd2.getFluxStatistics());
	}

	@Test
	public void testGetEdgeFluxes() {
		HashMap<String, Double> edgeFluxes = fd1.getEdgeFluxes();
		assertNotNull(edgeFluxes);
		assertEquals(3, edgeFluxes.size());
		
		edgeFluxes = fd2.getEdgeFluxes();
		assertNotNull(edgeFluxes);
		assertEquals(3, edgeFluxes.size());
	}

	@Test
	public void testGetEdgeDirections() {
		HashMap<String, FluxDirection> edgeDirections = fd1.getEdgeDirections();
		assertNotNull(edgeDirections);
		assertEquals(3, edgeDirections.size());
		
		edgeDirections = fd2.getEdgeDirections();
		assertNotNull(edgeDirections);
		assertEquals(3, edgeDirections.size());
	}

	@Test
	public void testGetNodeFluxes() {
		HashMap<String, Double> nodeFluxes = fd1.getNodeFluxes();
		assertNotNull(nodeFluxes);
		assertEquals(4, nodeFluxes.size());
		
		nodeFluxes = fd2.getNodeFluxes();
		assertNotNull(nodeFluxes);
		assertEquals(4, nodeFluxes.size());
	}

	@Test
	public void testGetNodeConcentrations() {
		HashMap<String, Double> nodeConcentrations = fd1.getNodeConcentrations();
		assertNotNull(nodeConcentrations);
		assertEquals(0, nodeConcentrations.size());
		
		nodeConcentrations = fd2.getNodeConcentrations();
		assertNotNull(nodeConcentrations);
		assertEquals(5, nodeConcentrations.size());
	}
	
	
	/*
	@Test
	public void testEdgeHasFlux() {
		assertTrue(fd1.edgeHasFlux("e1"));
		assertTrue(fd1.edgeHasFlux("e2"));
		assertTrue(fd1.edgeHasFlux("e3"));
		assertFalse(fd1.edgeHasFlux("e4"));
	}

	@Test
	public void testEdgeHasDirection() {
		assertTrue(fd1.edgeHasDirection("e1"));
		assertTrue(fd1.edgeHasDirection("e2"));
		assertTrue(fd1.edgeHasDirection("e3"));
		assertFalse(fd1.edgeHasDirection("e4"));
	}

	@Test
	public void testNodeHasFlux() {
		assertTrue(fd1.nodeHasFlux("n1"));
		assertTrue(fd1.nodeHasFlux("n2"));
		assertTrue(fd1.nodeHasFlux("n3"));
		assertTrue(fd1.nodeHasFlux("n4"));
		assertFalse(fd1.nodeHasFlux("nx"));
	}

	@Test
	public void testNodeHasConcentration() {
		assertFalse(fd1.nodeHasConcentration("n1"));
		assertTrue(fd2.nodeHasConcentration("n1"));
		assertTrue(fd2.nodeHasConcentration("n2"));
		assertTrue(fd2.nodeHasConcentration("n3"));
		assertTrue(fd2.nodeHasConcentration("n4"));
		assertTrue(fd2.nodeHasConcentration("n5"));
		assertFalse(fd2.nodeHasConcentration("nx"));
	}


	@Test
	public void testGetFluxForEdge() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFluxDirectionForEdge() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFluxForNode() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetConcentrationForNode() {
		fail("Not yet implemented");
	}
	*/

}
