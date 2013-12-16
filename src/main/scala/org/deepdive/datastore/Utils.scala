package org.deepdive.datastore

import org.deepdive.Logging
import org.deepdive.settings._
import spray.json._
import anorm._

object Utils extends Logging {

  type AnormSeq = Seq[(String, ParameterValue[_])]  

  /* 
   * Converts a JSON objects row to a object that can be handled by Anorm
   * Example: ["John", 15, True] => AnormSeq(...)
   */
  implicit def jsonRowToAnormSeq(row: JsObject) : AnormSeq = {
    jsonRowsToAnormSeq(List(row)).head
  }

  /* 
   * Converts a multiple JSON objects rows to a object that can be handled by Anorm
   * Example: [["John", 15, True], ...] => Seq[AnormSeq(...), ...]
   */
  implicit def jsonRowsToAnormSeq[T <% Iterable[JsObject]]
    (rows: T): Seq[AnormSeq] = {
    rows.map { row =>
      row.fields.mapValues { 
        case(JsNull) => toParameterValue(null)
        case(x: JsString) => toParameterValue(x.value)
        case(x: JsNumber) => toParameterValue(x.value.toLong)
        case(x: JsBoolean) => toParameterValue(x.value)
        case (value) => 
          log.error("Found value of type ${value.getClass.name} which is not supported.")
          toParameterValue(null)
      }.toSeq
    }.toSeq
  }
}
