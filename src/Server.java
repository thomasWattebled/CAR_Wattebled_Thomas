import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Server {

	
	public static void main(String [] args) throws IOException {
		HashMap<String, String> maplogin = new HashMap<String,String>(); 
		maplogin.put("thomas", "oui");
		maplogin.put("valentin", "non");
		
		ServerSocket server = new ServerSocket(2124);
		System.out.println("lancement Server sur le port 2124 \n");
		Socket s = new Socket();
		s = server.accept();
		System.out.println("Connexion avec une socket \n");
		
		InputStream in = s.getInputStream() ;
		OutputStream out = s.getOutputStream();
		String str = "220 Service Ready \r\n" ;
		out.write(str.getBytes());
		Scanner scanner = new Scanner(in);
		String res = scanner.nextLine();
		System.out.println(res);
		String login = res.substring(5);

		if (maplogin.containsKey(login)) {
			str = "331 UserName Ok \r\n" ;
			out.write(str.getBytes());
			res = scanner.nextLine();
			System.out.println(res);
			String pass = res.substring(5);
			if (maplogin.get(login).equals(pass)) {
				str = "230 User log in\r\n" ;
				out.write(str.getBytes());
			}
		}
		while(true) {
			int chiffre = 1;
		}
	}
}
