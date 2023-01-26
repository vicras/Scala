package com.vicras
package domain

sealed abstract case class Move(coordinate: Coordinate, mark: Mark)

object Move {
  def of(coordinate: Coordinate, mark: Mark) = new Move(coordinate, mark) {}
}
