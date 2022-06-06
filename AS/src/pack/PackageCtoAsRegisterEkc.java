package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageCtoAsRegisterEkc {
    @StructField(order=0)
    @ArrayLengthMarker(fieldName = "IDc")
    public int IDcLen;
    @StructField(order=1)
    public char[] IDc;
    @StructField(order=2)
    @ArrayLengthMarker(fieldName = "TS1")
    public int TS1Len;
    @StructField(order=3)
    public char[] TS1;
    @StructField(order=4)
    @ArrayLengthMarker(fieldName = "name")
    public int nameLen;
    @StructField(order=5)
    public char[] name;
    @StructField(order=6)
    @ArrayLengthMarker(fieldName = "content")
    public int contentLen;
    @StructField(order=7)
    public byte[] content;
    public PackageCtoAsRegisterEkc(String IDc1,String TS11,String name1,byte[] content1){
        this.IDcLen=IDc1.length();
        this.IDc=IDc1.toCharArray();
        this.TS1Len=TS11.length();
        this.TS1=TS11.toCharArray();
        this.nameLen=name1.length();
        this.name =name1.toCharArray();
        this.contentLen=content1.length;
        this.content=content1;
    }
}
