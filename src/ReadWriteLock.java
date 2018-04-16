
/**
 * A simple custom lock that allows simultaneously read operations, but
 * disallows simultaneously write and read/write operations.
 *
 * Does not implement any form or priority to read or write operations. The
 * first thread that acquires the appropriate lock should be allowed to
 * continue.
 */
public class ReadWriteLock {
	private int readers;
	private int writers;

	/**
	 * Initializes a multi-reader single-writer lock.
	 */
	public ReadWriteLock() {
		readers = 0;
		writers = 0;
	}
	
	public int getReaders() {
		return readers;
	}

	public void setReaders(int readers) {
		this.readers = readers;
	}

	public int getWriters() {
		return writers;
	}

	public void setWriters(int writers) {
		this.writers = writers;
	}

	/**
	 * Will wait until there are no active writers in the system, and then will
	 * increase the number of active readers.
	 */
	public synchronized void lockReadOnly() {
		while (this.getWriters() > 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.setReaders(this.getReaders() + 1);
		this.notifyAll();
	}

	/**
	 * Will decrease the number of active readers, and notify any waiting
	 * threads if necessary.
	 */
	public synchronized void unlockReadOnly() {
		if (this.getReaders() > 0) {
			this.setReaders(this.getReaders() - 1);
			this.notifyAll();
		}
	}

	/**
	 * Will wait until there are no active readers or writers in the system, and
	 * then will increase the number of active writers.
	 */
	public synchronized void lockReadWrite() {
		while (this.getReaders() > 0 || this.getWriters() > 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.setWriters(this.getWriters() + 1);
		this.notifyAll();
	}

	/**
	 * Will decrease the number of active writers, and notify any waiting
	 * threads if necessary.
	 */
	public synchronized void unlockReadWrite() {
		if (this.getWriters() > 0) {
			this.setWriters(this.getWriters()-1);
			this.notifyAll();
		}
	}
}
