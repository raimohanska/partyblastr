package partyblastr

import lastfm.LastFM
import org.scalatra._
import net.liftweb.json._
import scala.collection.immutable.HashMap

class PartyServlet extends ScalatraServlet {
  implicit val formats = DefaultFormats
  val idGenerator : IdGenerator = new RandomIdGenerator
  val lastFM = new LastFM
  private var parties = new HashMap[String, Party]

  post("/party") {
    val party = Party(idGenerator.nextId, Nil)
    response.setHeader("Location", request.getRequestURL.toString + "/" + party.id)
    saveAndRenderParty(party)
  }
  get("/party/:id") {
    processPartyOrError(render)
  }
  get("/party/:id/playlist") {
    processPartyOrError { party =>
      val lastFmTracks = lastFM.getPlaylistForUsers(party.members.map(member => member.username))
      val tracks = lastFmTracks.map(track => Track(track.artist.name, track.name))
      render(Playlist(tracks))
    }
  }
  post("/party/:id/members") {
    processPartyOrError { party =>
      saveAndRenderParty(party.copy(members = party.members :+ Member(request.body)))
    }
  }

  def render(content: AnyRef) = {
    contentType = "application/json"
    net.liftweb.json.Serialization.write(content)
  }

  def saveAndRenderParty(party: Party) = {
    parties += (party.id -> party)
    response.setStatus(201)
    render(party)
  }
  def processPartyOrError(handleParty: (Party) => String) = {
    parties.get(params("id")) match {
      case Some(party) => handleParty(party)
      case None => halt(404, "Party not found")
    }
  }
}

case class Party(id: String, members : List[Member])
case class Member(username: String)
case class Track(artist : String,  title : String)
case class Playlist(tracks : List[Track])
