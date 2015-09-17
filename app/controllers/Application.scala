package controllers

import com.google.inject.Inject
import module.dao.CarAdvertDao
import play.api.libs.json._
import play.api.mvc._
import models.Book._

class Application @Inject() (carAdvertDao: CarAdvertDao) extends Controller {

  def listCarAdverts = Action {
    Ok(Json.toJson(carAdvertDao.findAll()))
  }

  def listBooks = Action {
    Ok(Json.toJson(books))
  }

  def saveBook = Action(BodyParsers.parse.json) { request =>
    val b = request.body.validate[Book]
    b.fold(
      errors => {
        BadRequest(Json.obj("status" -> "OK", "message" -> JsError.toFlatJson(errors)))
      },
      book => {
        addBook(book)
        Ok(Json.obj("status" -> "OK"))
      }
    )
  }
}
