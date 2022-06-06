package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageCtoAsModify {
    @StructField(order=0)
    public byte status;
    @StructField(order = 1)
    @ArrayLengthMarker(fieldName = "EKc")
    public int EKcLen;
    @StructField(order = 2)
    public byte[] EKc;
    public PackageCtoAsModify(byte[] Ekc1){
        this.status=2;
        this.EKcLen=Ekc1.length;
        this.EKc=Ekc1;
    }
}
