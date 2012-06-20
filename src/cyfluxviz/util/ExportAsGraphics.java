package cyfluxviz.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisCollection;
import cyfluxviz.netview.NetworkView;
import cyfluxviz.netview.NetworkViewTools;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.dialogs.ExportAsGraphicsFileChooser;
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

/* Action for exporting a network view to bitmap or vector graphics.
 * @author Samad Lotia, Matthias Koenig
 */
public class ExportAsGraphics
{
	private static ExportFilter BMP_FILTER = new BitmapExportFilter("bmp", "BMP");
	private static ExportFilter JPG_FILTER = new BitmapExportFilter("jpg", "JPEG");
	private static ExportFilter PDF_FILTER = new PDFExportFilter();
	private static ExportFilter PNG_FILTER = new BitmapExportFilter("png", "PNG");
	private static ExportFilter SVG_FILTER = new SVGExportFilter();
	private static ExportFilter EPS_FILTER = new PSExportFilter("eps", "EPS");
	public static ExportFilter[] FILTERS = { PDF_FILTER, SVG_FILTER, EPS_FILTER, JPG_FILTER, PNG_FILTER, BMP_FILTER };
	
	public ExportAsGraphics(){}
	
    public static void exportImage(){
    	if (AttributeUtils.getSelectedFluxDistributions().size() == 0){
			JOptionPane.showMessageDialog(null,
					"No flux distributions selected for export.\n" +
					"Select flux distributions before image export.", "No flux distribution selected", JOptionPane.WARNING_MESSAGE);
    		return;
    	}
   
    	// Select folder for the export
		if (FluxVizUtil.availableNetworkAndView()){
			ExportAsGraphicsFileChooser chooser = new ExportAsGraphicsFileChooser(ExportAsGraphics.FILTERS);
	    	ExportAsGraphics ex = new ExportAsGraphics();
	    	ex.actionPerformed();	
		}
		else {
			JOptionPane.showMessageDialog(null,
					"Image export in empty network or without view not possible.\n" +
					"Load network and select view for the network before image export.",
					"Empty network or view warning", JOptionPane.WARNING_MESSAGE);
		}
    }
	
	public void actionPerformed()
	{
		// General selection of format and settings for all images
		final ExportAsGraphicsFileChooser chooser = new ExportAsGraphicsFileChooser(FILTERS);
		ActionListener listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				ExportFilter filter = (ExportFilter) chooser.getSelectedFormat();
				filter.setExportTextAsFont(chooser.getExportTextAsFont());
				
				File selectedFile = chooser.getSelectedFile();
				chooser.dispose();
				
				FileOutputStream stream = null;
				FluxDisCollection fdCollection = FluxDisCollection.getInstance();
				String extension = (String) filter.getExtensionSet().toArray()[0];
				
		    	for (String fdId : AttributeUtils.getSelectedFluxDistributions()){		
		    		FluxDis fd = fdCollection.getFluxDistribution(fdId);
					fdCollection.setFluxDistributionActive(fd);
					NetworkView.updateNetworkViewsForFluxDistribution(fd);
					
		    		// Get all views for the
		    		if (fd != null){
		    			String networkId = fd.getNetworkId();
		        		CyNetwork network = Cytoscape.getNetwork(networkId);
		        		if (network != null){
		        			List<CyNetworkView> views = NetworkViewTools.getCyNetworkViewsForNetworkId(networkId);
		        			for (CyNetworkView view: views){
		        				String name = view.getTitle() + "_" + fd.getName();
		    		    		String filename = selectedFile.getParent() + "/" + name + "." + extension;
		    		    		System.out.println("CyFluxViz[INFO] -> Export : " + filename);
		    		            
		    		    		File file = new File(filename);
		    					try {
		    						stream = new FileOutputStream(file);
		    						filter.export(view, stream);  
		    					} catch (Exception exp) {
		    						exp.printStackTrace();
		    						JOptionPane.showMessageDialog(	
		    								Cytoscape.getDesktop(),
		    								String.format("Could not create file %s\nError: %s",
		    											  file.getName(), exp.getMessage() ));
		    						return;
		    					}
		        			}
		        		}		    		
		    		}
		    	}
			}
		};
		chooser.addActionListener(listener);
		chooser.setVisible(true);
	}	
}

class ExportTask
{
	public static void run(	final String title,
				final Exporter exporter,
				final CyNetworkView view,
				final FileOutputStream stream){
		// Create the Task
		Task task = new Task(){
			TaskMonitor monitor;

			public String getTitle(){
				return title;
			}

			public void setTaskMonitor(TaskMonitor monitor){
				this.monitor = monitor;
			}

			public void halt(){}

			public void run()
			{
                try {
                    try {
                        exporter.export(view, stream);
                    }
                    catch (IOException e) {
                        monitor.setException(e, "Could not complete export of network");
                    }
                }
                finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (IOException ioe) {
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

/*
 * ExportFilter for the different formats.
 */
abstract class ExportFilter extends CyFileFilter
{
	protected boolean exportTextAsFont = false;
	public ExportFilter(String extension, String description)
	{
		super(extension, description);
	}

	public boolean isExtensionListInDescription()
	{
		return true;
	}

	public String toString()
	{
		return getDescription();
	}

	public void setExportTextAsFont(boolean pExportTextAsFont) {
		exportTextAsFont = pExportTextAsFont;
	}
	
	public boolean getExportTextAsFont() {
		return exportTextAsFont;
	}
	
	public abstract void export(CyNetworkView view, FileOutputStream stream);
}

class PDFExportFilter extends ExportFilter
{
	public PDFExportFilter()
	{
		super("pdf", "PDF");
	}
	public void export(final CyNetworkView view, final FileOutputStream stream)
	{
		PDFExporter exporter = new PDFExporter();
		exporter.setExportTextAsFont(this.getExportTextAsFont());
		ExportTask.run("Exporting to PDF", exporter, view, stream);
	}
}

class BitmapExportFilter extends ExportFilter
{
	private String extension;

	public BitmapExportFilter(String extension, String description)
	{
		super(extension, description);
		this.extension = extension;
	}

	public void export(final CyNetworkView view, final FileOutputStream stream)
	{
		final InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(view);
		final ExportBitmapOptionsDialog dialog = new ExportBitmapOptionsDialog(ifc.getWidth(), ifc.getHeight());
		ActionListener listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				BitmapExporter exporter = new BitmapExporter(extension, dialog.getZoom());
				dialog.dispose();
				ExportTask.run("Exporting to " + extension, exporter, view, stream);
			}
		};
		dialog.addActionListener(listener);
		dialog.setVisible(true);
	}
}

class SVGExportFilter extends ExportFilter
{
	public SVGExportFilter()
	{
		super("svg", "SVG");
	}

	public void export(final CyNetworkView view, final FileOutputStream stream)
	{
		SVGExporter exporter = new SVGExporter();
		exporter.setExportTextAsFont(this.getExportTextAsFont());
		ExportTask.run("Exporting to SVG", exporter, view, stream);
	}
}

class PSExportFilter extends ExportFilter
{
	public PSExportFilter(String extension, String description)
	{
		super(extension, description);
	}

	public void export(final CyNetworkView view, final FileOutputStream stream)
	{
		PSExporter exporter = new PSExporter();
		exporter.setExportTextAsFont(this.getExportTextAsFont());
		ExportTask.run("Exporting to EPS", exporter, view, stream);
	}
}
