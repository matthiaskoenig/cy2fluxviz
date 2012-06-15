package cyfluxviz.util;

import java.io.File;

public class CreateSubdirectory {

	/**
	 * Creates all nesessary subdirectories for the file.
	 */
	public CreateSubdirectory (String fname) {
		String parent = (new File(fname).getParent());
		if (parent == null){
			return;
		}
		else {
			@SuppressWarnings("unused")
			CreateSubdirectory tmp = new CreateSubdirectory(parent);
		}
		File dir = new File(parent);
		if(!dir.exists()){
			boolean success = dir.mkdir();
			if (success == true){
				System.out.println("Created dir: " + dir.getPath());
			}
			else {
				System.out.println("Not created: " + dir.getPath());
			}
		}
	}
}
