package com.vicras

import domain._

import zio._

import java.io.IOException
import scala.util.Try
import scala.util.matching.Regex

object TicTacToe {

  val play: IO[Serializable, Unit] =
    for {
      _ <- Console.printLine("Welcome to tic-tac-toe")
      board <- getInitialBoard
      _ <- gameFlow(Game.of(board, GameStatus.EmptyField))
    } yield ()

  private def getInitialBoard: IO[IOException, Board] = for {
    inputStr <- getUserInput("Enter size of board: ")
    board <- ZIO.fromOption(Try(inputStr.trim.toInt).toOption.flatMap(Board.of)) <>
      (Console.printLine("Can't recognise, input must be a number more than '1'") <*> getInitialBoard)
  } yield board

  private def gameFlow(game: Game, nextMoveMark: Mark = Mark.TAC): IO[Serializable, Unit] = {
    val oppositeMark = Mark.getOpposite(nextMoveMark)
    val flow: IO[Serializable, Unit] = for {
      cord <- getCoordinate
      move <- getPlayerMove(cord, oppositeMark)
      gameMod <- ZIO.fromEither(game.makeMove(move))
      _ <- Console.printLine(gameMod.board)
      _ <- checkGameStatus(oppositeMark, gameMod)
    } yield ()
    flow.catchSome {
      case reason: String => outputErrorMessageCurrentTable(game, reason) *> gameFlow(game, nextMoveMark)
    }
  }

  private def outputErrorMessageCurrentTable(game: Game, reason: String): IO[IOException, Unit] = {
    Console.printLine(reason) *> Console.printLine(game.board)
  }

  private def checkGameStatus(oppositeMark: Mark, updatedGame: Game) = {
    updatedGame.gameStatus match {
      case GameStatus.TicWon => Console.printLine("Tic won")
      case GameStatus.TacWon => Console.printLine("Tac won")
      case GameStatus.Draw => Console.printLine("Draw")
      case GameStatus.InProgress | GameStatus.EmptyField => gameFlow(updatedGame, oppositeMark)
    }
  }

  private val getCoordinate: IO[IOException, Coordinate] =
    for {
      input <- getUserInput("Enter position through space ([row] [column]): ")
      move <- parseUserInput(input) <> (Console.printLine("Can't recognize, please try again") <*> getCoordinate)
    } yield move

  private def getUserInput(message: String): IO[IOException, String] = Console.readLine(message)

  private def parseUserInput(input: String): IO[Option[Nothing], Coordinate] = {
    val userInputPattern: Regex = """([0-9]+) ([0-9]+)""".r
    ZIO.from(input match {
      case userInputPattern(iCord, jCord) => Coordinate.of(iCord.toInt, jCord.toInt)
      case _ => None
    })
  }

  private def getPlayerMove(cord: Coordinate, mark: Mark): UIO[Move] = ZIO.succeed(Move.of(cord, mark))
}
