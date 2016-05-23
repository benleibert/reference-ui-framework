package org.openlmis.web.view;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.json.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import org.openlmis.utils.*;
import org.openlmis.web.MenuItem;

@Component
public class MenuView extends AbstractView {

    protected static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    public MenuView() {
        setContentType(CONTENT_TYPE);
    }


    @Override
    protected void renderMergedOutputModel(Map arg0, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        response.setContentType("application/xml; charset=UTF-8");

        //TODO: Determine how this value will be provided. Perhaps via a config file?
        String mountLocation = getServletContext().getRealPath("/public/sample-mount");

        response.getWriter().write(getMenuHtml(mountLocation));
    }

    private String getMenuHtml(String mountLocation) throws IOException
    {
        PaddedStringBuilder sb = new PaddedStringBuilder();
        sb.append("<div class=\"navigation toggleFullScreen\" ng-controller=\"NavigationController\" ui-nav>");
            sb.append("<ul class=\"clearfix\" style=\"display:none\">");

            sb.append( getInnerMenuHtml(mountLocation) );

            sb.append("</ul>");
        sb.append("</div>");

        sb.append("<div class=\"navigation-locale-bar\">");
            sb.append("<ng-include src=\"'/public/pages/locale-list.html'\" />");
        sb.append("</div>");

        return sb.toString();
    }

    private String getInnerMenuHtml(String mountLocation) throws IOException
    {
        LinkedList<Path> paths = getMenuDataFilePaths(mountLocation);
        LinkedList<MenuItem> topLevelMenuItems = getMenuItems(paths);
        String menuHTML = getMenuHtml(topLevelMenuItems);

        //Use Jsoup to nicely format out tags
        return Jsoup.parse(menuHTML, "", Parser.xmlParser()).toString();
    }

    private String getMenuHtml(LinkedList<MenuItem> menuItems)
    {
        PaddedStringBuilder sb = new PaddedStringBuilder();
        for(MenuItem topMenuItem : menuItems)
        {
            sb.append("<li>");
            sb.append("     <a href=\"javascript: void(0);\">" + topMenuItem.getTitle() + "</a>");
            sb.append(      getSubMenuHtml(topMenuItem) );
            sb.append("</li>");
        }

        return sb.toString();
    }



    private String getSubMenuHtml(MenuItem menuItem)
    {
        PaddedStringBuilder sb = new PaddedStringBuilder();

        if(menuItem.hasChildren()) {
            sb.append("     <div class=\"submenu\">");
            sb.append("         <ul>");

            for (MenuItem subMenu : menuItem.getChildren())
            {
                if( ! subMenu.hasChildren())
                {
                    sb.append("             <li>");
                    sb.append("                 <a href=\"" + getFullHtmlPathForMenu(subMenu) + "\">" + subMenu.getTitle() + "</a>");
                    sb.append("             </li>");
                }
                else
                {
                    sb.append("             <li class=\"has-children\">");
                    sb.append("                 <a href=\"javascript: void(0);\">" + subMenu.getTitle() + "</a>");
                    for (MenuItem sub2 : menuItem.getChildren())
                        sb.append(getSubMenuHtml(sub2));
                    sb.append("             </li>");
                }
            }

            sb.append("         </ul>");
            sb.append("     </div>");
        }

        return sb.toString();
    }

    private LinkedList<MenuItem> getMenuItems(LinkedList<Path> paths) throws IOException
    {
        LinkedList<MenuItem> topLevelMenuItems = new LinkedList<MenuItem>();

        Iterator i = paths.iterator();
        while (i.hasNext())
        {
            Path path = (Path)i.next();
            String fileContent = getFileContent(path);
            JSONObject jsonObject = new JSONObject(fileContent);
            JSONArray menuHierarchy = jsonObject.getJSONArray("menuHierarchy");

            if(menuHierarchy.length() < 1)
                continue;

            //Add top-level item
            String title = menuHierarchy.getString(0);
            MenuItem item_1 = new MenuItem(title, path);
            int indexOf = topLevelMenuItems.indexOf(item_1);
            if(indexOf > -1)
                item_1 = topLevelMenuItems.get(indexOf);
            else {
                item_1 = new MenuItem(title, path);
                topLevelMenuItems.add(item_1);
            }

            if(menuHierarchy.length() < 2)
                continue;

            //Add second-level item
            title = menuHierarchy.getString(1);
            MenuItem item_2 = new MenuItem(title, path);
            indexOf = item_1.getChildren().indexOf(item_2);
            if(indexOf > -1)
                item_2 = item_1.getChildren().get(indexOf);
            else {
                item_2 = new MenuItem(title, path);
                item_1.getChildren().add(item_2);
            }

            if(menuHierarchy.length() < 3)
                continue;

            //Add third-level item
            title = menuHierarchy.getString(2);
            MenuItem item_3 = new MenuItem(title, path);
            indexOf = item_2.getChildren().indexOf(item_3);
            if(indexOf > -1)
                item_3 = item_1.getChildren().get(indexOf);
            else {
                item_3 = new MenuItem(title, path);
                item_2.getChildren().add(item_3);
            }
        }

        return topLevelMenuItems;
    }


    private static LinkedList<Path> getMenuDataFilePaths(String rootDirectory) throws IOException
    {
        LinkedList<Path> paths = new LinkedList<>();
        Files.find(Paths.get(rootDirectory), 2, (p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().endsWith("json")).forEach(file -> paths.add(file));
        return paths;
    }

    private static String getFileContent(Path path) throws IOException
    {
        try(BufferedReader br = new BufferedReader(new FileReader(path.toString())))
        {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
    }

    private String getFullHtmlPathForMenu(MenuItem menuItem)
    {
        String path = menuItem.getPath().toAbsolutePath().toString().replace("menuData.json" , "index.html");
        return getRelativePath(path);
    }

    private String getRelativePath(String fullPath)
    {
        return fullPath.replace(getServletContext().getRealPath("") , "");
    }
}