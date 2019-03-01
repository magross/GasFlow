/**
 * PassphraseUserInfo.java
 *
 */
package scp;

import com.jcraft.jsch.UserInfo;
import javax.swing.JPasswordField;

/**
 *
 * @author Martin Groß
 */
public class PassphraseUserInfo implements UserInfo {

    private String passphrase;
    private final JPasswordField passphraseField;

    public PassphraseUserInfo() {
        passphraseField = new JPasswordField(20);
    }

    @Override
    public String getPassword() {
        throw new AssertionError("Not supported");
    }

    @Override
    public boolean promptYesNo(String str) {
        return true;
    }

    @Override
    public String getPassphrase() {
        return passphrase;
    }

    @Override
    public boolean promptPassphrase(String message) {
        PassphraseDialog dialog = new PassphraseDialog();
        dialog.setVisible(true);
        passphrase = dialog.getPassphrase();
        return true;
    }

    @Override
    public boolean promptPassword(String message) {
        throw new AssertionError("Not supported");
    }

    @Override
    public void showMessage(String message) {
        throw new AssertionError("Not supported");
    }
}
