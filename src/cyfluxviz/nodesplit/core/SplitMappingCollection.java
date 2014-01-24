package cyfluxviz.nodesplit.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/** Collection of the SplitMappings for the different networks. */
public class SplitMappingCollection {
	private static SplitMappingCollection uniqueInstance;
	
	private HashMap<String, SplitMapping> splitMappings;
	
	public static synchronized SplitMappingCollection getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new SplitMappingCollection();
		}
		return uniqueInstance;
	}
	private SplitMappingCollection() {
		splitMappings = new HashMap<String, SplitMapping>();
	}

	public Set<String> getNetworkIdSet() {
		return splitMappings.keySet();
	}
	
	public int size(){
		return splitMappings.size();
	}
	
	public Collection<SplitMapping> getSplitMappings(){
		return splitMappings.values();
	}
	
	public boolean hasSplitMappyingForNetworkId(String id){
		return splitMappings.containsKey(id);
	}
	
	/** Get or create. */
	public SplitMapping getOrCreateSplitMappingForNetworkId(String id){
		if (! hasSplitMappyingForNetworkId(id) ){
			System.out.println("CyNodeSplit[INFO]: Create new SplitMapping for network: " + id);
			splitMappings.put(id, new SplitMapping(id));
		}
		return splitMappings.get(id);
	}
}