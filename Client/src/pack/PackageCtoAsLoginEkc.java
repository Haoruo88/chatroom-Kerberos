package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageCtoAsLoginEkc {
    @StructField(order = 0)
    @ArrayLengthMarker(fieldName = "IDc")
    public int IDcLen;
    @StructField(order = 1)
    public char[] IDc;
    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "IDtgs")
    public int IDtgsLen;
    @StructField(order = 3)
    public char[] IDtgs;
    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "TS1")
    public int TS1Len;
    @StructField(order = 5)
    public char[] TS1;
    @StructField(order = 6)
    public byte[] redundancy;
    @StructField(order =7)
    @ArrayLengthMarker(fieldName = "content")
    public int contentLen;
    @StructField(order = 8)
    public byte[] content;
    public PackageCtoAsLoginEkc(String IDc1,String IDtgs1,String TS11,byte[] content1){
        this.IDcLen=IDc1.length();
        this.IDc=IDc1.toCharArray();
        this.IDtgsLen=IDtgs1.length();
        this.IDtgs=IDtgs1.toCharArray();
        this.TS1Len=TS11.length();
        this.TS1=TS11.toCharArray();
        this.redundancy=new byte[10];
        this.contentLen=content1.length;
        this.content=content1;
    }
}
