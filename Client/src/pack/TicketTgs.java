package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class TicketTgs {
    @StructField(order = 0)
    @ArrayLengthMarker(fieldName = "KcTgs")
    public int KcTgsLen;
    @StructField(order = 1)
    public char[] KcTgs;
    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "IDc")
    public int IDcLen;
    @StructField(order = 3)
    public char[] IDc;
    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "ADc")
    public int ADcLen;
    @StructField(order = 5)
    public char[] ADc;
    @StructField(order = 6)
    @ArrayLengthMarker(fieldName = "IDtgs")
    public int IDtgsLen;
    @StructField(order = 7)
    public char[] IDtgs;
    @StructField(order = 8)
    @ArrayLengthMarker(fieldName = "TS")
    public int TSLen;
    @StructField(order = 9)
    public char[] TS;
    @StructField(order = 10)
    @ArrayLengthMarker(fieldName = "Lifetime")
    public int LifetimeLen;
    @StructField(order = 11)
    public char[] Lifetime;
    public TicketTgs(String KcTgs1,String IDc1,String ADc1,String IDtgs1,String TS1,String Lifetime1){
        this.KcTgsLen=KcTgs1.length();
        this.KcTgs=KcTgs1.toCharArray();
        this.IDcLen=IDc1.length();
        this.IDc=IDc1.toCharArray();
        this.ADcLen=ADc1.length();
        this.ADc=ADc1.toCharArray();
        this.IDtgsLen=IDtgs1.length();
        this.IDtgs=IDtgs1.toCharArray();
        this.TSLen=TS1.length();
        this.TS=TS1.toCharArray();
        this.LifetimeLen=Lifetime1.length();
        this.Lifetime=Lifetime1.toCharArray();
    }
}
