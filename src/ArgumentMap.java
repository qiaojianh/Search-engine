import java.util.HashMap;
import java.util.Map;

// TODO Homework: Fill in or fix all of the methods with TODO comments.

/**
 * Parses command-line arguments into flag/value pairs, and stores those pairs
 * in a map for easy access.
 */
public class ArgumentMap {

	private final Map<String, String> map;
//	private InvertedIndexBulider iib = new InvertedIndexBulider();

	/**
	 * Initializes the argument map.
	 */
	public ArgumentMap() {
		map = new HashMap<>();
	}

	/**
	 * Initializes the argument map and parses the specified arguments into
	 * key/value pairs.
	 *
	 * @param args
	 *            command line arguments
	 *
	 * @see #parse(String[])
	 */
	public ArgumentMap(String[] args) {
		this();
		parse(args);
	}

	/**
	 * Parses the specified arguments into key/value pairs and adds them to the
	 * argument map.
	 *
	 * @param args
	 *            command line arguments
	 */
	public void parse(String[] args) {
		// TODO
		String tmp; 
				
		for(int i = 0; i < args.length;i++) {
			tmp = args[i];
			if(isFlag(tmp)) {
				map.put(tmp, null);					
			}else if(isValue(tmp) && i != 0) {
				if(isFlag(args[i-1])) {
					map.put(args[i-1], tmp);
				}
			}
		}
		
		if(map.containsKey("-index") && map.get("-index") == null) {
			map.put("-index", "index.json");
		}
		if(map.containsKey("-results") && map.get("-results") == null) {
			System.out.println("jinglaile");
			map.put("-results", "results.json");
		}
		
	}

	/**
	 *
	 * @param arg
	 * @return
	 */
	public static boolean isFlag(String arg) {
		if(arg == null) {
			return false;
		}else if(arg.trim().equals("-")){
			return false;
		}else {
			return arg.startsWith("-");
		}// TODO
	}

	/**
	 *
	 * @param arg
	 * @return
	 */
	public static boolean isValue(String arg) {
//		System.out.print(arg);
		if(arg == null) {
			System.out.println("1");
			return false;
		}else if(arg.trim().equals("")) {
			System.out.println("2");
			return false;
		}else if(arg.trim().equals("-")) {
			System.out.println("3");
			return false;
		}
		else if(isFlag(arg)) {
			System.out.println();
			return false;
		}		
		return true;
		// TODO
	}

	/**
	 * Returns the number of unique flags stored in the argument map.
	 *
	 * @return number of flags
	 */
	public int numFlags() {
		return map.size(); // TODO (1 LOC)
	}

	/**
	 * Determines whether the specified flag is stored in the argument map.
	 *
	 * @param flag
	 *            flag to test
	 *
	 * @return true if the flag is in the argument map
	 */
	public boolean hasFlag(String flag) {
		return map.containsKey(flag); // TODO (1 LOC)
	}

	/**
	 * Determines whether the specified flag is stored in the argument map and
	 * has a non-null value stored with it.
	 *
	 * @param flag
	 *            flag to test
	 *
	 * @return true if the flag is in the argument map and has a non-null value
	 */
	public boolean hasValue(String flag) {
//		System.out.println(map.get(flag) != null);
//		System.out.println(map.get(flag));
		return map.get(flag) != null; // TODO (1 LOC)
	}

	/**
	 * Returns the value for the specified flag as a String object.
	 *
	 * @param flag
	 *            flag to get value for
	 *
	 * @return value as a String or null if flag or value was not found
	 */
	public String getString(String flag) {
		return map.get(flag); // TODO (1 LOC)
	}

	/**
	 * Returns the value for the specified flag as a String object. If the flag
	 * is missing or the flag does not have a value, returns the specified
	 * default value instead.
	 *
	 * @param flag
	 *            flag to get value for
	 * @param defaultValue
	 *            value to return if flag or value is missing
	 * @return value of flag as a String, or the default value if the flag or
	 *         value is missing
	 */
	public String getString(String flag, String defaultValue) {
		if(!hasFlag(flag)) {
			return defaultValue;
		}else if(!hasValue(flag)) {
			return defaultValue;
		}
		return getString(flag); // TODO
	}

	/**
	 * Returns the value for the specified flag as an int value. If the flag is
	 * missing or the flag does not have a value, returns the specified default
	 * value instead.
	 *
	 * @param flag
	 *            flag to get value for
	 * @param defaultValue
	 *            value to return if the flag or value is missing
	 * @return value of flag as an int, or the default value if the flag or
	 *         value is missing
	 */
	public int getInteger(String flag, int defaultValue) {
		if(!hasFlag(flag)) {
			return defaultValue;
		}else if(!hasValue(flag)) {
			return defaultValue;
		}else {
			try{
				return Integer.parseInt(getString(flag));
			}catch(Exception e){
				return defaultValue;
			}
		}
		 // TODO
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
