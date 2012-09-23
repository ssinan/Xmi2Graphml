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
public class CycleClassFinder implements DependencyPatternClassFinder {

    private List<Class> classList;
    private List<Class> cycleClassList;

    public CycleClassFinder(List<Class> classList) {
        this.classList = classList;
        this.cycleClassList = new ArrayList<Class>();
    }

    @Override
    public List<Class> find() {

        cycleClassList.clear();
        for (Class klass : classList) {

            if (klass.getOutList().contains(klass)) {
                klass.setType(Class.CYCLE);
                cycleClassList.add(klass);
            }
        }

        return cycleClassList;
    }

}
