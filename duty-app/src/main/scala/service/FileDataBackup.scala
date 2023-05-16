package service

import zio.ZIO

import java.io.{File, PrintWriter}
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.FileSystems
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

trait DataBackup {
  def dump(content: String): ZIO[Any, Throwable, Unit]
}

class FileDataBackup extends DataBackup {
  private val DUMP_FILE_NAME = "duty-app.dump"
  private val DUMP_FOLDER_NAME = "duty-app-dump"
  private val FILE_SEPARATOR = FileSystems.getDefault.getSeparator

  def dump(content: String): ZIO[Any, Throwable, Unit] = ZIO.acquireReleaseWith(
    for {
      userHome <- ZIO.succeed(System.getProperty("user.home"))
      _ <- createDumpFolder(DUMP_FOLDER_NAME, userHome)
      writer <- createWriterForDumpFile(DUMP_FILE_NAME, userHome)
    } yield writer
  )(writer => ZIO.succeed(writer.close())
  )(
    writer => ZIO.succeed(writer.write(content))
  )

  private def createDumpFolder(folderName: String, userHome: String) = for {
    dumpFolder <- ZIO.succeed(new File(userHome + FILE_SEPARATOR + folderName))
    _ <- ZIO.attempt(dumpFolder.mkdir())
  } yield ()

  private def createWriterForDumpFile(DUMP_FILE_NAME: String, userHome: String) = for {
    fullPath <- ZIO.succeed(userHome + FILE_SEPARATOR + DUMP_FOLDER_NAME + FILE_SEPARATOR + DUMP_FILE_NAME + "_" + timestamp())
    dumpFile <- ZIO.succeed(new File(fullPath))
    writer <- ZIO.attempt(new PrintWriter(dumpFile, UTF_8))
  } yield writer

  private def timestamp(): String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
}
