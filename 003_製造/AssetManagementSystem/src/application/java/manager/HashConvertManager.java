package application.java.manager;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import application.java.common.AppConst;
import application.java.common.AppUtil;

/**
 * HASH値操作専用サービス
 */
public class HashConvertManager {
	
	private final String FORM_NAME = "HashConvertManager";

	/**
	 * 対象文字(パスワード)：ハッシュ値比較
	 * @param passWord 平文パスワード
	 * @param hashedValue DBから取得したハッシュ値文字列
	 * @param separator 区切り文字（デフォルトは '$' の文字）
	 * @return 判定結果
	 * @brief 平文パスワードが、指定されたPBKDF2-SHA256ハッシュ値と一致するか検証する。<br>
	 * 形式: $アルゴリズム$ストレッチング回数$ソルト$ハッシュ値<br>
	 * "$pbkdf2-sha256$[ストレッチング回数]$[ソルト値]=$[ハッシュ値]="
	 */
	public boolean verifyPasswordSHA256PBKDF2(String passWord, String hashedValue, char separator) {

		if (passWord == null || passWord.isEmpty() || hashedValue == null || hashedValue.isEmpty()) {
			return false;
		}

		// HASH値を分割 (JavaのSplitは正規表現を扱うため、区切り文字をエスケープして安全に分割します)
		String regexSeparator = "\\" + separator;
		String[] parts = hashedValue.split(regexSeparator);

		// 形式が不正な場合は検証不可 (C#のSplitの挙動に合わせ、先頭の空文字を含むため要素数チェックを行います)
		if (parts.length < 5 || !"pbkdf2-sha256".equals(parts[1])) {
			return false;
		}

		try {
			// 各要素の抽出
			int iterations = Integer.parseInt(parts[2]);
			byte[] salt = Base64.getDecoder().decode(parts[3]);
			byte[] storedHash = Base64.getDecoder().decode(parts[4]);

			// PBKDF2-SHA256ファクトリー生成(Java標準)
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			// C#側と全く同じ条件（パスワード、ソルト、回数、ビット長）でスペックを組立
			// ※ storedHash.length * 8 により、バイト長をビット長に変換して指定
			KeySpec spec = new PBEKeySpec(passWord.toCharArray(), salt, iterations, storedHash.length * 8);

			// ハッシュ（鍵）の生成
			byte[] generatedHash = factory.generateSecret(spec).getEncoded();

			// ※ C#のCryptographicOperations.FixedTimeEquals と同等の固定時間比較
			// タイミング攻撃（Timing Attack）を完全に防御して比較します
			return MessageDigest.isEqual(storedHash, generatedHash);

		} catch (Exception e) {
			// 既存のログシステムに準拠
			LogManager.writeError("[" + FORM_NAME + "] : パスワードハッシュ検証中に例外が発生しました", e);
			return false;
		}
	}

	/**
	 * Javaでのオーバーロード用メソッド（C#の引数初期値を再現）
	 * @brief 区切り文字を'$'とする
	 */
	public boolean verifyPasswordSHA256PBKDF2(String passWord, String hashedValue) {
		return this.verifyPasswordSHA256PBKDF2(passWord, hashedValue, '$');
	}
	
	/**
	 * 対象文字(パスワード)：ハッシュ値変換処理
	 * @param words 平文パスワード
	 * @param separator 区切り文字（デフォルトは '$' の文字）
	 * @return ハッシュ値文字列
	 * @brief 平文をPBKDF2-SHA256ハッシュ値に変換して返す。<br>
	 * 形式: $アルゴリズム$ストレッチング回数$ソルト$ハッシュ値<br>
	 * "$pbkdf2-sha256$[ストレッチング回数]$[ソルト値]=$[ハッシュ値]="<br>
	 * 例外の(変換できなかった)場合、空白又はNULLを返す
	 */
	public String convertWordsToSHA256PBKDF2(String words, char separator) {

		if (AppUtil.StringIsNullOrWhiteSpace(words)) { return "";}

		try {
			// 暗号学的に安全なランダムな「ソルト」を毎回生成
            byte[] saltBytes = new byte[AppConst.SALT_BYTE];
            new SecureRandom().nextBytes(saltBytes);

            // PBKDF2-SHA256 でハッシュ値を計算 (C#の Rfc2898DeriveBytes.Pbkdf2 に相当)
			// C#側と全く同じ条件（パスワード、ソルト、回数、ビット長）でスペックを組立
            // ※注意: Javaの鍵長(keyLength)は「ビット単位」で指定するため、バイト長をビット長に変換して指定
            KeySpec spec = new PBEKeySpec(
                    words.toCharArray(),
                    saltBytes,
                    AppConst.ITERATIONS,
                    AppConst.HASH_BYTE * 8
            );

			// PBKDF2-SHA256ファクトリー生成(Java標準)
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			// ハッシュ（鍵）の生成
			byte[] generatedHash = factory.generateSecret(spec).getEncoded();

            // 4. それぞれをBase64形式の文字列に変換 (C#の Convert.ToBase64String に相当)
            String saltBase64 = Base64.getEncoder().encodeToString(saltBytes);
            String hashBase64 = Base64.getEncoder().encodeToString(generatedHash);
			
            // 既存DBの1カラムに詰め込めるよう「$」で連結
            // 形式: $アルゴリズム$ストレッチング回数$ソルト(文字列)$ハッシュ値(文字列)
            return String.format(
            		"%1$cpbkdf2-sha256%1$c%2$d%1$c%3$s%1$c%4$s", 
            		separator, 
            		AppConst.ITERATIONS, 
            		saltBase64, 
            		hashBase64);
            
		} catch (Exception e) {
			// 既存のログシステムに準拠
			LogManager.writeError("[" + FORM_NAME + "] : ハッシュ生成中に例外が発生しました", e);
			return null;
		}
	}
}
