package javazoom.jl.player;

import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;

public interface AudioDevice
{

	public void open(Decoder decoder) throws JavaLayerException;
		
	public boolean isOpen();

	public void write(short[] samples, int offs, int len) throws JavaLayerException;

	public void close();
	
	public void flush();

	public int getPosition();
}
