package com.kott.shortener

import com.kott.shortener.User

class FBUser {

	long uid
  String accessToken
  Date accessTokenExpires

	static belongsTo = [user: User]

	static constraints = {
		uid unique: true
	}
}
