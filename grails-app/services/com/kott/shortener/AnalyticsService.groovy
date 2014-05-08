package com.kott.shortener

import grails.transaction.Transactional

import java.security.KeyStore
import java.security.PrivateKey

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.Base64
import com.google.api.services.analytics.Analytics
import com.google.api.services.analytics.AnalyticsScopes
import com.google.api.services.analytics.Analytics.Data.Ga.Get

/**
 * This is the service to interact with google analytics stuffs
 * @author jmguilla
 *
 */
@Transactional(readOnly = true)
class AnalyticsService {

  // for holding the configuration
  def grailsApplication
  // for interacting with mappings
  def mappingService

  def HttpTransport HTTP_TRANSPORT = new NetHttpTransport()
  def JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance()
  def HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport()
  def Credential credential = {
    Set<String> scopes = new HashSet<String>();
    scopes.add(AnalyticsScopes.ANALYTICS);
    scopes.add(AnalyticsScopes.ANALYTICS_EDIT);
    scopes.add(AnalyticsScopes.ANALYTICS_MANAGE_USERS);
    scopes.add(AnalyticsScopes.ANALYTICS_READONLY);

    String pkAsBase64 = System.getenv("google-privatekey.base64");
    byte [] encoded = Base64.decodeBase64(pkAsBase64);
    KeyStore ks = KeyStore.getInstance("PKCS12");
    ks.load(new ByteArrayInputStream(encoded), null);
    return new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
    .setJsonFactory(JSON_FACTORY)
    .setServiceAccountId("1051867773103-ujcp2cs6gok2jopa9iop3uetav918a68@developer.gserviceaccount.com")
    .setServiceAccountScopes(scopes)
    .setServiceAccountPrivateKey((PrivateKey) ks.getKey("privatekey", "notasecret".toCharArray()))
    .build()
  }()

  def Analytics client = new Analytics.Builder(httpTransport, JSON_FACTORY, null)
  .setHttpRequestInitializer(credential)
  .setApplicationName("shortener").build()

  /**
   * Retrieves {@link Mapping} statistics based on google analytics data
   * @param mappings
   * 
   * @return empty object if parameter is null or empty, data formatted <a href="https://developers.google.com/analytics/devguides/reporting/core/v3/reference#output">as a dataTable</a> otherwise.
   */
  def retrieveStats(User user = null, Set<Mapping> mappings = new HashSet<Mapping>()) {
    Get get = client.data().ga().get(grailsApplication.config.google.analytics.statsProfileId , "2005-01-01", "today", "ga:visits");
    get.setDimensions("ga:pagePath");
    get.setMetrics("ga:pageviews");
    get.setSort("-ga:pageviews");
    if(user){
      get.setQuotaUser(user.id as String);
    }
    get.setOutput("dataTable");
    def pagePathFilter = ""
    if(mappings){
      for(int i = 0; i < mappings.size(); i++){
        pagePathFilter += "ga:pagePath==/" + mappingService.getShortId(mappings[i])
        if(i < mappings.size() - 1){
          pagePathFilter += ","
        }
      }
      get.setFilters(pagePathFilter)
    }
    get.execute()
  }
}
