package pack;

import struct.StructClass;
import struct.StructField;
/**
 * @author  xyb
 * @version 1.0
 */
@StructClass
public class PackageAstoCCheck {
    @StructField(order = 0)
    public byte status;
    @StructField(order = 1)
    public byte checkStatus;
    public PackageAstoCCheck(byte checkStatus1){
        this.status=3;
        this.checkStatus=checkStatus1;
    }
}
