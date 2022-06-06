package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageCtoVAuth {
    @StructField(order = 0)
    public byte status;
    @StructField(order = 1)
    @ArrayLengthMarker(fieldName = "TicketV")
    public int TicketVLen;
    @StructField(order = 2)
    public byte[] TicketV;
    @StructField(order = 3)
    @ArrayLengthMarker(fieldName = "Authenticator")
    public int AuthenticatorLen;
    @StructField(order = 4)
    public byte[] Authenticator;
    @StructField(order = 5)
    public byte[] redundancy;
    public PackageCtoVAuth(byte[] TicketV1,byte[] Authenticator1){
        this.status=0;
        this.TicketVLen=TicketV1.length;
        this.TicketV=TicketV1;
        this.AuthenticatorLen=Authenticator1.length;
        this.Authenticator=Authenticator1;
        this.redundancy=new byte[10];
    }
}
