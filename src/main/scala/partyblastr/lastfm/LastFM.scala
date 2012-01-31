package partyblastr.lastfm

import dispatch._
import net.liftweb.json._

class LastFM {
  implicit val formats = DefaultFormats

  def getPlaylistForUsers(usernames: List[String]) : List[Track] = {
    new PlaylistCombinator().combine(usernames.map(username => getPlaylistForUser(username))).take(20)
  }

  protected def getPlaylistForUser(username : String) = {
    try {
      Http(url("http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&user=" + username + "&api_key=7f374b4805f2b2a2fb7504772139c3f0&format=json") >- { str =>
        (parse(str) \\ "track").extract[List[Track]]
      })
    } catch {
      case e : Exception => {
        println("Error getting playlist : " + e.getClass + ": " + e.getMessage)
        Nil
      }
    }
  }
}

class PlaylistCombinator {
  def combine(playlists: List[List[Track]]) : List[Track] = {
    val scoredList : List[(Track,  Float)] = playlists.map(scorePlaylist).flatten
    val grouped : Map[Track, List[(Track,  Float)]] = scoredList.groupBy(_._1)
    val scoredTracks : Map[Track,  Float] = grouped.mapValues(scores => scores.map(_._2).reduceLeft(_+_))
    scoredTracks.toList.sortBy(_._2).reverse.map(_._1)
  }

  protected def scorePlaylist(tracks : List[Track]) : List[(Track, Float)] = {
    tracks.zipWithIndex.map {case (t, index) => (t, (1f / (index + 1)))}
  }
}

case class Track(name: String, artist: Artist)
case class Artist(name: String)
