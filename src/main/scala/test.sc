import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

case class Offset(days: Long, number: Long, mnemonic: String) {
  override def toString = number + " " + mnemonic
}

object test {
  implicit def convertIterable(source: Iterable[Int]): Iterable[Long] = source.map(_.toLong)

  val yearInDays = 365.242

  def every(x: Long) = Stream.from(1).map(_ * x)

  def makeStream(unitStream: Iterable[Long], mnemonic: String, daysPerPeriod: Double): Iterable[Offset] =
    unitStream.map(x => Offset(math.round(x * daysPerPeriod), x, mnemonic))

  val planetYears = Map(
    "Mercury" -> 87.96,
    "Venus" -> 224.68,
    "Earth" -> 365.26,
    "Mars" -> 686.98,
    "Jupiter" -> 11.862 * yearInDays,
    "Saturn" -> 29.456 * yearInDays,
    "Uranus" -> 84.07 * yearInDays,
    "Neptune" -> 164.81 * yearInDays)

  val streams: List[Iterable[Offset]] = List(
    makeStream(every(1000), "days", 1),
    makeStream(every(100), "weeks", 7),
    makeStream(every(100), "months", yearInDays / 12),
    makeStream(every(1), "years", yearInDays),
    makeStream(1111 to 9999 by 1111, "days", 1),
    makeStream(11111 to 99999 by 11111, "days", 1)) ++
    planetYears.map { case (name, days) => makeStream(every(1), name + " years", days)}

  val birthday: Calendar = Calendar.getInstance()
  birthday.set(1985, Calendar.DECEMBER, 7)
  val now: Calendar = Calendar.getInstance()
  val daysOld = (now.getTimeInMillis - birthday.getTimeInMillis).toDouble /
    1000 / 3600 / 24

  val upcomingOffsets = streams.flatMap(s => s.find(item => item.days > daysOld)).sortBy(_.days)
  val upcomingDates = upcomingOffsets.map(offset => {
    val cal = Calendar.getInstance()
    cal.setTimeInMillis(birthday.getTimeInMillis + offset.days * 24 * 3600 * 1000)
    "%s on %s".format(offset, new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime))
  })
}
