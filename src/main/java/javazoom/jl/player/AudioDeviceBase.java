package javazoom.jl.player;

import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;

public abstract class AudioDeviceBase implements AudioDevice
{
	private	boolean			open = false;
	
	private Decoder			decoder = null;

	@Override
	public synchronized void open(Decoder decoder) throws JavaLayerException
	{
		if (!isOpen())
		{
			this.decoder = decoder;
			openImpl();
			setOpen(true);
		}
	}

	protected void openImpl() throws JavaLayerException
	{			
	}
	
	protected void setOpen(boolean open)
	{
		this.open = open;
	}
	
	@Override
	public synchronized boolean isOpen()
	{
		return open;	
	}
	
	@Override
	public synchronized void close()
	{
		if (isOpen())
		{
			closeImpl();
			setOpen(false);	
			decoder = null;
		}
	}
	
	protected void closeImpl()
	{
	}
	
	@Override
	public void write(short[] samples, int offs, int len) 
		throws JavaLayerException
	{
		if (isOpen())
		{
			writeImpl(samples, offs, len);
		}
	}
	
	protected void writeImpl(short[] samples, int offs, int len) 
		throws JavaLayerException
	{
	}
	
	@Override
	public void flush()
	{
		if (isOpen())
		{
			flushImpl();	
		}
	}
	
	protected void flushImpl()
	{		
	}

	protected Decoder getDecoder()
	{
		return decoder;	
	}
}
