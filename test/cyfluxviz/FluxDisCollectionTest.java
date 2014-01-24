package cyfluxviz;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

public class FluxDisCollectionTest {

	private static FluxDis fd1;
	private static FluxDis fd2;
	private static FluxDis fd3;
	private static FluxDis fd4;
	
	
	@BeforeClass
	public static void testSetup() {
		HashMap<String, Double> nodeFluxes = new HashMap<String, Double>();
		HashMap<String, Double> edgeFluxes = new HashMap<String, Double>();
		HashMap<String, Double> nodeConcentrations = new HashMap<String, Double>();
		
		fd1 = new FluxDis("fd1", "network", nodeFluxes, edgeFluxes, nodeConcentrations);
		fd2 = new FluxDis("fd2", "network", nodeFluxes, edgeFluxes, nodeConcentrations);
		fd3 = new FluxDis("fd3", "network", nodeFluxes, edgeFluxes, nodeConcentrations);
		fd4 = new FluxDis("fd4", "network", nodeFluxes, edgeFluxes, nodeConcentrations);
		
		FluxDisCollection fdc = FluxDisCollection.getInstance();
		Collection<FluxDis> fds = new HashSet<FluxDis>();
		fds.add(fd1);
		fds.add(fd2);
		fds.add(fd3);
		fds.add(fd4);
		fdc.addFluxDistributions(fds);
	}
	
	
	@Test
	public void testGetInstance() {
		FluxDisCollection fdc = FluxDisCollection.getInstance();
		assertNotNull(fdc);
	}

	@Test
	public void testGetIdSet() {
		FluxDisCollection fdc = FluxDisCollection.getInstance();
		assertNotNull(fdc.getIdSet());
		assertEquals(4, fdc.getIdSet().size());
		assertTrue(fdc.getIdSet().contains(fd1.getId()));
		assertTrue(fdc.getIdSet().contains(fd2.getId()));
		assertTrue(fdc.getIdSet().contains(fd3.getId()));
		assertTrue(fdc.getIdSet().contains(fd4.getId()));
		assertFalse(fdc.getIdSet().contains("test"));
	}

	@Test
	public void testGetFluxDistributions() {
		FluxDisCollection fdc = FluxDisCollection.getInstance();
		assertEquals(4, fdc.getFluxDistributions().size());
	}

	@Test
	public void testContainsFluxDistribution() {
		FluxDisCollection fdc = FluxDisCollection.getInstance();
		assertTrue(fdc.containsFluxDistribution(fd1.getId()));
		assertTrue(fdc.containsFluxDistribution(fd2.getId()));
		assertTrue(fdc.containsFluxDistribution(fd3.getId()));
		assertTrue(fdc.containsFluxDistribution(fd4.getId()));
		assertFalse(fdc.containsFluxDistribution(5));
	}

	@Test
	public void testGetFluxDistribution() {
		FluxDisCollection fdc = FluxDisCollection.getInstance();
		assertEquals(fd1, fdc.getFluxDistribution(fd1.getId()));
		assertEquals(fd2, fdc.getFluxDistribution(fd2.getId()));
		assertEquals(fd3, fdc.getFluxDistribution(fd3.getId()));
		assertEquals(fd4, fdc.getFluxDistribution(fd4.getId()));
		assertNull(fdc.getFluxDistribution(5));
	}


	@Test
	public void testRemoveFluxDistribution() {
		FluxDisCollection fdc = FluxDisCollection.getInstance();
		fdc.removeFluxDistribution(fd1);
		assertEquals(3, fdc.size());
		assertFalse(fdc.containsFluxDistribution(fd1.getId()));
	}

	@Test
	public void testHasActiveFluxDistribution() {
		FluxDisCollection fdc = FluxDisCollection.getInstance();
		assertFalse(fdc.hasActiveFluxDistribution());
		fdc.activateFluxDistribution(fd2);
		assertTrue(fdc.hasActiveFluxDistribution());
	}

	@Test
	public void testGetActiveFluxDistribution() {
		FluxDisCollection fdc = FluxDisCollection.getInstance();
		Collection<FluxDis> fds = new HashSet<FluxDis>();
		fds.add(fd2);
		fdc.addFluxDistributions(fds);
		fdc.activateFluxDistribution(fd2);
		assertEquals(fd2, fdc.getActiveFluxDistribution());
	}

	@Test
	public void testActivateFluxDistributionInt() {
		FluxDisCollection fdc = FluxDisCollection.getInstance();
		Collection<FluxDis> fds = new HashSet<FluxDis>();
		fds.add(fd3);
		fdc.addFluxDistributions(fds);
		fdc.activateFluxDistribution(fd3.getId());
		assertEquals(fd3, fdc.getActiveFluxDistribution());
	}


	@Test
	public void testDeactivateFluxDistribution() {
		FluxDisCollection fdc = FluxDisCollection.getInstance();
		assertTrue(fdc.hasActiveFluxDistribution());
		fdc.deactivateFluxDistribution();
		assertFalse(fdc.hasActiveFluxDistribution());
	}

}
