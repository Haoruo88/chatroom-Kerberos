package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageCtoTgs {
    @StructField(order = 0)
    @ArrayLengthMarker(fieldName = "IDv")
    public int IDvLen;
    @StructField(order = 1)
    public char[] IDv;
    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "TicketTgs")
    public int TicketTgsLen;
    @StructField(order = 3)
    public byte[] TicketTgs;
    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "Authenticator")
    public int AuthenticatorLen;
    @StructField(order = 5)
    public byte[] Authenticator;
    @StructField(order = 6)
    public byte[] redundancy;
    public PackageCtoTgs(String IDv1,byte[] TicketTgs1,byte[] Authenticator1){
        this.IDvLen=IDv1.length();
        this.IDv=IDv1.toCharArray();
        this.TicketTgsLen=TicketTgs1.length;
        this.TicketTgs=TicketTgs1;
        this.AuthenticatorLen=Authenticator1.length;
        this.Authenticator=Authenticator1;
        this.redundancy=new byte[10];
    }
}
