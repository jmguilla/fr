package com.kott.shortener.remuneration

import grails.converters.JSON

import com.google.api.services.analytics.model.GaData
import com.google.api.services.analytics.model.GaData.DataTable.Rows
import com.kott.shortener.AnalyticsService
import com.kott.shortener.Mapping
import com.kott.shortener.User

/**
 * Very simple algorithme.
 * For Every user, depending on the total number of views of their links they get the
 * ratio they performed...
 * @author jmguilla
 */
class PercentageSimple {

  /**
   * 
   * @param amountToShare
   * @param usersToMappings
   * @return
   */
  public Map<User, Integer> computeRemuneration(AnalyticsService analyticsService, Integer amountToShare, List<User> users){
    //TODO externalize the configuration
    def toShare = amountToShare * 0.5 //Percentage of the total earning of the web site
    GaData stats = analyticsService.retrieveStats()
    int totalViews = 0
    Map<User, Integer> usersToViews = [:]
    // first, retrieve the stats for all the mappings
    for(Rows row in stats.getDataTable().getRows()){
      //TODO improve retrieval of appropriate columns
      def page = row.getC()[0].getV()
      def views = Integer.parseInt(row.getC()[1].getV(), 10)
      for(User user in users){
        for(Mapping mapping in user.mappings){
          // mapping shortId has no leading /
          if(("/" + mapping.shortId).equals(page)){
            totalViews += views
            Integer userCurrentViews = views
            Integer userTotalViews = usersToViews[user]
            if(userTotalViews){
              usersToViews[user] = userTotalViews + userCurrentViews 
            }else{
              usersToViews[user] = userCurrentViews
            }
          }
        }
      }
    }
    
    Map<User, Integer> result = [:]
    for(User user in users){
      Integer nbViews = usersToViews[user]
      if(!nbViews){
        result[user] = 0
      }else{
        result[user] = toShare * totalViews / nbViews
      }
    }
    
    return result
  }
}
