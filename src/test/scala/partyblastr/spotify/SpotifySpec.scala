package partyblastr.spotify

import org.specs2.mutable.Specification
import partyblastr.Track

class SpotifySpec extends Specification {
 "Spotify API" should {
    "return URI for a well-known track" in {
      val track = new Spotify().getSpotifyUri(Track("Jeff Buckley", "Grace"))
      track match {
        case None => failure("Track not found")
        case Some(uri) => { uri must_== "spotify:track:4zIYBKi1u2A3QoUexWx2wZ" }
      }
    }

    "return empty list for bogus user" in {
      val track = new Spotify().getSpotifyUri(Track("qwadsvrb", "24fawwagwe"))
      track match {
        case None => success
        case Some(uri) => failure("Not expected to be found")
      }
    }
  }
}