package partyblastr.spotify

import partyblastr.Track
import dispatch._
import net.liftweb.json._

class Spotify {
  implicit val formats = DefaultFormats

  def getSpotifyUri(track : Track) : Option[String] =  {
    try {
      Http(url("http://ws.spotify.com/search/1/track.json") <<? (("q", track.title + " " + track.artist) :: Nil) >- { str =>
        (parse(str) \\ "tracks").extract[List[SpotifyTrack]].headOption.map(_.href)
      })
    } catch {
      case e : Exception => {
        println("Error getting spotify track : " + e.getClass + ": " + e.getMessage)
        None
      }
    }
  }

  case class SpotifyTrack (name : String, href : String, artists : List[SpotifyArtist])
  case class SpotifyArtist (name : String,  href : String)
}