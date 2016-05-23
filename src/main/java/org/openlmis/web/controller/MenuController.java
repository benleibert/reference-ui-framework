/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;


import org.openlmis.web.view.MenuView;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MenuController extends MultiActionController
{
  ApplicationContext context;


  @RequestMapping(value = "/defaultMenu", method = GET)
  public ModelAndView dataViewHandler(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    ModelAndView mv = null;
    try
    {
      //TODO: Pass in the location of our mounted drive as part of the model. Currently, it's left up to MenuView to guess a default.
      Map<String, Object> model = new HashMap<String, Object>();

      MenuView view = new MenuView();
      mv = new ModelAndView(view, model);

      //For now, the particular view we're returning depends on our ApplicationContext (via a call to getServletContext) . Initialize our view such that it has this reference.
      this.getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, "menuView_" + System.currentTimeMillis());
    }
    catch (Exception e)
    {}
    return mv;
  }
}


