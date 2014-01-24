package cyfluxviz.io.image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.dialogs.ExportBitmapOptionsDialog;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;

import cytoscape.util.export.BitmapExporter;
import cytoscape.util.export.Exporter;
import cytoscape.util.export.PDFExporter;
import cytoscape.util.export.PSExporter;
import cytoscape.util.export.SVGExporter;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;

import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisCollection;
import cyfluxviz.gui.CyFluxVizPanel;
import cyfluxviz.io.image.GraphicsExporter.ChooserActionListener;
import cyfluxviz.util.CyNetworkUtils;
import cyfluxviz.visual.style.CyFluxVizStyles;
import cyfluxviz.visual.view.NetworkViewUpdater;
import cyfluxviz.visual.view.NetworkViewTools;

/* Action for exporting a network view to bitmap or vector graphics.
 * The GraphicssExporter is constructed with a Collection of FluxDistributions. 
 * 
 * All NetworkViews are generated with the given FluxDistributions and the available 
 * networks and views.
 * 
 * The bitmap settings dialog is only called once and the made settings applied to all
 * generated images.
 * 
 * @author Samad Lotia, Matthias Koenig
 */
public class GraphicsExporter {
	private static ExportFilter BMP_FILTER = new BitmapExportFilter("bmp", "BMP");
	private static ExportFilter JPG_FILTER = new BitmapExportFilter("jpg", "JPEG");
	private static ExportFilter PDF_FILTER = new PDFExportFilter();
	private static ExportFilter PNG_FILTER = new BitmapExportFilter("png", "PNG");
	private static ExportFilter SVG_FILTER = new SVGExportFilter();
	private static ExportFilter EPS_FILTER = new PSExportFilter("eps", "EPS");
	public static ExportFilter[] FILTERS = { PDF_FILTER, SVG_FILTER, EPS_FILTER, JPG_FILTER, PNG_FILTER, BMP_FILTER };

	private final String SEPARATOR = System.getProperty("file.separator");
	private ExportAsGraphicsFolderChooser chooser;
	Collection<FluxDis> fds;
	
	public GraphicsExporter(Collection<FluxDis> fds){
		this.fds = fds;
	}

	
	/** Get the folder and file type for export
	 * and start the export.
	 */
	public void exportImages() {
		// Select folder for the export
		if (CyNetworkUtils.existsCurrentNetworkAndView()) {
			chooser = new ExportAsGraphicsFolderChooser(GraphicsExporter.FILTERS, GraphicsExporter.PNG_FILTER);
			ActionListener listener = new ChooserActionListener();
			chooser.addActionListener(listener);
			chooser.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(
							null,
							"Image export in empty network or without view not possible.\n"
									+ "Load network and select view for the network before image export.",
							"Empty network or view warning",
							JOptionPane.WARNING_MESSAGE);
		}
	}

	
	class ChooserActionListener implements ActionListener {
		private File folder;
		ExportFilter filter;
		String extension;
		
		public void export(){
			if (filter instanceof BitmapExportFilter) {	
				// Create the BMP Dialog and create the images after the dialog is finished
				BitmapExportFilter bmpFilter = (BitmapExportFilter) filter;
				bmpFilter.createExportBitmapOptionsDialog(this, Cytoscape.getCurrentNetworkView());
				// the exportImages() function is called from the ActionListener in the BMP dialog.
			}else{
				exportImages();	
			}
		}
		
		public void exportImages(){
			FileOutputStream stream = null;
			FluxDisCollection fdCollection = FluxDisCollection.getInstance();
			String extension = (String) filter.getExtensionSet().toArray()[0];
			// Generate images for all FluxDistributions
			for (FluxDis fd : fds) {
				fdCollection.activateFluxDistribution(fd);
				
				// call the updateFunction via selecting the FluxDistribution
				CyFluxVizStyles.updateStyle();
				
				// Views are updated and drawn
				NetworkViewUpdater nvu = new NetworkViewUpdater(fd);
				nvu.updateNetworkViewsForFluxDistribution();
				
				// Mappings have to be updated for the selected FluxDistribution
				CyFluxVizPanel.getInstance().updateMappings();
				
				
				// Get all views for the fluxDistribution
				String networkId = fd.getNetworkId();
				CyNetwork network = Cytoscape.getNetwork(networkId);
				if (network != null) {
					for (CyNetworkView view : NetworkViewTools.getViewsForNetwork(networkId)) {
						String name = view.getTitle() + "_" + fd.getName()+ "." + extension;
						String filename = folder + SEPARATOR + name;
						System.out.println("CyFluxViz[INFO] -> Export : " + filename);
						File file = new File(filename);
						try {
							stream = new FileOutputStream(file);
							filter.export(view, stream);
							
						} catch (Exception exp) {
							exp.printStackTrace();
							JOptionPane.showMessageDialog(Cytoscape
									.getDesktop(), String.format(
									"Could not create file %s\nError: %s",
									file.getName(), exp.getMessage()));
							return;
						}
					}
				}
			}
			
		}
		
		public void actionPerformed(ActionEvent event) {
			filter = (ExportFilter) chooser.getSelectedFormat();
			filter.setExportTextAsFont(chooser.getExportTextAsFont());

			folder = chooser.getSelectedFile();
			chooser.dispose();
			export();
		}
	};

}


/** ExportFilter for the different formats.
 * These handle the actual export of the NetworkView */
abstract class ExportFilter extends CyFileFilter {
	public abstract void export(CyNetworkView view, FileOutputStream stream);
	
	protected boolean exportTextAsFont = false;

	public ExportFilter(String extension, String description) {
		super(extension, description);
	}

	public boolean isExtensionListInDescription() {
		return true;
	}

	public String toString() {
		return getDescription();
	}

	public void setExportTextAsFont(boolean pExportTextAsFont) {
		exportTextAsFont = pExportTextAsFont;
	}

	public boolean getExportTextAsFont() {
		return exportTextAsFont;
	}
}


/* PDF working without options. */
class PDFExportFilter extends ExportFilter {
	public PDFExportFilter() {
		super("pdf", "PDF");
	}

	public void export(final CyNetworkView view, final FileOutputStream stream) {
		PDFExporter exporter = new PDFExporter();
		exporter.setExportTextAsFont(this.getExportTextAsFont());
		ExportTask.run("Exporting to PDF", exporter, view, stream);
	}
}


class BitmapExportFilter extends ExportFilter {
	private String extension;
	private static ExportBitmapOptionsDialog dialog;
	
	public BitmapExportFilter(String extension, String description) {
		super(extension, description);
		this.extension = extension;
	}

	public void createExportBitmapOptionsDialog(final ChooserActionListener al,
			CyNetworkView view) {
		final InternalFrameComponent ifc = Cytoscape.getDesktop()
				.getNetworkViewManager().getInternalFrameComponent(view);

		dialog = new ExportBitmapOptionsDialog(ifc.getWidth(), ifc.getHeight());
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				al.exportImages();
			}
		};
		dialog.addActionListener(listener);
		dialog.setVisible(true);
	}
	
	
	public void export(final CyNetworkView view, final FileOutputStream stream) {
			BitmapExporter exporter = new BitmapExporter(extension, dialog.getZoom());
			ExportTask.run("Exporting to " + extension, exporter, view,
					stream);

	}
}


class SVGExportFilter extends ExportFilter {
	public SVGExportFilter() {
		super("svg", "SVG");
	}

	public void export(final CyNetworkView view, final FileOutputStream stream) {
		SVGExporter exporter = new SVGExporter();
		exporter.setExportTextAsFont(this.getExportTextAsFont());
		ExportTask.run("Exporting to SVG", exporter, view, stream);
	}
}


class PSExportFilter extends ExportFilter {
	public PSExportFilter(String extension, String description) {
		super(extension, description);
	}

	public void export(final CyNetworkView view, final FileOutputStream stream) {
		PSExporter exporter = new PSExporter();
		exporter.setExportTextAsFont(this.getExportTextAsFont());
		ExportTask.run("Exporting to EPS", exporter, view, stream);
	}
}


class ExportTask {
	public static void run(final String title, final Exporter exporter,
			final CyNetworkView view, final FileOutputStream stream) {
		// Create the Task
		Task task = new Task() {
			TaskMonitor monitor;

			public String getTitle() {
				return title;
			}

			public void setTaskMonitor(TaskMonitor monitor) {
				this.monitor = monitor;
			}

			public void halt() {
			}

			public void run() {
				try {
					try {
						exporter.export(view, stream);
					} catch (IOException e) {
						monitor.setException(e, "Could not complete export of network");
					}
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (IOException ioe) {
						}
					}
				}
			}
		};

		// Execute the task
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(false);
		jTaskConfig.displayTimeElapsed(true);
		jTaskConfig.displayTimeRemaining(false);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.setModal(true);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		TaskManager.executeTask(task, jTaskConfig);
	}
}

