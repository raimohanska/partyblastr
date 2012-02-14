package partyblastr

import lastfm.LastFM
import org.scalatra._
import net.liftweb.json._
import scala.collection.immutable.HashMap

class PartyServlet extends ScalatraServlet {
  implicit val formats = DefaultFormats
  val idGenerator : IdGenerator = new RandomIdGenerator
  private var parties = new HashMap[String, Party]

  post("/party") {
    response.setStatus(201)
    val party = Party(idGenerator.nextId, Nil)
    response.setHeader("Location", request.getRequestURL.toString + "/" + party.id)
    parties += (party.id -> party)
    renderParty(party)
  }
  get("/party/:id") {
    parties.get(params("id")) match {
      case Some(party) => renderParty(party)
      case None => halt(404, "Party not found")
    }
  }
  get("/party/:id/playlist") {
  }
  post("/party/:id/members") {
  }

  def render(content: AnyRef) = net.liftweb.json.Serialization.write(content)
  def renderParty(party: Party) = {
    contentType = "application/json"
    render(party)
  }
}

case class Party(id: String, members : List[Member])
case class Member(username: String)
case class Track(artist : String,  title : String)
case class Playlist(tracks : List[Track])
