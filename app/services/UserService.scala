package services

import com.google.inject.Inject
import models.{User, UserList}

import scala.concurrent.Future


class UserService @Inject()(items: UserList) {
  def register(item: User): Future[String] = {
    items.add(item)
  }

  def listAllItems: Future[Seq[User]] = {
    items.listAll
  }

  def findByEmail(email:String): Future[User]= {
    items.findByEmail(email)
  }

  def findById(id: Long): Future[Option[User]] = {
    items.get(id)
  }

}
