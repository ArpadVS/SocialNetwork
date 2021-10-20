package services

import com.google.inject.Inject
import models.{Post, PostList}

import scala.concurrent.Future


class PostService @Inject()(items: PostList) {
  def addItem(item: Post): Future[String] = {
    items.add(item)
  }

  def deleteItem(id: Long): Future[Int] = {
    items.delete(id)
  }

  def updateItem(item: Post): Future[Int] = {
    items.update(item)
  }

  def getItem(id: Long): Future[Option[Post]] = {
    items.get(id)
  }

  def listAllItems: Future[Seq[Post]] = {
    items.listAll
  }
}
