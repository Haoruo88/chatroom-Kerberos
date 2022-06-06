package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageVtoCPostStatus {
    @StructField(order = 0)
    public byte status;
    @StructField(order = 1)
    @ArrayLengthMarker(fieldName = "TS")
    public int TSLen;
    @StructField(order = 2)
    public char[] TS;
    @StructField(order = 3)
    public byte[] redundancy;
    public PackageVtoCPostStatus(String TS1){
        this.status=9;
        this.TSLen=TS1.length();
        this.TS=TS1.toCharArray();
        this.redundancy=new byte[10];
    }
}
