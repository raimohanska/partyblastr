package partyblastr

import lastfm.LastFM
import org.scalatra._
import net.liftweb.json._
import scala.collection.immutable.HashMap

class PartyServlet extends ScalatraServlet {
  implicit val formats = DefaultFormats
  val idGenerator : IdGenerator = new RandomIdGenerator

  post("/party") {
    contentType = "application/json"
    response.setStatus(201)
    val party = Party(idGenerator.nextId, Nil)
    response.setHeader("Location", request.getRequestURL.toString + "/" + party.id)
    render(party)
  }
  get("/party/:id") {
  }
  get("/party/:id/playlist") {
  }
  post("/party/:id/members") {
  }

  def render(content: AnyRef) = net.liftweb.json.Serialization.write(content)
}

case class Party(id: String, members : List[Member])
case class Member(username: String)
case class Track(artist : String,  title : String)
case class Playlist(tracks : List[Track])
