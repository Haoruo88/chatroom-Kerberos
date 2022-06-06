package des;

/**
 * F轮函数
 * 
 * @author heao
 */

public class FFunction {

	/**
	 * F轮函数，输入32bit明文（密文）右半部分、48bit子密钥, 返回32bit结果
	 * 
	 * @param r_plaintext 明文（密文）右半部分
	 * @param subkey      子密钥
	 * @return f_fun_result 输出结果
	 */
	public static int[] fFuction(int[] r_text, int[] subkey) {
		int[] e_output = new int[48];// E和异或的输出
		for (int i = 0; i < 48; i++) {// E扩展 32 --> 48、和密钥异或
			e_output[i] = r_text[Table.E[i] - 1] ^ subkey[i];
		}

		int[] sbox_output = new int[32];// S盒输出
		for (int i = 0; i < 8; i++) {// S盒压缩替换，48bit --> 32bit
			int r = (e_output[i * 6] << 1) + e_output[i * 6 + 5];// S盒表行
			int c = (e_output[i * 6 + 1] << 3) + (e_output[i * 6 + 2] << 2) + (e_output[i * 6 + 3] << 1)
					+ e_output[i * 6 + 4];// S盒表列
			String str = Integer.toBinaryString(Table.S_BOX[i][r][c]);// S盒行列计数从0开始
			while (str.length() < 4) {// 长度不足则补足4位
				str = "0" + str;
			}
			int p;
			for (int j = 0; j < 4; j++) {// 将字符串转化为对应的二进制int型数组
				p = Integer.valueOf(str.charAt(j));
				if (p == 48) {
					p = 0;
				} else {
					p = 1;
				}
				sbox_output[4 * i + j] = p;
			}
		}

		int[] f_fun_result = new int[32];// F轮函数输出结果
		for (int i = 0; i < 32; i++) {// P置换
			f_fun_result[i] = sbox_output[Table.P[i] - 1];
		}
		
		return f_fun_result;
	}

}
