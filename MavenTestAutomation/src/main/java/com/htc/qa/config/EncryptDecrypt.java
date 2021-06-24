package com.htc.qa.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import com.htc.qa.config.Config.ConfigProperty;

/**
 * Class to encrypt decrypt the plain text passwords using salt and
 * 
 * @author anushar
 */
public class EncryptDecrypt {

	private EncryptDecrypt() {
		// no-op
	}

	/**
	 * Util to encrypt clear text
	 * 
	 * @param clearText
	 */
	public static void enCrypt(String clearText) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(Config.getConfigProperty(ConfigProperty.CRYPTO_PROPERTY));
		encryptor.setKeyObtentionIterations(1000);
		String encryptedText = encryptor.encrypt(clearText);
		System.out.println("encryptedText= " + encryptedText);
	}

	/**
	 * Util to de-crypt encrypted text
	 * 
	 * @param clearText
	 */
	public static String deCrypt(String encryptedText) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(Config.getConfigProperty(ConfigProperty.CRYPTO_PROPERTY));
		encryptor.setKeyObtentionIterations(1000);
		return encryptor.decrypt(encryptedText);
	}
	
	public static void main (String[] args) {
		enCrypt("clearText");
	}

}