package cyfluxviz.nodesplit.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/** Main class for the splitting of nodes which keeps track of the splitting. 
 * 
 * Mappings are specific for a network.
 * The original ids in the split are called sources, the new ids are referred to as 
 * targets of the split.
 * */

public class SplitMapping {
	private String networkId;
	private boolean splitStatus;
	private HashMap<Integer, Set<Integer>> nMap;		// node map
	private HashMap<Integer, Integer> eMap;				// edge map
	
	public SplitMapping(String networkId){
		this.networkId = networkId;
		clear();
	}
	
	public void clear(){
		nMap = new HashMap<Integer, Set<Integer>>();
		eMap = new HashMap<Integer, Integer>();
		setSplitStatus(false);
	}
	
	public boolean getSplitStatus() {
		return splitStatus;
	}
	private void setSplitStatus(boolean status) {
		splitStatus = status;
	}
	
	public void updateSplitStatus(){
		setSplitStatus(existsSplitting());
	}
	
	/** Test is splitting exists. 
	 * TODO: should be easier via only NodeSourceCount.
	 * */
	private boolean existsSplitting(){
		// return this.getNodeSourceCount() > 0
		
		return (this.getNodeSourceCount() < this.getNodeTargetCount());
	}
	
	public String getNetworkId(){
		return networkId;
	}
	
	public void putNode(Integer source, Integer target){
		if (! nMap.containsKey(source)){
			nMap.put(source, new HashSet<Integer>());
		}
		Set<Integer> set = nMap.get(source);
		set.add(target);
	}
	public void removeNodeSource(Integer source){
		nMap.remove(source);
	}
	
	
	public void putEdge(Integer source, Integer target){
		eMap.put(source, target);
	}
	public void removeEdgeSource(Integer source){
		eMap.remove(source);
	}
	public void clearEdges(){
		eMap.clear();
	}
	
	public Set<Integer> getNodeSources(){
		return nMap.keySet();
	}
	public Set<Integer> getEdgeSources(){
		return eMap.keySet();
	}
	
	public Set<Integer> getNodeTargets(Integer source){
		Set<Integer> set = null;
		if (nMap.containsKey(source)){
			set = nMap.get(source);
		} else {
			set = new HashSet<Integer>();
		}
		return set;
	}
	
	/** Get all the split nodes in the network. */
	public Set<Integer> getNodeTargets(){
		Set<Integer> set = new HashSet<Integer>();
		for (Integer source: nMap.keySet()){
			set.addAll(getNodeTargets(source));
		}
		return set;
	}
	
	public Integer getEdgeTarget(Integer source){
		Integer target = null;
		if (eMap.containsKey(source)){
			target = eMap.get(source);
		} 
		return target;
	}

	
	/** Count the node sources in the mapping. */
	public int getNodeSourceCount(){
		return nMap.size();
	}
	
	/** Count the edge sources in the mapping. */
	public int getEdgeSourceCount(){
		return eMap.size();
	}
	
	/** Count targets in the node mapping. */
	public int getNodeTargetCount(){
		int count = 0;
		for (Integer key: nMap.keySet()){
			count += nMap.get(key).size();
		}
		return count;
	}
}


