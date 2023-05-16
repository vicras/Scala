package service

import zio.ZIO

import java.io.{File, PrintWriter}
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{FileSystems, Files, Path}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

trait DataBackup {
  def dump(content: String): ZIO[Any, Throwable, Unit]

  def clear: ZIO[Any, Throwable, Unit]
}

class FileDataBackup extends DataBackup {
  private val DUMP_FILE_NAME = "duty-app.dump"
  private val DUMP_FOLDER_NAME = "duty-app-dump"
  private val FILE_SEPARATOR = FileSystems.getDefault.getSeparator

  def dump(content: String): ZIO[Any, Throwable, Unit] = ZIO.acquireReleaseWith(
    for {
      _ <- createDumpFolder
      writer <- createWriterForDumpFile
    } yield writer
  )(writer => ZIO.succeed(writer.close())
  )(
    writer => ZIO.succeed(writer.write(content))
  )

  private def createDumpFolder = for {
    _ <- ZIO.attempt(dumpFolder.mkdir())
  } yield ()

  private def createWriterForDumpFile = for {
    writer <- ZIO.attempt(new PrintWriter(dumpFile, UTF_8))
  } yield writer

  override def clear: ZIO[Any, Throwable, Unit] = for {
    _ <- ZIO.attempt(clearFolder(dumpFolder.toPath))
  } yield ()

  private def clearFolder(path: Path): Unit = {
    if (Files.exists(path)) {
      Files.walk(path)
        .map(_.toFile)
        .forEach(_.delete)
      Files.delete(path)
    }
  }

  private def dumpFolder: File = {
    val userHome = getUserHome
    new File(userHome + FILE_SEPARATOR + DUMP_FOLDER_NAME)
  }

  private def dumpFile: File = {
    val userHome = getUserHome
    val filePath = userHome + FILE_SEPARATOR + DUMP_FOLDER_NAME + FILE_SEPARATOR + DUMP_FILE_NAME + "_" + timestamp
    new File(filePath)
  }

  private def getUserHome = {
    System.getProperty("user.home")
  }

  private def timestamp: String = LocalDateTime.now().format(ISO_DATE_TIME)
}
