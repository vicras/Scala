package com.vicras
package domain

abstract sealed case class Game private(board: Board, gameStatus: GameStatus, metrics: GameMetrics) {

  def makeMove(move: Move): Either[String, Game] = {
    board.updateBoard(move)
      .map(newBoard => newBoard -> metrics.update(move))
      .map { case (board, metric) => (board, metric, checkBoard(move.mark, board, metric)) }
      .map { case (board, gameStat, metric) => new Game(board, metric, gameStat) {} }
  }

  private def checkBoard(lastMark: Mark, board: Board, metric: GameMetrics): GameStatus = {
    if (metric.checkWining) Mark.toWinnerStatus(lastMark)
    else getNotWinningStatus(board)
  }

  private def getNotWinningStatus(board: Board): GameStatus = {
    val flattenBoard = board.matrix.flatten
    if (flattenBoard.forall(mark => Mark.TOE == mark)) GameStatus.EmptyField
    else if (flattenBoard.contains(Mark.TOE)) GameStatus.InProgress
    else GameStatus.Draw
  }
}

object Game {
  def of(board: Board, gameStatus: GameStatus) = new Game(board, gameStatus, new GameMetrics(board.size)) {}
}