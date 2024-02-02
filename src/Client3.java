import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client3 {
	public static void main(String [] args) throws IOException {
		Socket socket= new Socket("localhost",2121);
	    System.out.println("connexion au server");
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		Scanner scanner = new Scanner(in);
		System.out.println(scanner.nextLine());
		
		out.write("USER miage\r\n".getBytes());
		System.out.println(scanner.nextLine());
		
		out.write("PASS car\r\n".getBytes());
		System.out.println(scanner.nextLine());
		
		
		out.write("LINE text.txt 10\r\n".getBytes());
		System.out.println(scanner.nextLine());
		
		out.write("QUIT\r\n".getBytes());
		socket.close();
		System.out.println("deconnexion server");
	}
}
