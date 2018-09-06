
public class Risk {
	
	public static void main(String[] args) {
		String title = "Risk";
		String bg = "images/bg.jpg";
		int width = 1920;
		int height = 1080;
		//TODO: replace the args to engine with a Properties file
		Engine engine = new Engine(title, bg, width, height);
  }
	
	//Shorthand
	public static void print(Object printMe) {
		System.out.println(printMe);
	}
}
