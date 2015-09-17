package module.dao

import awscala.dynamodbv2._
import com.google.inject.ImplementedBy
import models.CarAdvert


@ImplementedBy(classOf[CarAdvertDaoImpl])
trait CarAdvertDao {
  def findAll(): List[CarAdvert]

  def findById(id: String): Option[CarAdvert]

  def create(carAdvert: CarAdvert): Unit

}

class CarAdvertDaoImpl extends CarAdvertDao {

  implicit val dynamoDb: DynamoDB = DynamoDB.local()

  def createTable() = {
    val tableMeta: TableMeta = dynamoDb.createTable(
      name = "car-adverts", hashPK = "guid" -> AttributeType.String, rangePK = "title" -> AttributeType.String,
    otherAttributes = Seq(
    ),
    indexes = Seq(LocalSecondaryIndex(
      name = "car-adverts-index",
      keySchema = Seq(
        KeySchema("guid", KeyType.Hash),
        KeySchema("title", KeyType.Range)
      ),
      projection = Projection(ProjectionType.All)
    ))
    )
  }

  override def create(carAdvert: CarAdvert): Unit = {
    dynamoDb.table("car-adverts") match {
      case Some(table) => {
          table.put(
            hashPK = carAdvert.guid,
            rangePK = carAdvert.title,
            "fuel" -> carAdvert.fuel,
            "price" -> carAdvert.price,
            "new" -> (if (carAdvert.isNew) 1 else 0),
            "mileage" -> carAdvert.mileage,
            "firstRegistration" -> (if (carAdvert.isNew) -1 else carAdvert.firstRegistration.getTime)
          )

        table.destroy()
      }
      case None => {
        createTable()
        create(carAdvert)
      }
    }
  }

  override def findById(id: String): Option[CarAdvert] = {
    dynamoDb.table("car-adverts") match {
      case Some(table) => {
        table. match {
          case item :: Nil => {
            Some(CarAdvert.toObject(item))
          }
          case Nil => None
        }
      }
      case None => {
        createTable()
        findById(id)
      }
    }
  }

  override def findAll(): List[CarAdvert] = ???
}