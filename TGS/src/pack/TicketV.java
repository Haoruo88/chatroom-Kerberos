package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class TicketV {
    @StructField(order = 0)
    @ArrayLengthMarker(fieldName = "KeyClientServer")
    public int KeyClientServerLength;

    @StructField(order = 1)
    public char[] KeyClientServer;

    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "IdClient")
    public int IdClientLength;

    @StructField(order = 3)
    public char[] IdClient;

    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "AddressClient")
    public int AddressClientLength;

    @StructField(order = 5)
    public char[] AddressClient;

    @StructField(order = 6)
    @ArrayLengthMarker(fieldName = "IdServer")
    public int IdServerLength;

    @StructField(order = 7)
    public char[] IdServer;

    @StructField(order = 8)
    @ArrayLengthMarker(fieldName = "TimeStamp")
    public int TimeStampLength;

    @StructField(order = 9)
    public char[] TimeStamp;

    @StructField(order = 10)
    @ArrayLengthMarker(fieldName = "Lifetime")
    public int LifetimeLen;

    @StructField(order = 11)
    public char[] Lifetime;

    public TicketV(String KeyClientServer_, String IdClient_, String AddressClient_, String IdServer_,
            String TimeStamp_, String Lifetime_) {
        this.KeyClientServerLength = KeyClientServer_.length();
        this.KeyClientServer = KeyClientServer_.toCharArray();
        this.IdClientLength = IdClient_.length();
        this.IdClient = IdClient_.toCharArray();
        this.AddressClientLength = AddressClient_.length();
        this.AddressClient = AddressClient_.toCharArray();
        this.IdServerLength = IdServer_.length();
        this.IdServer = IdServer_.toCharArray();
        this.TimeStampLength = TimeStamp_.length();
        this.TimeStamp = TimeStamp_.toCharArray();
        this.LifetimeLen = Lifetime_.length();
        this.Lifetime = Lifetime_.toCharArray();
    }
}
