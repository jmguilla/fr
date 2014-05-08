package com.kott.shortener


class Mapping {

  def mappingService

  def long id

  def String target

  def String description

  def String shortId
  
  def Date creationDate
  
  static transients = ["shortId"]
  static belongsTo = [user: User]

  static constraints = {
    user nullable: true;
	description nullable: true;
    target blank: false, nullable: false, size: 1..20000, validator: {val, obj, errors ->
      try{
        new URL(val)
        return true
      }catch(Throwable t){
        errors.rejectValue('target', 'mapping.target.url.error', 'Target must be a valid URL')
      }
    }
  }
  
  def beforeValidate(){
    if(!creationDate){
      creationDate = new Date()
    }
  }
  
  def afterInsert(){
    computeShortId()
  }

  def afterUpdate(){
    computeShortId()
  }

  def onLoad(){
    computeShortId()
  }

  def computeShortId(){
    this.shortId = mappingService.getShortId(this)
  }
}
