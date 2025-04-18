package cc.unknown.util.client.netty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cc.unknown.util.Accessor;
import cc.unknown.util.render.client.ChatUtil;
import cc.unknown.util.structure.comparators.ValuePair;
import net.minecraft.entity.player.EntityPlayer;

public class NetworkUtil implements Accessor {
		
    private static final int BUFFER_SIZE = 4096;

    public static void downloadResources(String url, File directory, String fileName, String folderName, String input, String outPut) {
        try {
        	ChatUtil.display(input);
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File zipFile = new File(tempDir, fileName);
            File extractDir = tempDir;

            downloadFile(url, zipFile);

            unzip(zipFile.toString(), extractDir.toString());

            File dllSourceDir = new File(extractDir, folderName);
            File dllTargetDir = directory;

            if (dllSourceDir.exists() && dllSourceDir.isDirectory()) {
                copyDLLs(dllSourceDir, dllTargetDir);
                ChatUtil.display(outPut);
            } else {
                System.err.println("No se encontró la carpeta 'resources'");
            }

            Files.deleteIfExists(zipFile.toPath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(String fileURL, File saveFile) throws IOException {
    	ChatUtil.display("Descargando: " + fileURL);
        try (InputStream in = new URL(fileURL).openStream();
             FileOutputStream out = new FileOutputStream(saveFile)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                ChatUtil.display("Buffer: " + buffer);
                ChatUtil.display("Bytes: " + bytesRead);
            }
        }
        ChatUtil.display("Descarga completada: " + saveFile);
    }

    private static void unzip(String zipFilePath, String destDirectory) throws IOException {
    	ChatUtil.display("Extrayendo ZIP en: " + destDirectory);
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                File newFile = new File(destDirectory, entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int len;
                        while ((len = zipIn.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipIn.closeEntry();
            }
        }
        ChatUtil.display("Extracción completada.");
    }

    private static void copyDLLs(File sourceDir, File targetDir) {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        File[] dllFiles = sourceDir.listFiles((dir, name) -> name.endsWith(".dll"));
        if (dllFiles == null || dllFiles.length == 0) {
            return;
        }

        for (File dll : dllFiles) {
            File targetFile = new File(targetDir, dll.getName());
            try {
                Files.copy(dll.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                ChatUtil.display("DLL Copiada: " + dll.getName() + " → " + targetDir);
            } catch (IOException e) {
            	ChatUtil.display("Error copiando DLL: " + dll.getName());
                e.printStackTrace();
            }
        }
    }

    public static List<ValuePair> parse(String query, Charset charset) {
        List<ValuePair> result = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return result;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            try {
                String key = URLDecoder.decode(keyValue[0], charset.name());
                String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], charset.name()) : "";
                result.add(new ValuePair(key, value));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
    
	public static int getPing(EntityPlayer player) {
		return mc.getNetHandler().getPlayerInfo(player.getUniqueID()) != null ? mc.getNetHandler().getPlayerInfo(player.getUniqueID()).getResponseTime() : 0;
	}
	
	public static boolean checkNet() {
	    try {
	        InetAddress address = InetAddress.getByName("google.com");
	        return address.isReachable(1000);
	    } catch (IOException e) {
	        return false;
	    }
	}
	
    public static String getRaw(String url){
        try {
            URL website = new URL(url);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(website.openStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString().trim();
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRaw(String url, String instruction) {
        try {
            URL website = new URL(url);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(website.openStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" \\| ");
                    if (parts.length == 2 && parts[1].equals(instruction)) {
                        response.append(parts[0]);
                        break;
                    }
                }
                return response.toString().trim();
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean isServer(String server) {
        return mc.getIntegratedServer() == null
                && mc.getCurrentServerData() != null
                && mc.getCurrentServerData().serverIP.toLowerCase().contains(server);
    }
}
