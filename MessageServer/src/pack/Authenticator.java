package pack;
import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class Authenticator {
    @StructField(order = 0)
    @ArrayLengthMarker(fieldName = "IDc")
    public int IDcLen;
    @StructField(order = 1)
    public char[] IDc;
    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "ADc")
    public int ADcLen;
    @StructField(order = 3)
    public char[] ADc;
    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "TS")
    public int TSLen;
    @StructField(order = 5)
    public char[] TS;
    @StructField(order = 6)
    public byte[] redundancy;
    public Authenticator(String IDc1,String ADc1,String TS1){
        this.IDcLen=IDc1.length();
        this.IDc= IDc1.toCharArray();
        this.ADcLen=ADc1.length();
        this.ADc=ADc1.toCharArray();
        this.TSLen=TS1.length();
        this.TS=TS1.toCharArray();
        this.redundancy=new byte[10];
    }
}
