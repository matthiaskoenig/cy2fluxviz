package fluxviz.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;


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
import fluxviz.CyFluxVizPlugin;
import fluxviz.attributes.FluxAttributeUtils;
import fluxviz.view.NetworkView;

/**
 * Action for exporting a network view to bitmap or vector graphics.
 * Multiple gra
 * 
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
	private static CyFluxVizPlugin fluxViz;
	
	
	public ExportAsGraphics()
	{
	}

	public void actionPerformed()
	{
		// General selection of format and settings for all images
		final ExportAsGraphicsFileChooser chooser = new ExportAsGraphicsFileChooser(FILTERS);
		ActionListener listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// here is whats happening if the ok button is clicked
				ExportFilter filter = (ExportFilter) chooser.getSelectedFormat();
				filter.setExportTextAsFont(chooser.getExportTextAsFont());
				
				// Folder for export is choosen
				File folder = chooser.getSelectedFile(); 

				chooser.dispose();
				
				// get selected attributes in list
		    	String[] attributes = FluxAttributeUtils.getSelectedAttributes(ExportAsGraphics.fluxViz);
		    	if (attributes.length == 0){
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
							"No flux distributions are selected for export.",
							"Select flux distributions", JOptionPane.WARNING_MESSAGE);
		    	}    	
				FileOutputStream stream = null;
		    	
				//TODO: remove the .val from the attribute names
		    	for (int i=0; i<attributes.length; ++i){	
		    		
		    		String extension = (String) filter.getExtensionSet().toArray()[0];
		            File file = new File(folder, attributes[i]+ "." + extension);
		            System.out.println(file.getAbsolutePath());
		            
					try
					{
						stream = new FileOutputStream(file);
					}
					catch (Exception exp)
					{
						JOptionPane.showMessageDialog(	Cytoscape.getDesktop(),
															"Could not create file " + file.getName()
															+ "\n\nError: " + exp.getMessage());
						return;
					}
		    		//Apply the view for the attribute and create the image
		            NetworkView.applyFluxVizView(attributes[i]);
					CyNetworkView view = Cytoscape.getCurrentNetworkView();
					filter.export(view, stream);            
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
				final FileOutputStream stream)
	{
		// Create the Task
		Task task = new Task()
		{
			TaskMonitor monitor;

			public String getTitle()
			{
				return title;
			}

			public void setTaskMonitor(TaskMonitor monitor)
			{
				this.monitor = monitor;
			}

			public void halt()
			{
			}

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
