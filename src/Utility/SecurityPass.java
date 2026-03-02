package Utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityPass {
    public static String maHoa(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] harsh = md.digest(password.getBytes());
            StringBuilder hexString =   new StringBuilder();
            for(byte b : harsh){
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("Loi thuat toan bam.", e);
        }
    }
}
