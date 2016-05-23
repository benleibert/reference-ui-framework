package org.openlmis.web;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.LinkedList;

//@EqualsAndHashCode(of = "title")
public class MenuItem
{
    //Properties explicitly specified within a json file
    @Getter @Setter private String title;
    @Getter @Setter private  LinkedList<MenuItem> children = new LinkedList<MenuItem>();
    @Getter @Setter private  LinkedList<MenuItem> permissions = new LinkedList<MenuItem>();

    //The path to the json file that specified our properties
    @Getter @Setter private  Path path;

    public MenuItem(String title)
    {
        this.title = title;
    }
    public MenuItem(String title, Path path)
    {
        this.title = title;
        this.path = path;
    }

    public boolean isLeaf()
    {
        return children.size() > 0;
    }

    public  boolean hasChildren()
    {
        return children.size() > 0;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof MenuItem))
            return false;

        MenuItem menuItem = (MenuItem) o;
        return this.title.equals(menuItem.title);
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }
}