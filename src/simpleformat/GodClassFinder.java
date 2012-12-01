/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleformat;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author saricas
 */
public class GodClassFinder implements DependencyPatternClassFinder {

    private List<Class> authorityClassList;
    private List<Class> hubClassList;
    private List<Class> godList;

    public GodClassFinder(List<Class> authorityClassList, List<Class> hubClassList) {
        this.authorityClassList = authorityClassList;
        this.hubClassList = hubClassList;
        this.godList = new ArrayList<Class>();
    }

    @Override
    public List<Class> find() {
        for (int i = 0; i < authorityClassList.size(); i++) {
            simpleformat.Class c = authorityClassList.get(i);
            if (hubClassList.contains(c)) {
                c.setType(simpleformat.Class.GOD);
                authorityClassList.remove(c);
                hubClassList.remove(c);
                godList.add(c);
                i--;
            }
        }
        return godList;
    }
}
