package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageVtoCPost {
    @StructField(order = 0)
    public byte status;
    @StructField(order = 1)
    public int totalLen;
    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "EkCV")
    public int EkCVLen;
    @StructField(order = 3)
    public byte[] EkCV;
    public PackageVtoCPost(int totalLen1,byte[] Ekcv1){
        this.status=6;
        this.totalLen=totalLen1;
        this.EkCVLen=Ekcv1.length;
        this.EkCV=Ekcv1;
    }
}
