package Utils;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

/**
 * RSA算法实现
 */
public class RSA {
	public RSA() {
	}

//	public static void main(String[] args) {
//		BigInteger e = new BigInteger("65537");
//		BigInteger[] privateKey = new BigInteger[2];
//		BigInteger[] publicKey = new BigInteger[2];
//		privateKey = RSA.generateKey(e);
//		publicKey[0] = privateKey[0];
//		publicKey[1] = e;
//		byte[] origin = new byte[]{11,23,12,11,0};
//		System.out.println(Arrays.toString(publicKey));
//		System.out.println(Arrays.toString(privateKey));
//		System.out.println(privateKey[0].toString());
//		System.out.println(privateKey[1].toString());
//	}

	public static void main(String[] args) {
		String account = "1111111111";
		BigInteger[] privateKey = new BigInteger[2];
		BigInteger[] publicKey = new BigInteger[2];
		Util.readPrivateKey(privateKey,account);
		Util.readPublicKey(publicKey,account);
			byte[] b = new byte[]{11,32,43,0,1};

		try {
			MessageDigest messageDigest = null;
			messageDigest = MessageDigest.getInstance("MD5");
			byte[] md = messageDigest.digest(b);//生成hash值
			System.out.println(Arrays.toString(md));
			byte[] b1 = RSA.encryption(md,privateKey[0],privateKey[1]);
			System.out.println(Arrays.toString(b1));
			byte[] b2 = RSA.decryption(b1,publicKey[0],publicKey[1]);
			System.out.println(Arrays.toString(b2));
			if (Arrays.equals(md, b2)) {
				System.out.println("true");
			}
		} catch (UnsupportedEncodingException | pqException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 自定义异常内部类，用于输出由于明文过长产生的异常。
	 */
	public static class pqException extends Exception {
		public static final long serialVersionUID = -7777566816579144864L;

		public pqException(String message) {
			super(message);
		}
	}

	/**
	 * 密钥产生，返回产生两个大质数的乘积n、私钥d。
	 */
	public static BigInteger[] generateKey(BigInteger e) {
		BigInteger p = generateNBitRandomPrime(1024);// 两个大素数
		BigInteger q = generateNBitRandomPrime(1024);
		BigInteger φn;// = (p-1)(q-1)

		BigInteger n = p.multiply(q);
		φn = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		BigInteger d = RSA.extdGcd(e, φn)[1];// 利用扩展欧几里得算法求私钥d
		if (d.compareTo(BigInteger.ZERO) != 1) {// 私钥不可以小于0
			d = d.add(φn);
		}

		BigInteger[] nd = new BigInteger[2];
		nd[0] = n;
		nd[1] = d;
		return nd;
	}
	/**
	 * RSA加密
	 *
	 * @param plainText 明文
	 * @param n         两个大质数乘积
	 * @param e         公钥
	 * @return byte[]，加密后的byte数组
	 * @throws UnsupportedEncodingException
	 * @throws pqException
	 */
	public static byte[] encryption(byte[] plainText, BigInteger n, BigInteger e)
			throws UnsupportedEncodingException, pqException {
		String plainTextString = new String(plainText,StandardCharsets.ISO_8859_1);	// ISO-8859-1 编码是单字节编码
		String textNum = "";// 明文数字字符串表示形式
		BigInteger m = BigInteger.ZERO;// 明文数字表示形式
		byte[] textByte = plainTextString.getBytes(StandardCharsets.ISO_8859_1);
		for (int i = 0; i < textByte.length; i++) {// 每个字节用3位数的整数表示，不够则在前面补0
			int bn = textByte[i] & 0xff;
			if (bn < 10) {
				textNum += "00" + bn;
			} else if (bn < 100) {
				textNum += "0" + bn;
			} else {
				textNum += bn;
			}
		}
		m = new BigInteger(textNum);

		if (m.compareTo(n) == -1) {// m < n，可直接加密
			m = m.modPow(e, n);
			return m.toByteArray();
		} else {
			throw new pqException("明文长度过长");
		}
	}
	/**
	 * RSA解密
	 * 
	 * @param cipher 密文
	 * @param n      两个大质数乘积
	 * @param d      私钥
	 * @return 解密后的byte[]
	 */
	public static byte[] decryption(byte[] cipher, BigInteger n, BigInteger d) {
		BigInteger c = new BigInteger(cipher);
		BigInteger m = c.modPow(d, n);

		String mToString = m.toString();
		int mToStringLengthMod = 0;
		mToStringLengthMod = m.toString().length() % 3;
		if (mToStringLengthMod != 0) {// 由于加密时String转BigInter时前者前面的0并不会计入，所以需要确认并补全
			for (int j = 0; j < 3 - mToStringLengthMod; j++) {
				mToString = "0" + mToString;
			}
		}

		int byteNum = mToString.length() / 3;// 明文总字节数
		byte[] result = new byte[byteNum];
		for (int i = 0; i < byteNum; i++) {// 每三位数转化为byte型并返回该byte数组所表达的字符串
			result[i] = (byte) (Integer.parseInt(mToString.substring(i * 3, i * 3 + 3)));
		}
		return result;
	}

	/**
	 * 利用扩展欧几里得算法求出私钥d，使得de = kφ(n)+1，k为整数。
	 * 
	 * @param e  公钥
	 * @param φn =(p-1)(q-1)
	 * @return gdk BigInteger数组形式返回最大公约数、私钥d、k
	 */
	private static BigInteger[] extdGcd(BigInteger e, BigInteger φn) {
		BigInteger[] gdk = new BigInteger[3];

		if (φn.compareTo(BigInteger.ZERO) == 0) {
			gdk[0] = e;
			gdk[1] = BigInteger.ONE;
			gdk[2] = BigInteger.ZERO;
			return gdk;
		} else {
			gdk = extdGcd(φn, e.remainder(φn));
			BigInteger tmp_k = gdk[2];
			gdk[2] = gdk[1].subtract(e.divide(φn).multiply(gdk[2]));
			gdk[1] = tmp_k;
			return gdk;
		}
	}

	/**
	 * 随机产生n比特的素数。最高位是1，从高位开始随机产生共n-1个0和1（0和1的比例是随机的，不是
	 * 平均），并将每位所得值相加最终形成一个n位数。然后判断该数是否是素数，是则返回，否则重新开始产生新的数直至该数为素数为止。
	 * 
	 * @param n 比特数
	 * @return result 结果
	 */
	private static BigInteger generateNBitRandomPrime(int n) {
		BigInteger tmp = new BigInteger("2").pow(n - 1);// 最高位肯定是1
		BigInteger result = new BigInteger("2").pow(n - 1);
		Random random = new Random();
		int r1 = random.nextInt(101);// 产生0-100的整数，用于确定0和1的比例
		int r2;

		while (true) {// 循环产生数，直到该数为素数
			for (int i = n - 2; i >= 0; i--) {// 逐位产生表示数的0和1，并根据所在位计算结果相加起来
				r2 = random.nextInt(101);
				if (0 < r2 && r2 < r1) {// 产生的数为1
					result = result.add(BigInteger.valueOf(2).pow(i));
				}
				continue;
			}
			if (result.isProbablePrime(5)) {// 素数判断
				return result;
			}
			result = tmp;// 重新计算
		}
	}

}
