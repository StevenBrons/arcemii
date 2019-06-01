package server.general;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import shared.entities.Player;

public class SinglePlayerServer extends ArcemiiServer {

	private ObjectOutputStream oOut2;
	private ObjectInputStream oIn1;
	private ObjectOutputStream oOut1;
	private ObjectInputStream oIn2;

    /*          client              server
        1       in1     ---->        out1
        2       out2    <----        in2
     */

	public SinglePlayerServer() {
		try {
			PipedInputStream in1 = new PipedInputStream();
			PipedOutputStream out1 = new PipedOutputStream(in1);

			PipedInputStream in2 = new PipedInputStream();
			PipedOutputStream out2 = new PipedOutputStream(in2);

			//client
			oOut1 = new ObjectOutputStream(out1);
			oIn1 = new ObjectInputStream(in1);

			//server
			oOut2 = new ObjectOutputStream(out2);
			oIn2 = new ObjectInputStream(in2);

			Player p = new Player(oIn2,oOut1);
			gameHandler.addPlayer(p);

			System.out.println("Singleplayer server started");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//relative to client
	public ObjectOutputStream getOutputStream() {
		return oOut2;
	}

	public ObjectInputStream getInputStream() {
		return oIn1;
	}

	/**
	 * Stop the singleplayer server a.k.a. close all the streams.
	 * @author Bram Pulles
	 */
	public void stopServer(){
		Log.d("CONNECTION","Stopping singleplayer server");
		try{
			oIn1.close();
			oOut1.close();
			oIn2.close();
			oOut2.close();
		}catch(Exception e){
			System.out.println("Could not stop the singleplayer server appropriately.");
		}
	}
}
