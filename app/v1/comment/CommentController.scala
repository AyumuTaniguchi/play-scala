package v1.comment

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class CommentFormInput(content: String, contributor_name: String)

/**
  * Takes HTTP requests and produces JSON.
  */
class CommentController @Inject()(cc: CommentControllerComponents)(
    implicit ec: ExecutionContext)
    extends CommentBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[CommentFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "content" -> text,
        "contributor_name" -> nonEmptyText
      )(CommentFormInput.apply)(CommentFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = CommentAction.async { implicit request =>
    logger.trace("index: ")
    commentResourceHandler.find.map { comments =>
      Ok(Json.toJson(comments))
    }
  }

  def create: Action[AnyContent] = CommentAction.async { implicit request =>
    logger.trace("process: ")
    createJsonComment()
  }

  private def createJsonComment[A]()(
      implicit request: CommentRequest[A]): Future[Result] = {
    def failure(badForm: Form[CommentFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: CommentFormInput) = {
      commentResourceHandler.create(input).map { comment =>
        Created(Json.toJson(comment)).withHeaders(LOCATION -> comment.link)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
