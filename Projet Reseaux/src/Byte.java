
public class Byte 
{
	private final int value;

	// Recevoir un string de type "A5"
	public Byte(String str)
	{
		//bits = Integer.toBinaryString(value);
		value = Integer.decode("0x"+str);
	}
	
	public Byte()
	{
		value = 0;
	}
	public int getValue()
	{
		return value;
	}
	
	public String getHexValue() {
		if (value >= 16)
			return Integer.toHexString(value).toUpperCase();
		else return "0" + Integer.toHexString(value).toUpperCase();
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(value);
	}
}
