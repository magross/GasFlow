package scp;
/**
 * This program will demonstrate the file transfer from remote to local $
 * CLASSPATH=.:../build javac ScpFrom.java $ CLASSPATH=.:../build java ScpFrom
 * user@remotehost:file1 file2 You will be asked passwd. If everything works
 * fine, a file 'file1' on 'remotehost' will copied to local 'file1'.
 *
 */
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnacondaServer {

    private String host;
    private JSch jsch;
    private File privateKey;
    private Session session;
    private Channel shellChannel;
    private String user;
    private boolean preserveModesAndTimes;

    public AnacondaServer() {
        host = "anaconda.mathematik.tu-darmstadt.de";
        jsch = new JSch();
        privateKey = new File("/homes/combi/gross/.ssh/id_rsa");
        user = "gross";
        if (privateKey.exists()) {
            try {
                jsch.addIdentity(privateKey.getAbsolutePath());
            } catch (JSchException ex) {
                java.util.logging.Logger.getLogger(AnacondaServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean startSession() {
        try {
            session = jsch.getSession(user, host, 22);
            UserInfo ui = new PassphraseUserInfo();
            session.setDaemonThread(true);
            session.setUserInfo(ui);
            session.connect();
            return true;
        } catch (JSchException ex) {
            Logger.getLogger(AnacondaServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void stopSession() {
        if (shellChannel != null) {
            shellChannel.setInputStream(null);
            shellChannel.setOutputStream(null);
            shellChannel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

    public boolean copyFileToLocal(String sourceThere, String destinationHere) {
        String command = "scp -f " + sourceThere;
        ChannelExec channel = null;
        try {            
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
        } catch (JSchException e) {
            System.out.println(e);
            return false;
        }
        try (OutputStream out = channel.getOutputStream(); InputStream in = channel.getInputStream()) {
            channel.connect();
            sendNull(out);
            while (true) {
                if (readNextByte(in) != 'C') {
                    break;
                }
                // read '0644 '
                byte[] buf = new byte[5];
                in.read(buf, 0, 5);
                long filesize = readFilesize(in);
                String file = readFilename(in);
                sendNull(out);
                // read a content of lfile
                String prefix = null;
                if (new File(destinationHere).isDirectory()) {
                    prefix = destinationHere + File.separator;
                }

                try (FileOutputStream fileOutputStream = new FileOutputStream(prefix == null ? destinationHere : prefix + file)) {
                    readFileContentTo(in, filesize, fileOutputStream);
                }
                if (!checkIfSuccess(in)) {
                    return false;
                }
                sendNull(out);
            }
            channel.disconnect();
            return true;
        } catch (JSchException | IOException ex) {
            Logger.getLogger(AnacondaServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean copyFileToRemote(String fileHere, String fileThere) {
        String command = "scp " + (preserveModesAndTimes ? "-p" : "") + " -t " + fileThere;
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();
        } catch (JSchException e) {
            return false;
        }
        try (OutputStream out = channel.getOutputStream(); InputStream in = channel.getInputStream();) {
            if (!checkIfSuccess(in)) {
                return false;
            }
            if (preserveModesAndTimes) {
                writeFileTimes(out, fileHere);
                if (!checkIfSuccess(in)) {
                    return false;
                }
            }
            writeFilesizeAndName(out, fileHere);
            if (!checkIfSuccess(in)) {
                return false;
            }
            try (FileInputStream fileInputStream = new FileInputStream(fileHere)) {
                writeFileContentTo(out, fileInputStream);
            }
            sendNull(out);
            if (!checkIfSuccess(in)) {
                return false;
            }
            channel.disconnect();
        } catch (IOException ex) {
            Logger.getLogger(AnacondaServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean openShell() {
        return openShell(System.in, System.out);
    }

    public boolean openShell(InputStream in, OutputStream out) {
        try {
            shellChannel = session.openChannel("shell");
            shellChannel.setInputStream(System.in);
            shellChannel.setOutputStream(System.out);
            shellChannel.connect();
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean isShellChannelOpen() {
        return shellChannel != null && shellChannel.isConnected();
    }

    private boolean checkIfSuccess(InputStream in) throws IOException {
        return readNextByte(in) == 0;
    }

    private int readNextByte(InputStream in) throws IOException {
        int b = in.read();
        StringBuilder builder;
        switch (b) {
            case 1:
                builder = new StringBuilder("Error: ");
                break;
            case 2:
                builder = new StringBuilder("Fatal Error: ");
                break;
            default:
                return b;
        }
        int c;
        do {
            c = in.read();
            builder.append((char) c);
        } while (c != '\n');
        throw new IOException(builder.toString());
    }

    /**
     * Sends '\0' to the specified output stream.
     */
    private void sendNull(OutputStream out) throws IOException {
        byte[] buffer = new byte[1];
        buffer[0] = 0;
        out.write(buffer, 0, 1);
        out.flush();
    }

    private long readFilesize(InputStream in) throws IOException {
        byte[] buf = new byte[1];
        long filesize = 0L;
        while (true) {
            if (in.read(buf, 0, 1) < 0) {
                break;
            }
            if (buf[0] == ' ') {
                break;
            }
            filesize = filesize * 10L + (long) (buf[0] - '0');
        }
        return filesize;
    }

    private String readFilename(InputStream in) throws IOException {
        String file = null;
        byte[] buf = new byte[1024];
        for (int i = 0;; i++) {
            in.read(buf, i, 1);
            if (buf[i] == (byte) 0x0a) {
                file = new String(buf, 0, i);
                break;
            }
        }
        return file;
    }

    private void writeFileTimes(OutputStream out, String fileHere) throws IOException {
        File file = new File(fileHere);
        String command = "T" + (file.lastModified() / 1000) + " 0";
        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class
        );
        command += (" " + (attrs.lastAccessTime().to(TimeUnit.MILLISECONDS)) + " 0\n");

        out.write(command.getBytes());
        out.flush();
    }

    private void writeFilesizeAndName(OutputStream out, String fileHere) throws IOException {
        File _lfile = new File(fileHere);
        // send "C0644 filesize filename", where filename should not include '/'
        long filesize = _lfile.length();
        String command = "C0644 " + filesize + " ";
        if (fileHere.lastIndexOf('/') > 0) {
            command += fileHere.substring(fileHere.lastIndexOf('/') + 1);
        } else {
            command += fileHere;
        }
        command += "\n";
        out.write(command.getBytes());
        out.flush();
    }

    private void readFileContentTo(InputStream in, long filesize, FileOutputStream fos) throws IOException {
        byte[] buffer = new byte[1024];
        int blockLength = buffer.length;
        while (true) {
            if (buffer.length >= filesize) {
                blockLength = (int) filesize;
            }
            blockLength = in.read(buffer, 0, blockLength);
            if (blockLength < 0) {
                break;
            }
            fos.write(buffer, 0, blockLength);
            filesize -= blockLength;
            if (filesize == 0L) {
                break;
            }
        }
    }

    private void writeFileContentTo(OutputStream out, FileInputStream fis) throws IOException {
        int blockLength;
        byte[] buffer = new byte[1024];
        while (true) {
            blockLength = fis.read(buffer, 0, buffer.length);
            if (blockLength <= 0) {
                break;
            }
            out.write(buffer, 0, blockLength);
            out.flush();
        }
    }

    public boolean copyProjectToRemote(String projectFolderHere, String projectFolderThere) {
        String folder, name, prefix;
        if (projectFolderHere.endsWith(File.pathSeparator)) {
            folder = projectFolderHere.substring(0, projectFolderHere.length()-1);
        } else {
            folder = projectFolderHere;
        }
        if (folder.contains(File.pathSeparator)) {
            name = folder.substring(folder.indexOf(File.pathSeparator)+1);
        } else {
            name = folder;
        }
        prefix = folder + File.pathSeparator + name;
        copyFileToRemote(prefix + ".xml", name);
        return true;
    }

    public static void main(String[] args) {
        AnacondaServer scp = new AnacondaServer();
        scp.startSession();
        scp.copyFileToLocal("examples/onePipe/onePipe2.xml", "/homes/combi/gross/test.xml");
        System.out.println("Done");
        scp.copyFileToRemote("/homes/combi/gross/test.xml", "examples/onePipe/onePipe2.xml");
        System.out.println("Done");
        scp.openShell();
        try {
            sleep(10);
        } catch (InterruptedException ex) {
            System.out.println(ex);
            Logger.getLogger(AnacondaServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        scp.stopSession();
        System.out.println("Closing");
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        System.out.println(threadSet);
    }

}
