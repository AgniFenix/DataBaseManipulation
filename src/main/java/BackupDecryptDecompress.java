import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;

/**
 * Esta clase se encarga de realizar descompresion de archivo 7zip y cifrar archivos.
 */

public class BackupDecryptDecompress {
    public BackupDecryptDecompress() throws Exception {
        String encryptedBackupPath = "backup_encrypted.7z";
        String decryptedBackupPath = "backup_decrypted.7z";
        String password = "password";

        // Decrypt compressed backup file
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] key = sha.digest(password.getBytes("UTF-8"));
        key = copyOf(key, 16);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        try (FileInputStream fis = new FileInputStream(encryptedBackupPath);
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             FileOutputStream fos = new FileOutputStream(decryptedBackupPath)) {
            byte[] data = new byte[1024];
            int read;
            while ((read = cis.read(data)) != -1) {
                fos.write(data, 0, read);
            }
            fos.flush();
        }

        // Decompress decrypted backup file
        SevenZFile sevenZFile = new SevenZFile(new File(decryptedBackupPath));
        SevenZArchiveEntry entry;
        while ((entry = sevenZFile.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            File curfile = new File(entry.getName());
            File parent = curfile.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(curfile);
            byte[] content = new byte[(int) entry.getSize()];
            sevenZFile.read(content, 0, content.length);
            out.write(content);
            out.close();
        }
    }

    private static byte[] copyOf(byte[] original, int newLength) {
        byte[] copy = new byte[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }
}