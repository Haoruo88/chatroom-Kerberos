package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageAstoCAuthEkc {
    @StructField(order = 0)
    @ArrayLengthMarker(fieldName = "KcTgs")
    public int KcTgsLen;
    @StructField(order = 1)
    public char[] KcTgs;
    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "IDtgs")
    public int IDtgsLen;
    @StructField(order = 3)
    public char[] IDtgs;
    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "TS")
    public int TSLen;
    @StructField(order = 5)
    public char[] TS;
    @StructField(order = 6)
    @ArrayLengthMarker(fieldName = "Lifetime")
    public int LifetimeLen;
    @StructField(order = 7)
    public char[] Lifetime;
    @StructField(order = 8)
    @ArrayLengthMarker(fieldName = "TicketTgs")
    public int TicketTgsLen;
    @StructField(order = 9)
    public byte[] TicketTgs;
    @StructField(order = 10)
    @ArrayLengthMarker(fieldName = "name")
    public int nameLen;
    @StructField(order = 11)
    public char[] name;
    public PackageAstoCAuthEkc(String KcTgs1,String IDtgs1,String TS1,String Lifetime1,byte[] TicketTgs1,String name1){
        this.KcTgsLen=KcTgs1.length();
        this.KcTgs=KcTgs1.toCharArray();
        this.IDtgsLen=IDtgs1.length();
        this.IDtgs=IDtgs1.toCharArray();
        this.TSLen=TS1.length();
        this.TS=TS1.toCharArray();
        this.LifetimeLen=Lifetime1.length();
        this.Lifetime=Lifetime1.toCharArray();
        this.TicketTgsLen=TicketTgs1.length;
        this.TicketTgs=TicketTgs1;
        this.nameLen=name1.length();
        this.name=name1.toCharArray();
    }
}
