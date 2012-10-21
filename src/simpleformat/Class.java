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
public class Class {

    public static final int NONCLASSIFIED = 0;
    // Single class dependency patterns
    public static final int AUTHORITY = 1;
    public static final int HUB = 2;
    public static final int CYCLE = 3;
    // Multi class dependency patterns
    public static final int ISLAND = 4;
    public static final int BRIDGE = 5;
    // This one is a defect type
    public static final int GOD = 6;

    private String name;
    private List<Class> inList;
    private List<Class> outList;
    private int type = 0;
    private int tag;

    public Class(String name) {
        this.name = name;
        inList = new ArrayList<Class>();
        outList = new ArrayList<Class>();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the inList
     */
    public List<Class> getInList() {
        return inList;
    }

    /**
     * @return the outList
     */
    public List<Class> getOutList() {
        return outList;
    }

    public boolean addInClass(Class klass) {
        return this.addClass(getInList(), klass);
    }

    public boolean addOutClass(Class klass) {
        return this.addClass(getOutList(), klass);
    }

    private boolean addClass(List<Class> list, Class klass) {
        if (!list.contains(klass)) {
            list.add(klass);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Class) {
            if (this.name == null ? ((Class) obj).getName() == null
                    : this.name.equals(((Class) obj).getName())) {
                return true;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the tag
     */
    public int getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(int tag) {
        this.tag = tag;
    }

}
