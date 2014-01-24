package cyfluxviz.io.file;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import cyfluxviz.FluxDis;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

/**
 * Abstract super class which generates.
 */
public abstract class AbstractFluxDisReader {
	protected File[] files;
	protected String extension;
	

	public AbstractFluxDisReader() {
		files = getFilesWithExtension();
	}
	
	public AbstractFluxDisReader(File file) {
		files = new File[1];
		files[0] = file;
	}
	
	public AbstractFluxDisReader(String fileName) {
		this(new File(fileName));
	}
	
	public AbstractFluxDisReader(File[] files) {
		this.files = files;
	}
	
	/** Uses the Cytoscape file opening menu to get files.
	 * Creates the CyFilter for the extension.
	 * @param extension
	 * @param name
	 * @return
	 */
	private File[] getFilesWithExtension(){
		CyFileFilter[] filter = { new CyFileFilter(extension, "Load *." + extension + " files") };
		return FileUtil.getFiles("Select *." + extension + " files for current network.", FileUtil.LOAD, filter);
	}
	
	public final Collection<FluxDis> read(){
		Collection<FluxDis> fds = new LinkedList<FluxDis>();
		for (File file: files){
			Collection<FluxDis> newFds;
			try {
				newFds = read(file);
				if (newFds != null){
					fds.addAll(newFds);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		return fds;
	}
	
	public abstract Collection<FluxDis> read(File file) throws IOException;	
	
}
