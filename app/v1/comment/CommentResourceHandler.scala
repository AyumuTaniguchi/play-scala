package v1.comment

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying comment information.
  */
case class CommentResource(id: String, link: String, title: String, body: String)

object CommentResource {
  /**
    * Mapping to read/write a CommentResource out as a JSON value.
    */
    implicit val format: Format[CommentResource] = Json.format
}


/**
  * Controls access to the backend data, returning [[CommentResource]]
  */
class CommentResourceHandler @Inject()(
    routerProvider: Provider[CommentRouter],
    commentRepository: CommentRepository)(implicit ec: ExecutionContext) {

  def create(commentInput: CommentFormInput)(
      implicit mc: MarkerContext): Future[CommentResource] = {
    val data = CommentData(CommentId("999"), commentInput.content, commentInput.contributor_name)
    // We don't actually create the comment, so return what we have
    commentRepository.create(data).map { id =>
      createCommentResource(data)
    }
  }

  def lookup(id: String)(
      implicit mc: MarkerContext): Future[Option[CommentResource]] = {
    val commentFuture = commentRepository.get(CommentId(id))
    commentFuture.map { maybeCommentData =>
      maybeCommentData.map { commentData =>
        createCommentResource(commentData)
      }
    }
  }

  def find(implicit mc: MarkerContext): Future[Iterable[CommentResource]] = {
    commentRepository.list().map { commentDataList =>
      commentDataList.map(commentData => createCommentResource(commentData))
    }
  }

  private def createCommentResource(c: CommentData): CommentResource = {
    CommentResource(c.id.toString, routerProvider.get.link(c.id), c.content, c.contributor_name)
  }

}
