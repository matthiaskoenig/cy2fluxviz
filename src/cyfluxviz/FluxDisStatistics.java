package cyfluxviz;

import java.util.HashMap;

/** Calculate statistical values for a given FluxDistribution. 
 * Has to be recalculated when the fluxDistribution changes. 
 *
 * @author mkoenig
 */
public class FluxDisStatistics {

	// statistical values
	private double min = 0.0;
	private double max = 0.0;
	private double absMin = 0.0;
	private double absMax = 0.0;
	private double absMean = 0.0;
	private int fluxEdgeCount = 0;

	public FluxDisStatistics(FluxDis fd) {
		calculateStatistics(fd);
	}

	private void calculateStatistics(FluxDis fd) {

		HashMap<String, Double> fluxes = fd.getEdgeFluxes();
		fluxEdgeCount = fluxes.size();

		Double absSum = 0.0;
		for (Double flux : fluxes.values()) {

			absSum += Math.abs(flux);
			if (absMin == 0.0 && flux != 0.0) {
				absMin = Math.abs(flux);
			}
			if (flux > max) {
				max = flux;
			}
			if (flux < min) {
				min = flux;
			}
			if (flux != 0.0 && Math.abs(flux) < absMin) {
				absMin = Math.abs(flux);
			}
			if (Math.abs(flux) > absMax) {
				absMax = flux;
			}
		}
		absMean = absSum / fluxEdgeCount;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getAbsMin() {
		return absMin;
	}

	public double getAbsMax() {
		return absMax;
	}

	public String toHTML() {
		String out = String.format(
						  "<b>Statistics</b> <br>"
						+ "<table>"
						+ "<tr><td><i>Min Flux</i></td>           <td>%.3f</td></tr>"
						+ "<tr><td><i>Max Flux</i></td>           <td>%.3f</td></tr>"
						+ "<tr><td><i>Abs Min</i></td>        <td>%.3f</td></tr>"
						+ "<tr><td><i>Abs Max</i></td>        <td>%.3f</td></tr>"
						+ "<tr><td><i>Abs Mean</i></td>       <td>%.3f</td></tr>"
						+ "</table>", min, max, absMin, absMax, absMean);
		return out;
	}

	public String toString() {
		String output = String.format("min: %s\n" + "max: %s\n"
				+ "absMin: %s\n" + "absMax: %s\n" + "absMean: %s\n", 
				min, max, absMin, absMax, absMean);
		return output;
	}
}