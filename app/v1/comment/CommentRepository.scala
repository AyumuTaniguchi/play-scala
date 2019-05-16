package v1.comment

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class CommentData(id: CommentId, content: String, contributor_name: String)

class CommentId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object CommentId {
  def apply(raw: String): CommentId = {
    require(raw != null)
    new CommentId(Integer.parseInt(raw))
  }
}

class CommentExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the CommentRepository.
  */
trait CommentRepository {
  def create(data: CommentData)(implicit mc: MarkerContext): Future[CommentId]

  def list()(implicit mc: MarkerContext): Future[Iterable[CommentData]]

  def get(id: CommentId)(implicit mc: MarkerContext): Future[Option[CommentData]]
}

/**
  * A trivial implementation for the Comment Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class CommentRepositoryImpl @Inject()()(implicit ec: CommentExecutionContext)
    extends CommentRepository {

  private val logger = Logger(this.getClass)

  private val commentList = List(
    CommentData(CommentId("1"), "content 1", "contributor_name 1"),
    CommentData(CommentId("2"), "content 2", "contributor_name 2"),
    CommentData(CommentId("3"), "content 3", "contributor_name 3"),
    CommentData(CommentId("4"), "content 4", "contributor_name 4"),
    CommentData(CommentId("5"), "content 5", "contributor_name 5")
  )

  override def list()(
      implicit mc: MarkerContext): Future[Iterable[CommentData]] = {
    Future {
      logger.trace(s"list: ")
      commentList
    }
  }

  override def get(id: CommentId)(
      implicit mc: MarkerContext): Future[Option[CommentData]] = {
    Future {
      logger.trace(s"get: id = $id")
      commentList.find(comment => comment.id == id)
    }
  }

  def create(data: CommentData)(implicit mc: MarkerContext): Future[CommentId] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }

}
