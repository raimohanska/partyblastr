package partyblastr.cache

import collection.immutable.HashMap

trait Cache[K, V] {
  def get(key : K, fetch : (K => V)) : V
  def remove(key : K)
}

class NoCache[K, V] extends Cache[K, V] {
  def get(key : K, fetch : (K => V)) : V = fetch(key)
  def remove(key : K) = {}
}

class TTLCache[K, V](ttlMs : Long) extends Cache[K, V] {
  private case class Entry(value : V, timestamp : Long = System.currentTimeMillis)
  private var data = new HashMap[K, Entry]
  private var lastCleanup = System.currentTimeMillis

  def get(key : K, fetch : (K => V)) : V = {
    cleanup
    data.get(key) match {
      case Some (Entry(v, ts)) if (isValid(ts)) => v
      case None => val v = fetch(key)
      data += key -> Entry(v)
      v
    }
  }

  def remove(key : K) = {
    data -= key
  }

  private def isValid(timestamp : Long) = {
    timestamp >= System.currentTimeMillis - ttlMs
  }

  private def cleanup {
    if (!isValid(lastCleanup)) {
      data = data.filter({case (key, entry) => isValid(entry.timestamp)})
      lastCleanup = System.currentTimeMillis
    }
  }
}
