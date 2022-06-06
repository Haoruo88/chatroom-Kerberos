package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;
/**
 * @author  xyb
 * @version 1.0
 */
@StructClass
public class PackageAstoCAuth {
    @StructField(order = 0)
    public byte status;
    @StructField(order = 1)
    @ArrayLengthMarker(fieldName = "Ekc")
    public int EkcLen;
    @StructField(order = 2)
    public byte[] Ekc;
    public PackageAstoCAuth(byte[] Ekc1){
        this.status=0;
        this.EkcLen=Ekc1.length;
        this.Ekc=Ekc1;
    }
}
