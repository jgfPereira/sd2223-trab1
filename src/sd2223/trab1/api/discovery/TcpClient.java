package sd2223.trab1.api.discovery;

import java.net.*;
import java.util.*;

public class TcpClient {

	private static final String QUIT = "!quit";
	private static final String SERVICE_NAME = "ChatWebService";

	public static void main(String[] args) throws Exception {

		Discovery discovery = Discovery.getInstance();
		String[] hostAndPort = discovery.getHostAndPortOfService(SERVICE_NAME);
		var hostname = hostAndPort[0];
		var port = Integer.parseInt(hostAndPort[1]);

		try (var cs = new Socket(hostname, port); var sc = new Scanner(System.in)) {
			String input;
			do {
				System.out.println("Type '!quit' to exit or send a msg to the server");
				input = sc.nextLine();
				cs.getOutputStream().write((input + System.lineSeparator()).getBytes());
			} while (!input.equals(QUIT));

			System.out.println("Connection closed.");
			System.exit(0);
		}
	}
}