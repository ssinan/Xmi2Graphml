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
public class AuthorityClassFinder implements DependencyPatternClassFinder {

    private List<Class> classList;
    private List<Class> authorityClassList;
    private int edgeCount;
    private float dependencyPercentage;

    public AuthorityClassFinder(List<Class> classList, int edgeCount, float dependencyPercentage) {
        this.classList = classList;
        this.edgeCount = edgeCount;
        this.dependencyPercentage = dependencyPercentage;
        this.authorityClassList = new ArrayList<Class>();
    }

    @Override
    public List<Class> find() {

        authorityClassList.clear();
        for (Class klass : classList) {
            float depPer = (float)klass.getInList().size() / edgeCount;
            if (depPer > dependencyPercentage) {
                klass.setType(Class.AUTHORITY);
                authorityClassList.add(klass);
            }
        }

        return authorityClassList;
    }

}
