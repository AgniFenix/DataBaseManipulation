import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Esta clase se encarga de realizar copias de seguridad, comprimir y cifrar archivos.
 */
public class BackupCompressEncrypt {

    /**
     * Esta clase se encarga de realizar copias de seguridad, comprimir y cifrar archivo
     *
     * @throws Exception Arroja una exepcion si el comprimido o el cifrado no se realizan
     */
    public BackupCompressEncrypt() throws Exception {
        String backupPath = "backup.7z";
        String encryptedBackupPath = "backup_encrypted.7z";
        String password = "";

        // Compress backup file
        SevenZOutputFile sevenZOutput = new SevenZOutputFile(new File(backupPath));
        File entryFile = new File("backup.txt");
        SevenZArchiveEntry entry = sevenZOutput.createArchiveEntry(entryFile, entryFile.getName());
        sevenZOutput.putArchiveEntry(entry);
        FileInputStream in = new FileInputStream(entryFile);
        byte[] buffer = new byte[8192];
        int len;
        while ((len = in.read(buffer)) > 0) {
            sevenZOutput.write(buffer, 0, len);
        }
        sevenZOutput.closeArchiveEntry();
        sevenZOutput.close();

        // Encrypt compressed backup file
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] key = sha.digest(password.getBytes(StandardCharsets.UTF_8));
        key = copyOf(key, 16);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        try (FileInputStream fis = new FileInputStream(backupPath);
             FileOutputStream fos = new FileOutputStream(encryptedBackupPath);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
            byte[] data = new byte[1024];
            int read;
            while ((read = fis.read(data)) != -1) {
                cos.write(data, 0, read);
            }
            cos.flush();
        }
    }

    @NotNull
    private static byte[] copyOf(byte[] original, int newLength) {
        byte[] copy = new byte[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }
}