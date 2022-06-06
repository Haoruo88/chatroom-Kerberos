package pack;

import struct.StructClass;
import struct.StructField;
/**
 * @author  xyb
 * @version 1.0
 */
@StructClass
public class PackageAstoCLogin {
    @StructField(order = 0)
    public byte status;
    @StructField(order = 1)
    public byte loginStatus;
    public PackageAstoCLogin(byte loginStatus1){
        this.status=2;
        this.loginStatus=loginStatus1;
    }
}
