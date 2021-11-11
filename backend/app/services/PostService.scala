package services

import com.google.inject.Inject
import models._
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, Created, Ok}

import java.time.LocalDate
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}


class PostService @Inject()(postList: PostList, likeList: LikeList,
                            friendsList: FriendshipList)(implicit val ec: ExecutionContext) {

  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  def getTimeline(loggedInId: Long): Future[Seq[PostWithLiked]] = {
    val friendIds: mutable.Set[Long] = mutable.Set(loggedInId)
    val likedPosts: mutable.Set[Long] = mutable.Set()
    val retVal: mutable.ListBuffer[PostWithLiked] = mutable.ListBuffer()

    // collecting ids of friends
    friendsList.getFriends(loggedInId).map(friendships => {
      friendships.foreach(friendship => {
        friendIds += (friendship.user1Id, friendship.user2Id)
      })
    }).flatMap(_ => {
      //get posts logged in user liked
      likeList.findPostIdsLikedByUser(loggedInId).map(likes => {
        likes.foreach(like => likedPosts += like.postId)
      })
    })
      // convert post to post with likes info
      .flatMap(_ => {
        println("Friends are " + friendIds)
        println("My liked posts are " + likedPosts)
        postList.getTimeLine(friendIds).map(posts => {
          posts.foreach(p => {
            retVal += PostWithLiked(p.id, p.userId, p.firstName, p.lastName, p.picture,
              p.text, p.likes, p.created, likedPosts.contains(p.id))
          })
        }).flatMap(_ => Future(retVal.sortBy(_.created)(Ordering[LocalDate].reverse)))
      })
  }


  def getPostsFromUserWithLike(myId: Long, otherId: Long): Future[Seq[PostWithLiked]] = {
    val likedPosts: mutable.Set[Long] = mutable.Set()
    val retVal: mutable.ListBuffer[PostWithLiked] = mutable.ListBuffer()

    //get posts logged in user liked
    likeList.findPostIdsLikedByUser(myId).map(likes => {
      likes.foreach(like => likedPosts += like.postId)
    }).flatMap(_ => {
      postList.getTimeLine(mutable.Set(otherId)).map(posts => {
        posts.foreach(p => {
          retVal += PostWithLiked(p.id, p.userId, p.firstName, p.lastName, p.picture,
            p.text, p.likes, p.created, likedPosts.contains(p.id))
        })
      }).flatMap(_ => Future(retVal.sortBy(_.created)(Ordering[LocalDate].reverse)))
    })
  }

  def createPost(dto: PostCreationDTO, uId: Long): Future[Result] = {
    if (dto.text.length <= 1) Future(BadRequest("Post text cannot be empty!"))
    else {
      val newPost = Post(0, uId, dto.text, 0, LocalDate.now())
      postList.add(newPost).flatMap(_ => Future(Created("Post is created!")))
    }
  }

  def deleteItem(id: Long): Future[Int] = {
    //delete all likes for that post and then the post itself
    likeList.deleteLikesForPost(id).flatMap(_ => {
      postList.delete(id)
    })
  }

  def updatePost(dto: PostCreationDTO, uId: Long, postId: Long): Future[Result] = {
    if (dto.text.length <= 1) Future(BadRequest("Post text cannot be empty!"))
    else {
      getItem(postId).flatMap(post => {
        if (post.isEmpty) {
          Future(BadRequest("Post with id " + postId + " does not exist!"))
        } else {
          if (post.get.userId != uId) {
            Future(BadRequest("You cannot update other user's post!"))
          } else {
            val temp = post.get
            updateItem(Post(temp.id, temp.userId, dto.text, temp.likes, temp.created)).map(_ => Ok("Post updated"))
          }
        }
      })
    }
  }

  def updateItem(item: Post): Future[Int] = {
    postList.update(item)
  }

  def getItem(id: Long): Future[Option[Post]] = {
    postList.get(id)
  }

  def listAllItems: Future[Seq[Post]] = {
    postList.listAll
  }

  def likePost(l: Like): Future[Result] = {
    likeList.add(l).flatMap(retVal => {
      if (retVal == "Like successfully added") {
        getItem(l.postId).flatMap(likedPostOption => {
          val likedPost = likedPostOption.get
          likedPost.likes = likedPost.likes + 1
          updateItem(likedPost)
        }).map(_ => Created("Post liked"))
      } else
        Future(BadRequest("Cannot like a post twice"))
    })
  }

  def dislikePost(uId: Long, pId: Long): Future[Result] = {
    likeList.delete(uId, pId).flatMap(retVal => {
      if (retVal == 1) {
        //update like counter for post
        getItem(pId).flatMap(likedPostOption => {
          val likedPost = likedPostOption.get
          likedPost.likes = likedPost.likes - 1
          updateItem(likedPost)
        }).map(_ => Ok("Post disliked"))
      } else
        Future(BadRequest("Cannot dislike a not liked post"))
    })

  }

  def findPostLikeByUser(uId: Long): Future[Seq[Like]] = {
    likeList.findPostIdsLikedByUser(uId)
  }

}
