package cyfluxviz.io.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import cyfluxviz.FluxDis;


/** Handle the reading Task somewhere else. */
public abstract class AbstractFluxDisWriter {
	protected File file;	
	
	public AbstractFluxDisWriter(String fileName) {
		file = new File(fileName);
	}
	
	public AbstractFluxDisWriter(File file) {
		this.file = file;
	}
	
	public abstract void write(Collection<FluxDis> fds) throws IOException;
	
}