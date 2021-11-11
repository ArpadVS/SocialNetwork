package controllers

import models.{Like, Post, PostCreationDTO}
import play.api.libs.json.JsResult.Exception
import play.api.libs.json._
import play.api.mvc._
import services.{FriendService, PostService}
import utils.{AuthenticatedAction, JwtUser, JwtUtil}

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostController @Inject()(
                                cc: ControllerComponents,
                                postService: PostService,
                                friendService: FriendService,
                                authenticatedAction: AuthenticatedAction)
  extends AbstractController(cc) {

  implicit val postFormat: OFormat[Post] = Json.format[Post]
  implicit val formatUserDetails: OFormat[JwtUser] = Json.format[JwtUser]


  def createPost: Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    try {
      val detail: Option[PostCreationDTO] = request.body.asJson.get.asOpt[PostCreationDTO]
      val userId: Long = getUserIdFromToken(request)
      if (detail.isDefined && userId != -1) {
        postService.createPost(detail.get, userId)
      } else Future(BadRequest("Request body has wrong format!"))
    } catch {
      case _: Exception => Future(BadRequest("No body for request!"))
    }
  }

  def update(id: Long): Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    try {
      val detail: Option[PostCreationDTO] = request.body.asJson.get.asOpt[PostCreationDTO]
      val userId: Long = getUserIdFromToken(request)
      if (detail.isDefined && userId != -1) {
        postService.updatePost(detail.get, userId, id)
      } else Future(BadRequest("Request body has wrong format!"))
    } catch {
      case _: Exception => Future(BadRequest("No body for request!"))
    }
  }

  def delete(id: Long): Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    val userId: Long = getUserIdFromToken(request)
    postService.getItem(id).flatMap(optP => {
      if (optP.isDefined) {
        val uIdPost = optP.get.userId
        if (uIdPost == userId) {
          postService.deleteItem(id).flatMap(deleted => {
            if (deleted == 1) {
              Future(Ok("Post deleted"))
            } else {
              Future(InternalServerError("Something went wrong"))
            }
          })
        } else Future(BadRequest("Cannot delete other user's post!!"))
      } else Future(BadRequest("There is no post with given id!"))
    })
  }

  def getTimeline: Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    val loggedInId = getUserIdFromToken(request)
    postService.getTimeline(loggedInId).map { posts =>
      Ok(Json.toJson(posts))
    }
  }

  def likePost(postId: Long): Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>

    val jwtToken = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken).fold {
      Future.successful(Unauthorized("Invalid credential"))
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get
      postService.likePost(Like(0, postId = postId, userId = userInfo.id))
    }
  }

  def dislikePost(postId: Long): Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    val jwtToken2 = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken2).fold {
      Future.successful(Unauthorized("Invalid credential"))
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get
      postService.dislikePost(uId = userInfo.id, pId = postId)
    }
  }


  def getAll: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    postService.listAllItems.map { item =>
      Ok(Json.toJson(item))
    }
  }

  def getPostsFromUser(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val userId: Long = getUserIdFromToken(request)

    //  only friends can see other user's posts
    friendService.areFriends(userId, id).flatMap(areFriends => {
      if (areFriends || (userId == id)) {
        postService.getPostsFromUserWithLike(userId, id).map { posts =>
          Ok(Json.toJson(posts))
        }
      } else Future(BadRequest("Cannot view non-friend user's posts!!"))
    })
  }

  def getById(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    postService.getItem(id) map { item =>
      Ok(Json.toJson(item))
    }
  }

  private def getUserIdFromToken(request: Request[AnyContent]): Long = {
    val jwtToken2 = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken2).fold {
      val ret: Long = -1
      ret
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get
      userInfo.id
    }
  }

}
