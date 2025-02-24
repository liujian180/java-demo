package com.wujt.crypto;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 * @author wujt
 */
public class DESedeUtil {
    private DESedeUtil() {
    }

    public static SecretKey generateKey(byte[] seed) throws CryptoException {
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DESede");
            return secretKeyFactory.generateSecret(new DESedeKeySpec(seed));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CryptoException(e);
        }
    }

    public static IvParameterSpec generateIv(byte[] seed) {
        return new IvParameterSpec(Arrays.copyOf(seed, 8));
    }
}
