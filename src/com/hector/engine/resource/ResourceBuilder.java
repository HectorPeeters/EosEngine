package com.hector.engine.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A helper class to generate the asset archive instead of using platform dependant scripts. With this system we are
 * also able to process resources before making the archive.
 *
 * @author HectorPeeters
 */
public class ResourceBuilder {

    /**
     * Creates the resource archive from the assets/ folder.
     */
    public static void makeResourceArchive() {
        System.out.println("Started building asset archive...");
        File assetsFolder = new File("../../../assets/");
        File outputFile = new File("Assets.zip");

        long totalSize = 0;
        try {

            FileOutputStream fos = new FileOutputStream(outputFile);

            ZipOutputStream zos = new ZipOutputStream(fos);

            File[] files = assetsFolder.listFiles();

            if (files == null) {
                System.err.println("Folder \'" + assetsFolder.getAbsolutePath() + "\' does not exist");
                return;
            }

            //TODO: handle files in assets/ folder as well
            for (File f : files) {
                if (f.isDirectory())
                    totalSize += addDirToZipArchive(zos, f, "");
            }

            zos.flush();
            zos.close();
        } catch (IOException ioe) {
            System.out.println("Error creating zip file: " + ioe);
        }

        System.out.println("Finished building resource archive (" + humanReadableByteCount(totalSize, false) + ")");

        System.out.println("\n\n");
    }

    /**
     * Adds a file to the zip archive
     *
     * @param zos                 The {@link ZipOutputStream} to store the file in
     * @param fileToZip           The file to store in the zip
     * @param parentDirectoryName The name of the parent directory for proper file hierarchy
     * @return The amount of bytes added to the zip file
     * @throws IOException When reading the file fails
     */
    private static long addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parentDirectoryName) throws IOException {
        if (fileToZip == null || !fileToZip.exists()) {
            return 0;
        }

        String zipEntryName = fileToZip.getName();
        if (parentDirectoryName != null && !parentDirectoryName.isEmpty()) {
            zipEntryName = parentDirectoryName + "/" + fileToZip.getName();
        }

        if (fileToZip.isDirectory()) {
            long total = 0;
            for (File file : fileToZip.listFiles()) {
                total += addDirToZipArchive(zos, file, zipEntryName);
            }
            return total;
        } else {
//            System.out.println("Adding " + zipEntryName + "...");
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(fileToZip);
            zos.putNextEntry(new ZipEntry(zipEntryName));
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
            return fileToZip.length();
        }
    }

    /**
     * Small utility method to convert byte amount to a human readable format
     *
     * @param bytes The amount of bytes
     * @param si    Whether or not we should convert to SI unit
     * @return The human readable {@link String}
     */
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.11f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
