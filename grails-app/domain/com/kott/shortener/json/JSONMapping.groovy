package com.kott.shortener.json

import com.kott.shortener.Mapping;

/**
 * Mapping object ready for json exchange.
 * Contains transient values and additional data
 * 
 * @author Wildduck
 *
 */
class JSONMapping {
	
	def String id
	
	def String target
  
	def String description
  
	def String shortId
	
	def String shortUrl
	
	def static cloneMapping(Mapping mapping){
		def clone = new JSONMapping();
		
		clone.id = mapping.id;
		clone.target = mapping.target;
		clone.description = mapping.description;
		clone.shortId = mapping.shortId;
		
		return clone;
	}
}
