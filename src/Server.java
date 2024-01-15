import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Server {
	
	private HashMap<String, String> maplogin = new HashMap<String,String>();
	private ServerSocket serverSocket ;
	private Socket socket;
	private Scanner scanner ;
	
	public Server(int port) throws IOException {
		this.maplogin.put("thomas", "oui");
		this.maplogin.put("valentin", "non");
		
		this.serverSocket = new ServerSocket(port);
		System.out.println("lancement Server sur le port " + port +"\n");
		this.socket = new Socket();
	}
	public HashMap<String, String> getMap(){
		return this.maplogin ;
	}
	
	public ServerSocket getServerSocket() {
		return this.serverSocket ;
	}
	
	public Socket getSocket() {
		return this.socket ;
	}
	
	public void setSocket(Socket s) {
		this.socket =s ;
	}
	
	public void acceptSocket() throws IOException {
		Socket s = this.getSocket();
		s = this.getServerSocket().accept();
		this.setSocket(s);
		System.out.println("Connexion avec une socket \n");	
		InputStream in = s.getInputStream() ;
		this.scanner = new Scanner(in);
	}
	
	public void correctLogin(String log) throws IOException {
		OutputStream out = this.getSocket().getOutputStream();
		String str = "" ;
		if(this.maplogin.containsKey(log)) {
			str = "331 UserName Ok \r\n" ;
			out.write(str.getBytes());
			String res = scanner.nextLine();
			System.out.println(res);
			this.correctMdp(log,res.substring(5));
		}
		else {
			str = "430 Identifiant ou mot de passe incorrect\r\n" ;
			out.write(str.getBytes());
		}
	}
	
	private void correctMdp(String log,String pass) throws IOException {
		String str;
		OutputStream out = this.getSocket().getOutputStream();
		if(this.getMap().get(log).equals(pass)) {
			str = "230 User log in\r\n" ;
			out.write(str.getBytes());
			//String res = scanner.nextLine();	
			this.commandeFTP(str);
		}
		else {
			str = "430 Identifiant ou mot de passe incorrect\r\n" ;
			out.write(str.getBytes());
		}
	}
	
	private void commandeFTP(String str) throws IOException {
		String res = scanner.nextLine();
		OutputStream out = this.getSocket().getOutputStream();
		//String str = "";
		while(true) {
			if (res.equals("SYST")) {
				System.out.println("client connecte");
				out.write(str.getBytes());
				res = scanner.nextLine();
			}
			else if (res.equals("QUIT")) {
				System.out.println("client deconnecte");
				out = this.getSocket().getOutputStream();
				str = "221 deconnexion\r\n" ;
				out.write(str.getBytes());
				break;
			}
			else {
				System.out.println(res);
				out = this.getSocket().getOutputStream();
				str = "500 commande non reconnu\r\n" ;
				out.write(str.getBytes());
				res = scanner.nextLine();
				//break ;
			}
		}
	}
	public static void main(String [] args) throws IOException {
		Server server = new Server(2122);
		server.acceptSocket();
		Socket s = server.getSocket();
		InputStream in = s.getInputStream() ;
		OutputStream out = s.getOutputStream();
		String str = "220 Service Ready \r\n" ;
		out.write(str.getBytes());
		Scanner scanner = new Scanner(in);
		String res = scanner.nextLine();
		System.out.println(res);
		String login = res.substring(5);	
		server.correctLogin(login);
			}
}
