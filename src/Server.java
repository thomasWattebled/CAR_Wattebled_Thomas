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
	
	public void commandeSyst(String reponse,OutputStream out) throws IOException {
		String str = "230 utilisateur connecte\r\n";
		System.out.println("client connecte");
		out.write(str.getBytes());
	}
	
	public void commandeQuit(OutputStream out) throws IOException {
		System.out.println("client deconnecte");
		String str = "221 deconnexion\r\n" ;
		out.write(str.getBytes());
	}
	
	public void commandeEPSV(ServerSocket ss2,OutputStream out) throws IOException {
		String str = "229 Entering Extended Passive Mode (|||"+ ss2.getLocalPort()+ "|) \r\n";
		out.write(str.getBytes());
	}
	
	public void commandePASV(OutputStream out) throws IOException {
		String str = "227 Entering Passive Mode\r\n";
		out.write(str.getBytes());
	}
	
	public void commandeRETR(ServerSocket ss2,OutputStream out, String res) throws IOException {
		Socket dataSocket = ss2.accept();
		File file =  new File ("./ressource/" + res.substring(5));
		FileInputStream fileInput = new FileInputStream(file);
		String response ="";
		System.out.println(res);
		String str = "150 Accepted data connection\r\n";
		out.write(str.getBytes());	
		int octet;
		while((octet= fileInput.read())!=-1) {
			String reponse = octet + "\r\n";
			dataSocket.getOutputStream().write(reponse.getBytes());
		}
		dataSocket.close();
		fileInput.close();
		System.out.println("fichier copié");
		str = "226 File successfully transfered\r\n";
		out.write(str.getBytes());		
	}
	
	public String commandeList(ServerSocket ss2,OutputStream out,String path,String res) throws IOException {
		File repertoire ;
		File fichier;
		Socket dataSocket = ss2.accept();
		System.out.println(res);
		String str = "150 Accepted data connection\r\n";
		out.write(str.getBytes());
		repertoire = res.length()<5 ? new File(path+"/") : new File(path +"/"+res.substring(5));
		String liste[] = repertoire.list();
		 String response = "";
		 if (liste!=null) {
			 for (int i = 0; i < liste.length; i++) {
				 fichier = res.length()<5 ? new File("./"+liste[i]) :  new File(res.substring(5)+"/"+liste[i]); 
				 String droits = fichier.canRead() ? " dr" : "d-";
			     droits += fichier.canWrite() ? "w" : "-";
			     droits += fichier.canExecute() ? "x" : "-";
			     response += droits+" "+liste[i]+"\n"; 
			 }
			 response = response + "\r\n";
			 dataSocket.getOutputStream().write(response.getBytes());
			 dataSocket.close();
			 str = "212 File successfully transfered\r\n";
			out.write(str.getBytes());	
		 }
		 return path;
	}
	
	
	
	private void commandeFTP() throws IOException {
		ServerSocket ss2 = new ServerSocket(0);
		OutputStream out = this.getSocket().getOutputStream();
		String str = "230 utilisateur connecte\r\n";
		String path ="./"; 
		Boolean dialogue= true;
		while(dialogue) {
			String res = scanner.nextLine();
			if (res.equals("SYST")) {
				this.commandeSyst(str, out);
			}
			else if (res.equals("QUIT")) {
				this.commandeQuit(out);
				dialogue = false;
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
				this.commandeEPSV(ss2,out);
			}
			else if(res.substring(0,4).equals("PASV")) {
				System.out.println(res);
				this.commandePASV(out);
			}
			else if(res.substring(0,4).equals("SIZE")) {
				str = "213 File status\r\n";
				out.write(str.getBytes());
			}
			else if(res.substring(0,4).equals("EPRT")) {
				System.out.println(res);
				str = "200 EPRT command ok\r\n";
				out.write(str.getBytes());
			}
			else if(res.substring(0,4).equals("RETR")) {
				this.commandeRETR(ss2, out, res);
				}
			else if(res.substring(0,4).equals("LIST"))  {
				this.commandeList(ss2, out, path, res);		
				 }
			else if(res.substring(0,3).equals("CWD")) {
				System.out.println(res);
				File file = new File(res.substring(4));
				System.out.println(file.exists());
				if(file.exists()) {
					str="250 Requested file action okay, completed.\r\n";
					path = path + res.substring(4)+"/";
					System.out.println("mon path apres cd est de :" + path);
				}
				else {
					str="500 Syntax error in parameters or argument.\r\n";
				}
				 out.write(str.getBytes());
			 }
			else if(res.substring(0,4).equals("XCWD")) {
				System.out.println(res);
				File file = new File(res.substring(4));
				System.out.println(file.exists());
				if(file.exists()) {
					str="250 Requested file action okay, completed.\r\n";
				}
				else {
					str="500 Path false\r\n";
				}
				 out.write(str.getBytes());
			}	
			else {
				System.out.println(res);
				str = "500 commande non reconnu\r\n" ;
				out.write(str.getBytes());
			}	
		}
	this.getSocket().close();
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
