package com.vicras
package domain

sealed trait GameStatus

object GameStatus {
  object EmptyField extends GameStatus

  object InProgress extends GameStatus

  object TicWon extends GameStatus

  object TacWon extends GameStatus

  object Draw extends GameStatus
}
