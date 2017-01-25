import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.io.IOException;

public class Ex2Client {
    public static void main(String[] args) {
        try(Socket socket = new Socket("codebank.xyz", 38102)) {
            System.out.println("Connected to server.");
	    
			//Create byte streams to communicate to Server
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			//Store byte values for CRC
			byte[] message = new byte[100];
			byte value;

			System.out.print("Received bytes: ");
			
			//Iterate 100 times
			for(int i=0; i < 100; i++) {
				//Read 2 bytes each iteration
				int upperByte = is.read();
				int lowerByte = is.read();

				//Left bit shift x4
				upperByte = upperByte << 4;

				//1. Concatenate bytes
				//2. Store in byte array
				value = (byte) (upperByte | lowerByte);
				message[i] = value;
				
				//Print message in HEX values
				if(i%10 == 0) {
					System.out.println();
				}
				System.out.printf("%02X", message[i]);
			}

			//Create CRC
			CRC32 CRC = new CRC32();
			CRC.update(message);
			long code = CRC.getValue();
		
			//Print generated CRC code
			System.out.print("\nGenerated CRC32: ");
			System.out.printf("%02X", code);
			System.out.println();

			//Send 4 bytes to Server
			for(int i=3; i >= 0; i--) {
				//Shift 24 bits, 16 bits, 8 bits, 0 bits
				os.write((int)code >> (8*i));
			}
		   
		    //Check if generated CRC code matches Server's CRC code
			int checkCRC = is.read();
			if(checkCRC == 1) {
				System.out.println("Response good.");
			} else {
				System.out.println("Response bad.");
			}
			
	} catch (IOException e) {
		e.printStackTrace();
	}
		System.out.print("Disconnected from server.");
    }
}
