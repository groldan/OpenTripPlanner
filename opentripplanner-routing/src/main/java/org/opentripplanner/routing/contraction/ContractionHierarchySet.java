/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package org.opentripplanner.routing.contraction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.opentripplanner.routing.core.Graph;
import org.opentripplanner.routing.core.TraverseOptions;
import org.opentripplanner.routing.core.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractionHierarchySet implements Serializable {
    private static Logger _log = LoggerFactory.getLogger(ContractionHierarchySet.class); 

    private static final long serialVersionUID = -8621085480392710082L;
    
    private Graph graph;
    private HashMap<TraverseOptions, ContractionHierarchy> hierarchiesByMode = new HashMap<TraverseOptions, ContractionHierarchy>();
    private List<TraverseOptions> modeList;

    private double contractionFactor = 1.0;

    public ContractionHierarchySet() {
        modeList = new ArrayList<TraverseOptions>();
    }
    
    public ContractionHierarchySet(Graph graph, List<TraverseOptions> modeList, double contractionFactor) {
        this.modeList = modeList;
        this.graph = graph;
        this.contractionFactor = contractionFactor;
    }

    public ContractionHierarchySet(Graph graph, List<TraverseOptions> modeList) {
        this(graph, modeList, 1.0);
    }

    public void addTraverseOptions(TraverseOptions mando) {
        modeList.add(mando);
    }
    
    public void setGraph(Graph g) {
        graph = g;
    }

    public ContractionHierarchy getHierarchy(TraverseOptions options) {
        TraverseOptions bestOptions = null;
        int bestSimilarity = TraverseOptions.MIN_SIMILARITY; // A minimum score of 1000 is required

        if(modeList == null)
            return null;

        for(TraverseOptions optionsH : modeList) {
            int similarity = optionsH.similarity(options);
            if(similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestOptions = optionsH;
            }
        }

        return bestOptions != null ? hierarchiesByMode.get(bestOptions) : null;
    }
    
    public void build() {
        if (modeList == null) {
            return;
        }
        _log.debug("Building contraction hierarchies for " + modeList.size() + " modes");
        for (TraverseOptions mo : modeList) {
            _log.debug("Building contraction hierarchy for " + mo);
            ContractionHierarchy ch = new ContractionHierarchy(getGraph(), mo, contractionFactor);
            hierarchiesByMode.put(mo, ch);
        }
        /* TODO: cross-hierarchy Shortcut memory optimization */
    }

    public void setContractionFactor(double factor) {
        contractionFactor = factor;
    }

    public Graph getGraph() {
        return graph;
    }
}
