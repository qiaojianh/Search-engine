/**
 * 
 * The Drive of project
 * @author qiaojianhu
 *
 */
public class Driver {

	public static void main(String[] args) {

		// creat InvertedIndexBulider object and call the build function to make json file
		InvertedIndexBulider bulider = new InvertedIndexBulider();
		bulider.bulid(args);
	}

}
