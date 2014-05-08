package com.kott.shortener

import grails.transaction.Transactional

import org.apache.commons.codec.binary.Base64

@Transactional
class MappingService {

  /**
   * Returns the shortId built from the {@link Mapping} passed as input parameter.
   * 
   * @param mapping an instance of {@link Mapping} which is stored in database.
   * @return the shortId built from the {@link Mapping} passed as input parameter.
   * @throws IllegalStateException if mapping is null or has no ID
   */
  @Transactional(readOnly = true)
  def String getShortId(Mapping mapping) throws IllegalStateException {
    if(!mapping || !mapping.id){
      throw new IllegalStateException("Mapping passed as input parameter is null or has no ID: ${mapping}")
    }
    return new String(Base64.encodeBase64(mapping.id as byte[], false, true))
  }

  /**
   * Returns the {@link Mapping} matching the shortId passed as input parameter.
   * @param shortId a String
   * @return the {@link Mapping} matching the shortId passed as input parameter or null if no such {@link Mapping} exists.
   * @throws IllegalStateException if shortId is null
   */
  @Transactional(readOnly = true)
  def Mapping retrieveFromShortId(String shortId) throws IllegalStateException{
    if(!shortId){
      throw new IllegalStateException("shortId passed as input parameter is null")
    }
    Long id = new BigInteger(Base64.decodeBase64(shortId)).toLong()
    return Mapping.get(id)
  }
  
  /**
   * Retrieves the {@link Mapping} of the currently authenticated user
   * @return
   */
  @Transactional(readOnly = true)
  def Set<Mapping> getUserMappings(User user){
    return user.mappings
  }
}
