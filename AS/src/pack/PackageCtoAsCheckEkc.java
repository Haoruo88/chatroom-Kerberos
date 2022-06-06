package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;
/**
 * @author  xyb
 * @version 1.0
 */
@StructClass
public class PackageCtoAsCheckEkc {
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
    public byte[] redundancy;
    @StructField(order=5)
    @ArrayLengthMarker(fieldName = "content")
    public int contentLen;
    @StructField(order=6)
    public byte[] content;
    public PackageCtoAsCheckEkc(String IDc1,String TS11,byte[] content1){
        this.IDcLen=IDc1.length();
        this.IDc=IDc1.toCharArray();
        this.TS1Len=TS11.length();
        this.TS1=TS11.toCharArray();
        this.redundancy=new byte[10];
        this.contentLen=content1.length;
        this.content=content1;
    }
}
