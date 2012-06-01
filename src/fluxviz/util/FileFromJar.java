package fluxviz.util;

import java.io.*;

/**
 * Creates file object from jar.
 * Creates temporary file.
 * @author mkoenig
 *
 */
public class FileFromJar{
	private File f = new File("tmp");
	
	public FileFromJar(String filename){
		try{
			InputStream inputStream= new FileInputStream("filename");
			OutputStream out=new FileOutputStream(f);
			byte buf[]=new byte[1024];
			int len;
			while((len=inputStream.read(buf))>0){
				out.write(buf,0,len);
			}
			out.close();
			inputStream.close();
			}
		catch (IOException e){
			System.out.println("Error file reading: " + filename);
			e.printStackTrace();
		}
    }

	/* returns the file from the Jar */
	public File getFile() {
		return f;
	}
}
