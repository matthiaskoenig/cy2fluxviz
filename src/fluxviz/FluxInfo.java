package fluxviz;

/**
 * Stores the information of a single line in the simulation file.
 * @author mkoenig
 *
 */
public class FluxInfo {
	private String name;
	private String target;
	private String constraints;
	private String evaluation;
	private String comment;
	
	public FluxInfo(String line){
		//DPGase	DPGase	stdef.txt	DPGase	demonstrates the target reaction DPGase
		this.init(line);
	}
	
	/**
	 * Initialises the FluxInfo object from the given simulation line.
	 */
	public void init(String line){
		System.out.println("line: " + line);
		
		String[] items = line.split("\t");
		//TODO: test the simulation file and raise errors
		name = items[0] + ".val";
		target = items[1];
		constraints = items[2];
		evaluation = items[3];
		if (items.length > 4){
			comment = items[4];
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String toString(){
		String res = "\n#  FluxInfo  #\n"; 
		res += "name: " + name + "\n";
		res += "target: " + target + "\n";
		res += "constraints: " + constraints + "\n";
		res += "evaluation: " + evaluation + "\n";
		res += "comment: " + comment + "\n";
		return res;
	}
	public String toHTML(){
		String out = "<b># " + comment + " #</b><br>";
		//out += "<b>name</b>: " + name + "<br>";
		out += "<b>target fluxes</b>: " + target + "<br>";
		out += "<b>constraints</b>: " + constraints + "<br>";
		out += "<b>evaluation</b>: " + evaluation + "<br>";
		return out;
	}
}
