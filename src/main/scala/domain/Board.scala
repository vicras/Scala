package com.vicras
package domain

sealed abstract case class Board private(matrix: Array[Array[Mark]], size: Int) {
  def updateBoard(move: Move): Either[String, Board] = {
    val coordinate = move.coordinate
    if (coordinate.i >= size) Left(s"Coordinate i=${coordinate.i} to large, max value=${size - 1}")
    else if (coordinate.j >= size) Left(s"Coordinate j=${coordinate.j} to large, max value=${size - 1}")
    else if (matrix(coordinate.i)(coordinate.j) != Mark.TOE) Left("Field already contain value")
    else {
      matrix(coordinate.i)(coordinate.j) = move.mark; // TODO use immutable collection
      Right(this)
    }
  }

  override def toString: String = {
    matrix.map(row => row.map {
      case Mark.TAC => "o"
      case Mark.TIC => "x"
      case Mark.TOE => "_"
    }.mkString("|", " ", "|"))
      .mkString("\n")
  }
}

object Board {
  def of(size: Int): Option[Board] = {
    if (size > 1) Some(new Board(Array.tabulate[Mark](size, size)((_, _) => Mark.TOE), size) {})
    else None
  }
}
