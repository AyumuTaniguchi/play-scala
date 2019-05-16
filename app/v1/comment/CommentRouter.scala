package v1.comment

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the CommentResource controller.
  */
class CommentRouter @Inject()(controller: CommentController) extends SimpleRouter {
  val prefix = "/comments"

  def link(id: CommentId): String = {
    import com.netaporter.uri.dsl._
    val url = prefix / id.toString
    url.toString()
  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

    case POST(p"/") =>
      controller.create
  }

}
