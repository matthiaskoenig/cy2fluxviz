package cyfluxviz.util.file;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;

import cyfluxviz.CyFluxVizPlugin;

public class ExtractJar {
	private File jarFile;
	private File destDir;
	
	public ExtractJar(File jarFile, File destDir){
		this.jarFile = jarFile;
		this.destDir = destDir;
	}
	
	/* Extract the content of the jar file to the destination folder. */
	public void extract() throws IOException, java.util.zip.ZipException{
		print();
		java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile);
		java.util.Enumeration<JarEntry> entries = jar.entries();
		
		while (entries.hasMoreElements()) {
			java.util.jar.JarEntry file = (java.util.jar.JarEntry) entries.nextElement();
			File f;
			
			if (file.isDirectory()){
				@SuppressWarnings("unused")
				boolean success = new File(destDir + File.separator + file.getName()).mkdir();
				continue;
			}
			else{	
				f = new File(destDir, file.getName());
			}
			
			//Create subdirectories
			File dir = new File(f.getParent());
			if(!dir.exists()){
				@SuppressWarnings("unused")
				boolean success = dir.mkdirs();
			}	
			java.io.InputStream is = jar.getInputStream(file); // get the input stream
			java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
			while (is.available() > 0) {  // write contents of 'is' to 'fos'
				fos.write(is.read());
			}
			fos.close();
			is.close();
		}
		jar.close();
	}

	public void print(){
		CyFluxVizPlugin.LOGGER.info("Extract '" + jarFile + "' -> '" + destDir + "'.");
	}
}