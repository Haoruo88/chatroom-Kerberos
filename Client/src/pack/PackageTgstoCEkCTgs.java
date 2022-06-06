package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageTgstoCEkCTgs {
    @StructField(order=0)
    @ArrayLengthMarker(fieldName = "Kcv")
    public int KcvLen;
    @StructField(order=1)
    public char[] Kcv;
    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "IDv")
    public int IDvLen;
    @StructField(order = 3)
    public char[] IDv;
    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "TS")
    public int TSLen;
    @StructField(order = 5)
    public char[] TS;
    @StructField(order = 6)
    @ArrayLengthMarker(fieldName = "TicketV")
    public int TicketVLen;
    @StructField(order = 7)
    public byte[] TicketV;
    @StructField(order = 8)
    public byte[] redundancy;
    public PackageTgstoCEkCTgs(String Kcv1,String IDv1,String TS1,byte[] TicketV1){
        this.KcvLen=Kcv1.length();
        this.Kcv=Kcv1.toCharArray();
        this.IDvLen=IDv1.length();
        this.IDv=IDv1.toCharArray();
        this.TSLen=TS1.length();
        this.TS=TS1.toCharArray();
        this.TicketVLen=TicketV1.length;
        this.TicketV=TicketV1;
        this.redundancy=new byte[10];
    }
}

