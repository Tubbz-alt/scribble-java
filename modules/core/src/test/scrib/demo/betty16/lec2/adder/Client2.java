package demo.betty16.lec2.adder;

import static demo.betty16.lec2.adder.Adder.Adder.Adder.Add;
import static demo.betty16.lec2.adder.Adder.Adder.Adder.Bye;
import static demo.betty16.lec2.adder.Adder.Adder.Adder.C;
import static demo.betty16.lec2.adder.Adder.Adder.Adder.Res;
import static demo.betty16.lec2.adder.Adder.Adder.Adder.S;

import org.scribble.net.Buf;
import org.scribble.net.ObjectStreamFormatter;
import org.scribble.net.session.SessionEndpoint;
import org.scribble.net.session.SocketChannelEndpoint;

import demo.betty16.lec2.adder.Adder.Adder.Adder;
import demo.betty16.lec2.adder.Adder.Adder.channels.C.Adder_C_1;
import demo.betty16.lec2.adder.Adder.Adder.roles.C;

public class Client2 {

	public static void main(String[] args) throws Exception {
		Adder adder = new Adder();
		try (SessionEndpoint<Adder, C> client = new SessionEndpoint<>(adder, C, new ObjectStreamFormatter())) {	
			client.connect(S, SocketChannelEndpoint::new, "localhost", 8888);
			System.out.println("C: " + new Client2().run(client));
		}
	}
	
	private int run(SessionEndpoint<Adder, C> client) throws Exception {
		Buf<Integer> i = new Buf<>(1);

		Adder_C_1 s1 = new Adder_C_1(client);
		for (int j = 0; j < 10; j++) {
			s1 = s1.send(S, Add, i.val, i.val).receive(S, Res, i);
		}
		s1.send(S, Bye).receive(S, Bye);
		
		return i.val;
	}
}
