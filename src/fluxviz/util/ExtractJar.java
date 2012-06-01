package fluxviz.util;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;

import fluxviz.CyFluxVizPlugin;

public class ExtractJar {
	/**
	 * Extract jarFile to given directory.
	 * @param jarFile
	 * @param destDir
	 * @throws IOException
	 */
	private File jarFile;
	private File destDir;
	
	public ExtractJar(File jarFile, File destDir){
		this.jarFile = jarFile;
		this.destDir = destDir;
	}
	
	/**
	 * Extract the content of the jar file to the destination folder.
	 * @throws IOException
	 */
	public void extract() throws IOException, java.util.zip.ZipException{
		this.print();
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
			CyFluxVizPlugin.getLogger().fine(f.toString());
			
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
	}

	/**
	 * Print the extraction information.
	 */
	public void print(){
		System.out.println("Extract '" + jarFile + "' -> '"+ destDir +"'.");
	}
}