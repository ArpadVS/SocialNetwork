package services

import com.google.inject.Inject
import models.{LoginFormData, User, UserList}
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.{Duration, MILLISECONDS, SECONDS}


class UserService @Inject()(items: UserList) {

  def tryLogin(data: LoginFormData): Boolean = {
    var loginSuccess = false
    println("Before loginSuccess = " + loginSuccess.toString)
    println("Data is " + data.toString)
    val f = items.findByEmail(data.email).map(user => {
      println("User is " + user)
      if (user.email == data.email) { //just for sure
        println("Emails are equal")
        if (BCrypt.checkpw(data.password, user.password)) {
          loginSuccess = true
          println("Login is successful in tryLogin " + loginSuccess)
        }
      }
    })
    //could be changed
    Await.result(f, Duration(3, SECONDS))
    println("After loginSuccess = " + loginSuccess.toString)
    loginSuccess
  }

  def register(item: User): Future[String] = {
    items.add(item)
  }

  def listAllItems: Future[Seq[User]] = {
    items.listAll
  }

  def findByEmail(email: String): Future[User] = {
    items.findByEmail(email)
  }

  def findById(id: Long): Future[Option[User]] = {
    items.get(id)
  }

}
