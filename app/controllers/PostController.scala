package controllers

import models.{Post, PostForm}
import play.api.libs.json._
import play.api.mvc._
import services.PostService

import java.time.LocalDate
import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostController @Inject()(
                                cc: ControllerComponents,
                                postService: PostService)
  extends AbstractController(cc) {

  implicit val postFormat: OFormat[Post] = Json.format[Post]

  def getAll: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    postService.listAllItems.map { item =>
      Ok(Json.toJson(item))
    }
  }

  def getById(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    postService.getItem(id) map { item =>
      Ok(Json.toJson(item))
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    PostForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Error!"))
      },
      data => {
        val newPost = Post(0, data.userId, data.text, data.likes, LocalDate.now())
        // change redirect later to 200 Ok
        postService.addItem(newPost).map(_ => Redirect(routes.PostController.getAll))
      })
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    PostForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Error!"))
      },
      data => {

        val postItem = Post(id, data.userId, data.text, data.likes, LocalDate.now())
        // change redirect later to 200 Ok
        postService.updateItem(postItem).map(_ => Redirect(routes.PostController.getAll))
      })
  }

  def delete(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    postService.deleteItem(id) map { _ =>
      // change redirect later to 200 Ok
      Redirect(routes.PostController.getAll)
    }
  }

  def checkStatus(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok("Got request [" + request + "]")
  }
}
