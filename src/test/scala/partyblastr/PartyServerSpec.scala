package partyblastr

import lastfm.{Artist, LastFM}
import org.scalatra.test.specs2.MutableScalatraSpec

class PartyServerSpec extends MutableScalatraSpec {
  args(sequential=true)
  val servlet = new PartyServlet {
    /*
    override val lastFmApi = new LastFM {
      override def getPlaylistForUsers(usernames: List[String]) = lastfm.Track("Hallelujah", Artist("Jeff Buckley")) :: Nil
    }
    */
    override val idGenerator = new StaticIdGenerator
  }
  addServlet(servlet, "/*")
  "Non-existent party" should {
    "return 404" in {
      get("/party/asdf") {
        status must_== 404
      }
    }
  }
  "Creating party" should {
    "return 201 CREATED" in {
      postJson("/party", "") {
        status must_== 201
      }
    }
    "Include location header for following GET requests" in {
      postJson("/party", "") {
        response.getHeader("Location") must be matching("http://.*/party/1")
      }
    }
    "return party as JSON in response body" in {
      postJson("/party", "") {
        body must_== """{"id":"1","members":[]}"""
      }
    }
    "Use application/json content type" in {
      postJson("/party", "") {
        assertJsonContentType
      }
    }
  }

  "Empty party" should {
    "have zero members" in {
      postJson("/party", "") {}
      getJson("/party/1") {
        body must_== """{"id":"1","members":[]}"""
      }
    }
  }
  /*
  "Newly added member" should {
    "be included in party" in {
      postJson("/party", "") {}
      postJson("/party/1/members", """haerski""") {
        body must_== """{"id":"1","members":[{"username":"haerski"}]}"""
      }
      getJson("/party/1") {
        body must_== """{"id":"1","members":[{"username":"haerski"}]}"""
      }
    }
  }
  "Party playlist" should {
    "contain artists and song titles" in {
      getJson("/party/1/playlist") {
        body must_== """{"tracks":[{"artist":"Jeff Buckley","title":"Hallelujah"}]}"""
      }
    }
  }
  */
  /*
  "Spotify URIs" should {
    "be provided for songs" in {
      getJson("/party/1/spotify") {
        body must_== """["spotify:track:7bzwk5mvjHiIxfGrsuAiO7"]"""
      }
    }
  }
  */
  def postJson(url: String, body: String, statusCode: Int = 201)(assertions: => Unit) = {
    post(url, body) { jsonAssertions(assertions, statusCode) }
  }
  def getJson(url: String, statusCode: Int = 200)(assertions: => Unit) = {
    get(url) { jsonAssertions(assertions, statusCode) }
  }
  def jsonAssertions(assertions: => Unit, statusCode: Int) = {
    assertions
    status must_== statusCode
    assertJsonContentType
  }
  def assertJsonContentType = response.getContentType must_== ("application/json;charset=UTF-8")
}

class StaticIdGenerator extends IdGenerator {
  def nextId = "1"
}