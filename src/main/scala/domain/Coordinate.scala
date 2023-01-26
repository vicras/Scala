package com.vicras
package domain

sealed abstract case class Coordinate(i: Int, j: Int)

object Coordinate {
  def of(i: Int, j: Int): Option[Coordinate] = {
    if (i < 0 || j < 0) None
    else Some(new Coordinate(i, j) {})
  }
}