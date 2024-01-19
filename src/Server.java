import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;


// /git/CAR_Wattebled_Thomas/ressource/dog.png
// /Documents
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
			this.commandeFTP();
		}
		else {
			str = "430 Identifiant ou mot de passe incorrect\r\n" ;
			out.write(str.getBytes());
		}
	}
	
	private void commandeFTP() throws IOException {
		File file= null;
		//Path p = null;
		FileOutputStream filep = null;
		ServerSocket ss2 = new ServerSocket(0);
		String res = scanner.nextLine();
		OutputStream out = this.getSocket().getOutputStream();
		FileInputStream fileIn = null; 
		String str = "230 utilisateur connecte\r\n";
		while(true) {
			if (res.equals("SYST")) {
				System.out.println("client connecte");
				out.write(str.getBytes());
			}
			else if (res.equals("QUIT")) {
				System.out.println("client deconnecte");
				str = "221 deconnexion\r\n" ;
				out.write(str.getBytes());
				break;
			}
			else if(res.equals("PASS")) {
				System.out.println(res);
				out.write(str.getBytes());
			}
			else if(res.equals("TYPE I")) {
				System.out.println("passage bin");
				str = "200 Type accepted\r\n";
				out.write(str.getBytes());
			}
			else if(res.equals("TYPE A")) {
				System.out.println("passage ascii");
				str = "200 type accepted\r\n";
				out.write(str.getBytes());
			}
			else if(res.substring(0,4).equals("EPSV")) {
				System.out.println(res);
				str = "229 Entering Extended Passive Mode (|||"+ ss2.getLocalPort()+ "|) \r\n";
				out.write(str.getBytes());
			}
			else if(res.substring(0,4).equals("PASV")) {
				System.out.println(res);
				str = "227 Entering Passive Mode\r\n";
				out.write(str.getBytes());
			}
			else if(res.substring(0,4).equals("PORT")) {
				System.out.println(res);
				str = "227 yes\r\n";
				out.write(str.getBytes());
			}
			else if(res.substring(0,4).equals("SIZE")) {
				filep= new FileOutputStream("./ressource/"+ res.substring(5));
				file = new File("./ressource/"+ res.substring(5));
				System.out.println(file.isFile());
				System.out.println(res);
				str = "213 File status\r\n";
				out.write(str.getBytes());
			}
			else if(res.substring(0,4).equals("EPRT")) {
				System.out.println(res);
				str = "200 EPRT command ok\r\n";
				out.write(str.getBytes());
			}
			else if(res.substring(0,4).equals("RETR")) {
				Socket dataSocket = new Socket();
				dataSocket = ss2.accept();
				//OutputStream dataSocketout = dataSocket.getOutputStream();
				System.out.println(res);
				str = "150 Accepted data connection\r\n";
				out.write(str.getBytes());
				fileIn= new FileInputStream(file);
				dataSocket.getOutputStream().write(fileIn.readAllBytes());	
				System.out.println("fichier copié");
				str = "226 File successfully transfered\r\n";
				out.write(str.getBytes());
				dataSocket.close();			}
			else {
				System.out.println(res);
				str = "500 commande non reconnu\r\n" ;
				out.write(str.getBytes());
			}	
			res = scanner.nextLine();
			}
	}
	public static void main(String [] args) throws IOException {
		Server server = new Server(2121);
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
