import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.{ JsResult, Json }
import play.api.mvc.{ RequestHeader, Result }
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._
import v1.comment.CommentResource

import scala.concurrent.Future

class CommentRouterSpec extends PlaySpec with GuiceOneAppPerTest {
  "CommentRouter" should {
    "render the list of comments" in {
      val request = FakeRequest(GET, "/comments").withHeaders(HOST -> "localhost:9000").withCSRFToken
      val home:Future[Result] = route(app, request).get
      val comments: Seq[CommentResource] = Json.fromJson[Seq[CommentResource]](contentAsJson(home)).get

      comments.filter(_.id == "1").head mustBe (CommentResource("1" ,"/comments/1", "content 1", "contributor_name 1"))
    }
  }
}
