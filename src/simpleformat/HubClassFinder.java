/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpleformat;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sinan
 */
public class HubClassFinder implements DependencyPatternClassFinder {

    private List<Class> classList;
    private List<Class> hubClassList;
    private int edgeCount;
    private float dependencyPercentage;

    public HubClassFinder(List<Class> classList, int edgeCount, float dependencyPercentage) {
        this.classList = classList;
        this.edgeCount = edgeCount;
        this.dependencyPercentage = dependencyPercentage;
        this.hubClassList = new ArrayList<Class>();
    }

    @Override
    public List<Class> find() {

        hubClassList.clear();
        for (Class klass : classList) {
            float depPer = (float)klass.getOutList().size() / edgeCount;
            if (depPer > dependencyPercentage) {
                klass.setType(Class.HUB);
                hubClassList.add(klass);
            }
        }

        return hubClassList;
    }

}
