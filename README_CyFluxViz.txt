--------------------------------------------------------------------------
CyFluxViz-v0.93 for Cytoscape-v2.8.3
--------------------------------------------------------------------------
We are pleased to announce the release of CyFluxViz
which is available for download at

http://sourceforge.net/projects/fluxvizplugin/

CyFluxViz an open-source Cytoscape plugin for the 
visualization of flux distributions in molecular interaction 
networks.

The main features are
- import of networks (SBML, GML, XGMML, SIF, BioPAX, PSI-MI)
- import of flux distributions in a variety of formats
- subnetworks based on flux carrying reactions (flux subetwork)
  or arbitrary network attributes
- flexible mapping architecture for all visual attributes
- export of views in variety of formats (SVG, EPS, PDF, BMP, PNG)
- batch capabilities

Please try and report any problems you encounter using CyFluxViz to
matthias.koenig [at] charite.de

Thanks and have fun 
The CyFluxViz team.

--------------------------------------------------------------------------
 Copyright (c) 2013, Matthias Koenig, Computational Systems Biochemistry, 
 Charite Berlin
 matthias.koenig [at] charite.de

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
--------------------------------------------------------------------------
Disclaimer: There exist still some minor bugs using multiple networks with multiple
		 FluxDistributions. These will be removed in the next release. Currently, 
		 CyFluxViz should be used to visualize various FluxDistributions within
		 one network.
--------------------------------------------------------------------------
v0.93 [2013-08-07] bugfix release
- attribute subnetwork functionality fixed
- updated CyFluxViz style (SBML localization & irreversibel reactions)

v0.92 [2013-08-01] mayor release
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

v0.91
- icon loader NullPointer Exception fixed

v0.88
- smaller bugfixes
- better file import


v0.86 (Mayor Release)
- full support & compatibility to CySBML
- XML import and export of Flux Distributions
- support of multiple networks and subnetworks
- complete redesign of architecture for speed improvement 
- new example files (packed in Jar)

--------------------------------------------------------------------------
Matthias Koenig
Computational Systems Biochemistry
Institute of Biochemistry
Charité - Universitätsmedizin Berlin
Charité Crossover (CCO), Raum 04 321, Virchowweg 6, 10117 Berlin
Germany
http://www.charite.de/sysbio/people/koenig/

Tel: + 49 30 450 528 197
Email: matthias.koenig [at] charite.de 
--------------------------------------------------------------------------