package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageCtoAsRegister {
    @StructField(order=0)
    public byte status;
    @StructField(order = 1)
    @ArrayLengthMarker(fieldName = "EKc")
    public int EKcLen;
    @StructField(order = 2)
    public byte[] EKc;
    public PackageCtoAsRegister(byte[] Ekc1){
        this.status=0;
        this.EKcLen=Ekc1.length;
        this.EKc=Ekc1;
    }

}
