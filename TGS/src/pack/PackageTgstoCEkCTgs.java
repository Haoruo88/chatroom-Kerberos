package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageTgstoCEkCTgs {
    @StructField(order = 0)
    @ArrayLengthMarker(fieldName = "KeyClientServer")
    public int KeyClientServerLength;

    @StructField(order = 1)
    public char[] KeyClientServer;

    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "IdServer")
    public int IdServerLength;

    @StructField(order = 3)
    public char[] IdServer;

    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "TimeStamp")
    public int TimeStampLength;

    @StructField(order = 5)
    public char[] TimeStamp;

    @StructField(order = 6)
    @ArrayLengthMarker(fieldName = "TicketServer")
    public int TicketServerLength;

    @StructField(order = 7)
    public byte[] TicketServer;

    @StructField(order = 8)
    public byte[] redundancy;

    public PackageTgstoCEkCTgs(String KeyClientServer_, String IdServer_, String TimeStamp_, byte[] TicketServer_) {
        this.KeyClientServerLength = KeyClientServer_.length();
        this.KeyClientServer = KeyClientServer_.toCharArray();
        this.IdServerLength = IdServer_.length();
        this.IdServer = IdServer_.toCharArray();
        this.TimeStampLength = TimeStamp_.length();
        this.TimeStamp = TimeStamp_.toCharArray();
        this.TicketServerLength = TicketServer_.length;
        this.TicketServer = TicketServer_;
        this.redundancy = new byte[10];
    }
}
