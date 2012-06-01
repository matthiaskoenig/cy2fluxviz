FluxViz Plugin for Cytoscape v.0.1
----------------------------------------
The FluxViz is an open source Cytoscape Plugin which implements the visualization of flux distributions.

Main Features
--------------
(i) cycle through flux distributions 
(ii) visualize the flux containing subgraph in the full network context
(iii) export flux modes as images


Installation 
------------
The plugin supports Cytoscape Version 2.6. The Latest Cytoscape release is available at http://www.cytoscape.org/.

Fluxviz is available at https://sourceforge.net/projects/fluxvizplugin/.
The plugin is installed by moving the 'fluxviz-vX.X.jar' to the plugins folder in the Cytoscape installation directory.
FluxViz is loaded and installed during the next Cytoscape start and available through the FluxViz panel in the Control Panel.

The installation of SBMLReader2 is recommended. The stoichiometry attribute of SBML files is not parsed in the core SBMLReader, but necessary for the correct visualisation of fluxes.
SBMLReader2 is available at https://sourceforge.net/projects/sbmlreader2/.

Alternatively FluxViz can be installed via the Cytoscape Plugin Manager.
Detailed installation manual and FluxViz information are available at 
http://www.charite.de/sysbio/people/koenig/software/fluxviz/help/.


Documentation & Help
--------------------
Documentation, help, tutorial, source code and API documentation are included in the jar file of the plugin and available after the plugin installation at
$HOME/.cytoscape/2.6/plugins/FluxVizData/help/
$HOME/.cytoscape/2.6/plugins/FluxVizData/doc/

Example files after installed in 
$HOME/.cytoscape/2.6/plugins/FluxVizData/examples/
and are also available in the jar

Additional information about this project is available at
https://sourceforge.net/projects/fluxvizplugin/


FluxViz v0.14
- logger bux fixed (now logging in fluxviz.log in installation directory)
- image export as PDF, SVG, EPS, JPEG, PNG, BMP
- additional subnetwork features like attribute subnetworks
- FluxMapping to set the flux -> edgeWidth mapping in a flexible way
- bug flux direction removed & lots of smaller bugfixes
- new FluxViz VisualStyle
- Attribute subnets without flux distributions.
- Full compatibility to Cytoscape 2.6 and 2.7

FluxViz v0.12
- tabbed menu for info, examples and help
- example loading integrated in menu
- some code refactoring

FluxViz v0.11
- buxfix windows installer and network reload
- logger implemented
- new help and readme system
- examples

FluxViz v0.1
- many bugfixes
- installer for the tutorials and the example files
- jar extractor 
- work on tutorial and help menu
- examples


Contact:
--------
Project site:
http://www.charite.de/sysbio/people/koenig/software/fluxviz/help/

Source code and download:
or use https://sourceforge.net/projects/fluxvizplugin/

Report bugs, ask questions or send feature request to
Matthias Koenig (matthias.koenig[at]charite.de)
http://www.charite.de/sysbio/people/koenig/

