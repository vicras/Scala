package com.vicras
package domain

sealed case class GameMetrics private(metrics: Map[Int, Int], boardSize: Int) {
  def this(fieldSize: Int) = this(0.until(fieldSize * 2 + 2).map(i => i -> 0).toMap, fieldSize)

  def checkWining: Boolean = metrics.values.exists(value => value.abs == boardSize)

  def update(move: Move): GameMetrics = {
    val defaultIntValue: Int = 0
    val row = move.coordinate.i
    val column = move.coordinate.j
    val modFunc: Option[Int] => Option[Int] = move.mark match {
      case Mark.TIC => a => a.map(_ + 1).orElse(Some(defaultIntValue))
      case Mark.TAC => a => a.map(_ - 1).orElse(Some(defaultIntValue))
    }

    var stat = metrics.updatedWith(row)(modFunc)
    stat = stat.updatedWith(boardSize + column)(modFunc)
    stat = if (column == row) stat.updatedWith(boardSize * 2)(modFunc) else stat
    stat = if (boardSize - 1 - column == row) stat.updatedWith(boardSize * 2 + 1)(modFunc) else stat
    GameMetrics(stat, boardSize)
  }
}