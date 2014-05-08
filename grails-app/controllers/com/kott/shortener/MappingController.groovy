package com.kott.shortener

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional;

import com.kott.shortener.json.JSONMapping;
import com.kott.shortener.remuneration.PercentageSimple

class MappingController {

  def springSecurityService
  def mappingService
  def analyticsService

  static allowedMethods = [create:['GET', 'PUT'], retrieve: 'GET', getStats: 'GET']

  def index() {
  }

  /**
   * Display user mappings list
   * @return
   */
  @Secured(['IS_AUTHENTICATED_FULLY'])
  def list() {
    String view = 'list'
    respond(view: view)
  }

  /**
   * Displays mapping
   * @return
   */
  @Secured(['IS_AUTHENTICATED_FULLY'])
  def show() {
    String id = params.id

    String view = 'show'

    render view: view, model: [shortId: id]
  }

  /**
   * Retrieves a {@link Mapping} and render an html view from a shortId passed as an entry of params map.
   * See {@link UrlMappings} for the custom mapping.
   * 
   */
  @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
  def retrieve() {
    def result = [status: 200]
    if(!params.shortId){
      result.status = 400
      result.alert = 'danger'
      result.message = 'shortId parameter is missing'
    }else{
      result.mapping = mappingService.retrieveFromShortId(params.shortId)
      if(!result.mapping){
        result.status = 404
        result.alert = 'danger'
        result.message = message(code: 'rest.mapping.notfound', default: "No mapping with such shortId: ${params.shortId}", args: [params.shortId])
      }
    }
    if(result.mapping){
      if(!request.xhr){
        render view: "redirect", model: [mapping: result.mapping]
      }else{
        response.sendRedirect(result.mapping.target)
      }
    }else{
      response.sendError(result.status, result.message)
    }
  }
  
  /**
   * Retrieves a {@link Mapping} as JSON format from a shortId passed as an entry of params map.
   * See {@link UrlMappings} for the custom mapping.
   */
  @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
  def retrieveAsJSON() {
	def result = [status: 200]
	if(!params.shortId){
	  result.status = 400
	  result.alert = 'danger'
	  result.message = 'shortId parameter is missing'
	}else{
	  result.mapping = mappingService.retrieveFromShortId(params.shortId)
	  if(!result.mapping){
		result.status = 404
		result.alert = 'danger'
		result.message = message(code: 'rest.mapping.notfound', default: "No mapping with such shortId: ${params.shortId}", args: [params.shortId])
	  }
	}
	if(result.mapping){
	  def jsonMapping = JSONMapping.cloneMapping(result.mapping);
	  jsonMapping.shortUrl = g.createLink(absolute: true, uri: '/') + result.mapping.shortId;
	  result.mapping = jsonMapping;
	}
	response.status = result.status;
	render(result as JSON);
  }

  /**
   * Retrieves all mappings of current user
   * JSON: renders the appropriate object as JSON if it exists, otherwise, response.status == 404.
   *
   */
  @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
  def retrieveAll() {
    def result = [status: 200]
    Set<Mapping> mappings = mappingService.getUserMappings(springSecurityService.currentUser);

    result.mappings = mappings;

    if(!result.mappings){
      result.status = 404
      result.alert = 'danger'
      result.message = message(code: 'rest.mappings.notfound', default: "No mappings for current user", args: [])
    }
    withFormat{
      json{
        // create a new list for json result in order to keep transient values
        List jsonMappings = [];
        for (mapping in mappings){
          if(!mapping) continue;
          def jsonMapping=JSONMapping.cloneMapping(mapping);
          jsonMapping.shortUrl = g.createLink(absolute: true, uri: '/') + mapping.shortId;
          jsonMappings.push(jsonMapping);
        }
        response.status = result.status;
        result.mappings = jsonMappings;
        render(result as JSON)
      }
    }
  }

  /**
   * PUT: Creates a new {@link Mapping} and returns a JSON result with the following fields:
   * <ul>
   *   <li>alert: matching a value of <a href="http://getbootstrap.com/components/#alerts">http://getbootstrap.com/components/#alerts</a><li>
   *   <li>message: to describe what happened</a><li>
   *   <li>result: only in case of alert == 'success', the new mapping created as JSON object with additional shortUrl property
   * </ul>
   * params:
   * <ul>
   *  <li>target: the target url in a JSON object in request's body</li>
   * </ul>
   * 
   * GET: Renders the create view
   * 
   */
  @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
  def create() {
    if('get'.equalsIgnoreCase(request.method)){
      render view: 'create'
    }else if('put'.equalsIgnoreCase(request.method)){
      if(!request.JSON.target){
        response.status = 400
        render([
          alert: 'danger',
          message: message(code: 'rest.mapping.create.targetmissing', default: 'Target parameter required')
        ] as JSON)
      }else{
        Mapping result = new Mapping(target: request.JSON.target, user: springSecurityService.currentUser)
        if(!result.save(flush: true)){
          response.status = 400

          render([
            alert: 'danger',
            message: message(code: 'rest.mapping.create.failure', default: 'Url is not valid.', args: [/*result.errors as String*/'Url is not valid'])] as JSON)
        }else{
          def jsonMapping=JSONMapping.cloneMapping(result);
          jsonMapping.shortUrl = g.createLink(absolute: true, uri: '/') + result.shortId;

          render([
            alert: 'success',
            message: message(code: 'rest.mapping.create.success', default: 'Mapping created'),
            result: jsonMapping
          ] as JSON)
        }
      }
    }
  }

  /**
   * Retrieves the links' statistics for the authenticated user
   * @return the links' statistics for the authenticated user
   */
  @Secured(['IS_AUTHENTICATED_FULLY'])
  def stats(){
    Set<Mapping> mappings = mappingService.getUserMappings(springSecurityService.currentUser)
    def result = [:]
    if(mappings){
      result = analyticsService.retrieveStats(springSecurityService.currentUser, mappings)
    }
    withFormat{
      json{
        render([alert: 'success', message: message(code: 'rest.mapping.statistics.retrieved', default: 'Mappings statistics retrieved successfully'), result: result] as JSON)
      }
      '*'{ render(result) }
    }
  }

  @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
  @Transactional
  def remuneration(){
    PercentageSimple remunerationAlgorith = new PercentageSimple()
    render (remunerationAlgorith.computeRemuneration(analyticsService, 1000, User.list()) as JSON)
  }
}
