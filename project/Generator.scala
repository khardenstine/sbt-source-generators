import java.io.{BufferedWriter, File}

import sbt._

class Generator(root: File, header: Option[String] = None)(sourcePackage: String*) {
  private def write(name: String)(fn: BufferedWriter => Unit): File = {
    val file = sourcePackage.foldLeft(root)(_ / _) / s"$name.scala"
    IO.writer(file, "", IO.defaultCharset, append = false){
      bw =>
        header foreach bw.write
        if (sourcePackage.nonEmpty)
          bw.write(sourcePackage.mkString("package ", ".", "\n\n"))
        fn(bw)
    }
    file
  }

  private def writeObject(name: String, contents: BufferedWriter => Unit)(bw: BufferedWriter): Unit = {
    bw.write(s"object $name {")
    bw.newLine()
    contents(bw)
    bw.write("}")
    bw.newLine()
  }

  def generateObject(name: String)(contents: BufferedWriter => Unit): File = {
    write(name)(writeObject(name, contents))
  }

  private def writeClass(name: String, contents: BufferedWriter => Unit)(bw: BufferedWriter): Unit = {
    bw.write(s"class $name {")
    bw.newLine()
    contents(bw)
    bw.write("}")
    bw.newLine()
  }

  def generateClass(name: String)(contents: BufferedWriter => Unit): File = {
    write(name)(writeClass(name, contents))
  }

  def generateClassAndCompanion(name: String)(classContents: BufferedWriter => Unit)(companionContents: BufferedWriter => Unit): File = {
    write(name){
      bw =>
        writeClass(name, classContents)(bw)
        bw.newLine()
        writeObject(name, companionContents)(bw)
    }
  }
}

object Generator {
  def generate(root: File, header: Option[String] = None): Seq[File] = {
    val gen = new Generator(root, header)("org", "khardenstine")
    val f = gen.generateObject("Test")(_.write(
      """|  def a = 1
         |""".stripMargin
    ))
    List(f)
  }
}