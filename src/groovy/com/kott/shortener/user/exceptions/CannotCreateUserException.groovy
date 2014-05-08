package com.kott.shortener.user.exceptions

import com.kott.shortener.User
import com.kott.shortener.UserRole

class CannotCreateUserException extends RuntimeException {
  String message
  User user
  UserRole userRole

  CannotCreateUserException(String message, User user, UserRole userRole){
    super(message)
    this.user = user
    this.userRole = userRole
  }
}
