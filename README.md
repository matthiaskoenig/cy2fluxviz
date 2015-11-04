# cy2fluxviz for Cytoscape 2
<div align="right>
<a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&amp;hosted_button_id=RYHNRJFBMWD5N" title="Donate to this project using Paypal"><img src="https://img.shields.io/badge/paypal-donate-yellow.svg" alt="PayPal donate button" /></a>
</div>

![alt tag](docs/images/logo-cyfluxviz.png) **cy2fluxviz** is an open-source [Cytoscape 2](http://www.cytoscape.org) plugin for the visualization of flux distributions in networks. 

[![Download](docs/images/icon-download.png) Download](https://github.com/matthiaskoenig/cy2fluxviz/releases/latest)  
**Documentation** : http://matthiaskoenig.github.io/cy2fluxviz/  
**Support & Forum** : https://groups.google.com/forum/#!forum/cysbml-cyfluxviz  
**Bug Tracker** : https://github.com/matthiaskoenig/cy2fluxviz/issues  

**cy2fluxviz** is currently ported to Cytoscape 3, with first versions available at [cy3fluxviz](https://github.com/matthiaskoenig/cy3fluxviz).

## Features
* import of networks (SBML, GML, XGMML, SIF, BioPAX, PSI-MI)
* import of flux distributions in a variety of formats
* subnetworks based on flux carrying reactions (flux subetwork)
  or arbitrary network attributes
* flexible mapping architecture for all visual attributes
* export of views in variety of formats (SVG, EPS, PDF, BMP, PNG)
* batch capabilities

## License
* Source Code: [GPLv3](http://opensource.org/licenses/GPL-3.0)
* Documentation: [CC BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)

## Installation
### Requirements
* Java 8
* Cytoscape v2.8.3  
    http://www.cytoscape.org/download.html  
    http://chianti.ucsd.edu/Cyto-2_8_3/
* cy2sbml

### Install
* download latest release jars for cy2fluxviz and cy2sbml  
    https://github.com/matthiaskoenig/cy2fluxviz/releases/latest  
    https://github.com/matthiaskoenig/cy2sbml/releases/latest
* move `cy2sbml-vx.x.x.jar` and `cy2fluxviz-vx.x.x.jar` in the Cytoscape plugin folder under `Cytoscape_v2.8.3/plugins/`.  
* remove `sbml-reader-2.8.3-jar-with-dependencies.jar` from the plugin folder

cy2fluxviz and cy2sbml are installed and available in the plugins menu after restarting Cytoscape.

### Start cy2fluxviz
The plugin is loaded and installed during the next Cytoscape startup. To start cyfluxviz click the cyfluxviz icon in the Cytoscape toolbar ![CyFluxViz logo](/images/logo-cyfluxviz.png) which will load the cyfluxviz panel.

## Build instructions
Clone the repository and build with `ant`
```
git clone https://github.com/matthiaskoenig/cy2fluxviz.git cy2fluxviz
cd cy2fluxviz
ant cy2fluxviz
```

# Changelog
=======
## Changelog
**v1.0.1** [?]

**v1.0.0** [2015-11-03]
- fixes empty FluxDistribution bug after loading #1
- updated logging

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
