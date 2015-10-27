# cy2fluxviz for Cytoscape 2
<a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&amp;hosted_button_id=RYHNRJFBMWD5N" title="Donate to this project using Paypal"><img src="https://img.shields.io/badge/paypal-donate-yellow.svg" alt="PayPal donate button" /></a>

**cy2fluxviz** is an open-source Cytoscape 2 plugin for the visualization of flux distributions in molecular interaction  networks. **cy2fluxviz** is ported to Cytoscape 3 via [cy3fluxfix](https://github.com/matthiaskoenig/cy3fluxviz).

The main features are
* import of networks (SBML, GML, XGMML, SIF, BioPAX, PSI-MI)
* import of flux distributions in a variety of formats
* subnetworks based on flux carrying reactions (flux subetwork)
  or arbitrary network attributes
* flexible mapping architecture for all visual attributes
* export of views in variety of formats (SVG, EPS, PDF, BMP, PNG)
* batch capabilities

Additional information is available from  
http://www.charite.de/sysbio/people/koenig/software/cyfluxviz3/

**Status** : release  
**Support & Forum** : https://groups.google.com/forum/#!forum/cysbml-cyfluxviz  
**Bug Tracker** : https://github.com/matthiaskoenig/cy2fluxviz/issues  

## License
* Source Code: [GPLv3](http://opensource.org/licenses/GPL-3.0)
* Documentation: [CC BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)


## Changelog
**v0.95** [2015-10-26]
- small bug fix release and rebuild with java 8
- sourceforge to github migration

**v0.94** [2014-01-24]
- java 1.6 compatibility (bug fix)

**v0.93** [2013-08-07] bugfix release
- attribute subnetwork functionality fixed
- updated CyFluxViz style (SBML localization & irreversibel reactions)

**v0.92** [2013-08-01] mayor release
- implemented: changes due to Cytoscape from v2.8.1 -> v2.8.3
- implemented: sortable flux distribution table (id, name, network as keys)
- bug resolved: image export broken (now bitmap export settings are reused for multiple picture)
- implemented: new Mappings and VisualStyles implementation 
				(remove of deprecated methods and adaption to v2.8.3 implementation)
- bug resolved: attribute selection bug (flux related attributes were not made visible)
- bug resolved: initial max-min mapping not properly applied
- implemented: support of multiple VisualStyles & C13 and kinetic style
- bug resolved: flux distribution table was editable with side effects
- bug resolved: selected nodes and edges were lost when FluxDistribution changed.
- bug resolved: global max setting for mapping was not applied properly
- implemented: support of child networks (create additional views for network)
- bug resolved: Network selection changed the focused windows
- implemented: focus CyFluxViz panel via icon click
- implemented: integration with CyFluxViz Toolbox for Matlab for visualization
- bug resolved: NullPointerExceptions during deleting of FluxDistributions 
- implemented: importer & exporter rewritten 

**v0.91**
- icon loader NullPointer Exception fixed

**v0.88**
- smaller bugfixes
- better file import

**v0.86** (Mayor Release)
- full support & compatibility to CySBML
- XML import and export of Flux Distributions
- support of multiple networks and subnetworks
- complete redesign of architecture for speed improvement 
- new example files (packed in Jar)
