package cyfluxviz;

import java.util.HashMap;

import cyfluxviz.gui.PanelText;
import cytoscape.CyNetwork;

public class FluxDisStatistics {
	private FluxDis fluxDistribution;

	// statistical values
	private double min = 0.0;
	private double max = 0.0;
	private double absMin = 0.0;
	private double absMax = 0.0;
	private double absMean = 0.0;
	private double fluxFraction = 0.0;
	private int zeroEdgeCount = 0;
	private int networkEdgeCount = 0;
	private int fluxEdgeCount = 0;

	public FluxDisStatistics(FluxDis fluxDistribution) {
		this.fluxDistribution = fluxDistribution;
		networkEdgeCount = getNetworkEdgeCount();
		calculateStatistics();
	}

	private int getNetworkEdgeCount() {
		CyNetwork network = fluxDistribution.getCyNetwork();
		return network.getEdgeCount();
	}

	private void calculateStatistics() {

		HashMap<String, Double> fluxes = fluxDistribution.getEdgeFluxes();
		fluxEdgeCount = fluxes.size();
		zeroEdgeCount = 0;

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
			if (flux == 0.0){
				zeroEdgeCount++;
			}
		}
		fluxFraction = 1.0 * (fluxEdgeCount-zeroEdgeCount) / networkEdgeCount;
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
						+ "<tr><td><i>fluxFraction</i></td>  <td>%.3f [%d/%d] </td></tr>"
						+ "</table>", min, max, absMin, absMax, absMean,
						fluxFraction, (fluxEdgeCount-zeroEdgeCount), networkEdgeCount);
		return out;
	}

	public String toString() {
		String output = String.format("min: %s\n" + "max: %s\n"
				+ "absMin: %s\n" + "absMax: %s\n" + "absMean: %s\n"
				+ "fluxFraction: [%s/%s] %s", min, max, absMin, absMax,
				absMean, fluxFraction, fluxEdgeCount, networkEdgeCount,
				fluxFraction);
		return output;
	}
}