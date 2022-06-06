package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageAstoCModify {
    @StructField(order = 0)
    public byte status;
    @StructField(order = 1)
    @ArrayLengthMarker(fieldName = "IDc")
    public int IDcLen;
    @StructField(order = 2)
    public char[] IDc;
    @StructField(order = 3)
    public byte modifyStatus;
    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "TS")
    public int TSLen;
    @StructField(order = 5)
    public char[] TS;
    @StructField(order = 6)
    public byte[] redundancy;
    public PackageAstoCModify(String IDc1,byte modifyStatus1,String TS1){
        this.status=8;
        this.IDcLen=IDc1.length();
        this.IDc=IDc1.toCharArray();
        this.modifyStatus=modifyStatus1;
        this.TSLen=TS1.length();
        this.TS=TS1.toCharArray();
        this.redundancy=new byte[10];
    }
}
