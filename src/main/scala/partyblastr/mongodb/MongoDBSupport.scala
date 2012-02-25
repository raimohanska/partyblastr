package partyblastr.mongodb

import com.mongodb.casbah.Imports
import com.mongodb.casbah.commons.{Imports => CommonsImports}
import com.mongodb.casbah.query.{Imports => QueryImports}
import com.mongodb.ServerAddress
import partyblastr.{Member, Party}

//  val server = new ServerAddress("localhost")
  val server = new ServerAddress("staff.mongohq.com", 10095)
class MongoStorage extends MongoDBSupport {
  lazy val mongoDB = {
    val db = MongoConnection(server)("partyblastr")
    db.authenticate("partyblastr", "partyblastr")
    db
  }
  protected def partyCollection = mongoDB("party")
  def findParty(id: String) = partyCollection.findOne(MongoDBObject("id" -> id)).map(toObject[Party])
  def addMember(party: Party, member: Member) = partyCollection.findAndModify(
    MongoDBObject("id" -> party.id), null, null, false, $push ("members" -> toDBObject(member)), true, true).map(toObject[Party])
  def addParty(party: Party) = partyCollection.findAndModify(
    MongoDBObject("id" -> party.id), null, null, false, toDBObject(party), true, true).map(toObject[Party])
}

trait MongoDBSupport extends Imports with CommonsImports with QueryImports {
  import com.novus.salat._
  implicit val ctx = new Context {
    val name = "CustomContext"
    override val typeHintStrategy = StringTypeHintStrategy(TypeHintFrequency.WhenNecessary)
  }
  def toObject[A <: CaseClass](dbObject: DBObject)(implicit m: Manifest[A]) = grater[A].asObject(dbObject)
  def toDBObject[A <: CaseClass](a: A)(implicit m: Manifest[A]) = grater[A].asDBObject(a)
}
