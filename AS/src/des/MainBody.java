package des;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 根据DES算法原理实现DES算法，该类为算法主体部分
 * String TEXT = "永远相信美好的事情即将发生";//明文。任意长度
 * String KEY = "zhz";// 密钥。任意长度，不足则补0，多则取前部分64bit
 * byte[] c = new MainBody(TEXT.getBytes(), KEY, 1).mainBody();// 加密，最后的字节数是8的倍数，根据des加密原理可得
 * byte[] p = new MainBody(c, KEY, 0).mainBody();// 解密
 *
 * @author zhz
 */

public class MainBody {

    private int[][] groupText;// 分组后的明文，每组64bit
    private int[][] subkeys;// 16个子钥匙，每组48bit
    private int flag;// 0表示加密，1表示解密

    /**
     * 构造函数
     *
     * @param text 明文（密文）
     * @param key  密钥
     * @param flag 1表示加密，0表示解密
     * @throws UnsupportedEncodingException
     */
    public MainBody(byte[] text, String key, int flag) throws UnsupportedEncodingException {
        if (flag == 1) {// 如果是加密则在明文前面加上明文长度和符号@方便解密时去掉后面多出的的乱码
            int text_length = text.length;
            byte[] text_new = (text.length + "@").getBytes();
            int text_new_length = text_new.length;
            text_new = Arrays.copyOf(text_new, text_new_length + text_length);
            System.arraycopy(text, 0, text_new, text_new_length, text_length);
            this.groupText = GroupText.groupText(text_new);
        } else {// 加密
            this.groupText = GroupText.groupText(text);
        }

        this.subkeys = GenerateSubkeys.generateSubkeys(key);
        this.flag = flag;
    }

    /**
     * 算法主体部分
     *
     * @return result 加密/解密的byte数组型的结果
     * @throws UnsupportedEncodingException
     */
    public byte[] mainBody() throws UnsupportedEncodingException {
        byte[] result0 = new byte[8 * groupText.length];
        for (int g = 0; g < this.groupText.length; g++) {// 64bit为一组逐组加密、解密
            int[] IP_result = new int[64];
            for (int i = 0; i < 64; i++) {// IP置换
                IP_result[i] = groupText[g][Table.IP[i] - 1];
            }

            if (flag == 1) { // 16轮加密
                for (int i = 0; i < 16; i++) {
                    eachRound(IP_result, i);
                }
            } else { // 解密
                for (int i = 15; i > -1; i--) {
                    eachRound(IP_result, i);
                }
            }

            int[] IP_1_result = new int[64];
            for (int i = 0; i < 64; i++) {// IP-1置换
                IP_1_result[i] = IP_result[Table.IP_1[i] - 1];
            }

            byte[] tmp = new byte[8];// 每组的加密、解密结果
            for (int i = 0; i < 8; i++) {// int转化为二进制的c_byte数组
                tmp[i] = (byte) ((IP_1_result[8 * i] << 7) + (IP_1_result[8 * i + 1] << 6)
                        + (IP_1_result[8 * i + 2] << 5) + (IP_1_result[8 * i + 3] << 4) + (IP_1_result[8 * i + 4] << 3)
                        + (IP_1_result[8 * i + 5] << 2) + (IP_1_result[8 * i + 6] << 1) + (IP_1_result[8 * i + 7]));
            }

            System.arraycopy(tmp, 0, result0, g * 8, 8);// 将每组的结果复制到result数组返回
        }

        if (flag == 0) {// 解密时根据前面的长度返回正确的byte数组型明文
            for (int i = 0; i < result0.length; i++) {
                if (result0[i] == (byte) 64) {// 64即为@符号，处于明文长度和明文的中间
                    byte[] tmp0 = new byte[i];
                    System.arraycopy(result0, 0, tmp0, 0, i);
                    int j = Integer.parseInt(new String(tmp0));
                    byte[] result1 = new byte[j];
                    System.arraycopy(result0, i + 1, result1, 0, j);
                    return result1;
                }
            }
        }
        return result0;
    }

    /**
     * 16轮加密、解密中的每一轮
     *
     * @param IP_result 每组IP置换后的结果
     * @param roundNum  第几轮加密、解密
     */
    public void eachRound(int[] IP_result, int roundNum) {
        int[] L0 = new int[32];
        int[] R0 = new int[32];
        int[] L1 = new int[32];
        int[] R1 = new int[32];
        int[] f_result = new int[32];
        System.arraycopy(IP_result, 0, L0, 0, 32);
        System.arraycopy(IP_result, 32, R0, 0, 32);
        L1 = R0;

        f_result = FFunction.fFuction(R0, subkeys[roundNum]);// F轮函数，输入32bit明文右半部分、48bit子密钥, 返回32bit结果

        for (int j = 0; j < 32; j++) {
            R1[j] = L0[j] ^ f_result[j];
            if (((flag == 1) && (roundNum == 15)) || ((flag == 0) && (roundNum == 0))) {// 加密解密最后一轮不交换位置
                IP_result[j] = R1[j];
                IP_result[j + 32] = L1[j];
            } else {
                IP_result[j] = L1[j];
                IP_result[j + 32] = R1[j];
            }
        }
    }

}