package fluxviz.fluxanalysis;


import fluxviz.CyFluxVizPlugin;


public class UIDrawHist{
	HistPanel fOutputPanel = null;
	Histogram fHistogram;
	
	public void init(){
	    //Container content_pane = getContentPane ();
		System.out.println("Init Histogramm");
	    //JPanel panel = new JPanel (new BorderLayout ());
	    //JPanel panel = FluxViz.getFvPanel().getHistogramPanel();
	    
	    // Create a histogram with Gaussian distribution.
	    makeHist();

	    // JPanel subclass here.
	    fOutputPanel = new HistPanel (fHistogram);
	    CyFluxVizPlugin.getFvPanel().setHistogramPanel(fOutputPanel);
	    fOutputPanel.setVisible(true);
	    
	    //panel.add (fOutputPanel,"Center");	
	    // Add text area with scrolling to the contentPane.
	    //content_pane.add (panel);
	}

	
    
    /** Create a histogram if it doesn't yet exit. Fill it
     * with Gaussian random distribution.
    **/
   void makeHist () {
	 int fNumDataPoints = 100;
     // Create an instance of the Random class for
     // producing our random values.
     java.util.Random r = new java.util.Random ();

     // Them method nextGaussian in the class Random produces
     // a value centered at 0.0 and a standard deviation of 1.0.

     // Create an instance of our histogram class. Set the range
     // sot that it includes most of the distribution.
     if (fHistogram == null)
         fHistogram = new Histogram ("Gaussian Distribution",
                                   "random values",
                                   20,-3.0,3.0);

     // Fill histogram with Gaussian distribution
     for (int i=0; i < fNumDataPoints; i++) {
         double val = r.nextGaussian ();
         fHistogram.add (val);
     }
   } // makeHist
}
