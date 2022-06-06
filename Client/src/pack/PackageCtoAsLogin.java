package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageCtoAsLogin {
    @StructField(order = 0)
    public byte status;
    @StructField(order = 1)
    @ArrayLengthMarker(fieldName = "Ekc")
    public int EkcLen;
    @StructField(order = 2)
    public byte[] Ekc;
    public PackageCtoAsLogin(byte[] Ekc1){
        this.status=1;
        this.EkcLen=Ekc1.length;
        this.Ekc=Ekc1;
    }
}
