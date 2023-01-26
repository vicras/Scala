package com.vicras
package domain

sealed trait Mark

object Mark {
  case object TIC extends Mark

  case object TAC extends Mark

  case object TOE extends Mark

  def getOpposite(mark: Mark): Mark = mark match {
    case TAC => TIC
    case TIC => TAC
    case TOE => TOE
  }

  def toWinnerStatus(mark: Mark): GameStatus = mark match {
    case TAC => GameStatus.TacWon
    case TIC => GameStatus.TicWon
    case TOE => GameStatus.InProgress
  }
}
