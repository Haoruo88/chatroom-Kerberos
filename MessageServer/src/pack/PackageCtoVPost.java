package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageCtoVPost {
    @StructField(order = 0)
    public byte status;
    @StructField(order = 1)
    public int totalLen;
    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "EkCV")
    public int EkCVLen;
    @StructField(order = 3)
    public byte[] EkCV;
    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "TicketV")
    public int TicketVLen;
    @StructField(order = 5)
    public byte[] TicketV;
    public PackageCtoVPost(int totalLen1,byte[] Ekcv1,byte[] TicketV1){
        this.status=6;
        this.totalLen=totalLen1;
        this.EkCVLen=Ekcv1.length;
        this.EkCV=Ekcv1;
        this.TicketVLen=TicketV1.length;
        this.TicketV=TicketV1;
    }
}
