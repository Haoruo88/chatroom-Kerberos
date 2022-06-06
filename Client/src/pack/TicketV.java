package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class TicketV {
    @StructField(order=0)
    @ArrayLengthMarker(fieldName = "Kcv")
    public int KcvLen;
    @StructField(order=1)
    public char[] Kcv;
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
    @ArrayLengthMarker(fieldName = "IDv")
    public int IDvLen;
    @StructField(order = 7)
    public char[] IDv;
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
    public TicketV(String Kcv1,String IDc1,String ADc1,String IDv1,String TS1,String Lifetime1){
        this.KcvLen=Kcv1.length();
        this.Kcv=Kcv1.toCharArray();
        this.IDcLen=IDc1.length();
        this.IDc=IDc1.toCharArray();
        this.ADcLen=ADc1.length();
        this.ADc=ADc1.toCharArray();
        this.IDvLen=IDv1.length();
        this.IDv=IDv1.toCharArray();
        this.TSLen=TS1.length();
        this.TS=TS1.toCharArray();
        this.LifetimeLen=Lifetime1.length();
        this.Lifetime=Lifetime1.toCharArray();
    }
}
