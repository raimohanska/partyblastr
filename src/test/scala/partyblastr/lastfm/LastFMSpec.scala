package partyblastr.lastfm

import org.specs2.mutable.Specification

class LastFMSpec extends Specification {
  "LastFM API" should {
    "return tracks for given last.fm usernames" in {
      val tracks = new LastFM().getPlaylistForUsers("simula77" :: Nil)
      tracks.foreach(assertValidTrack)
      tracks.size must be_>(0)
    }

    "return empty list for bogus user" in {
      val tracks = new LastFM().getPlaylistForUsers("0q3iowkejfsdhkh" :: Nil)
      tracks.size must be_==(0)
    }
  }

  def assertValidTrack(t: Track) {
    t.name must_!= ""
    t.artist.name must_!= ""
  }
}

class PlaylistCombinatorSpec extends Specification {
  val track1 = Track("name1", Artist("artist1"))
  val track2 = Track("name2", Artist("artist2"))
  val track3 = Track("name3", Artist("artist3"))

  val testData = List(List(track1, track2, track3), List(track3))

  "PlaylistCombinator" should {
    "preserve only one playlist" in {
      new PlaylistCombinator().combine(List(List(track1, track2, track3))) must_== List(track1, track2, track3)
    }
    "combine playlist using popularity" in {
      new PlaylistCombinator().combine(testData) must_== List(track3, track1, track2)
    }
  }
}