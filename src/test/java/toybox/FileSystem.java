/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import javax.swing.filechooser.FileSystemView;

/**
 * @version 2012/03/07 19:47:04
 */
public class FileSystem {

    /** The file system root. */
    protected static File root;

    /** The file syste. */
    private static FileSystemView view = FileSystemView.getFileSystemView();

    static {
        Deque<File> files = new ArrayDeque(Arrays.asList(view.getRoots()));

        while (root == null) {
            File file = files.poll();
            File[] children = file.listFiles();

            for (File child : children) {
                if (view.isDrive(child)) {
                    root = file;
                    break;
                }
                files.add(child);
            }
        }
    }

    public static void main(String[] args) {
        // Iterable<Path> iterable = FileSystems.getDefault().getRootDirectories();
        // Iterator<Path> iterator = iterable.iterator();
        //
        // while (iterator.hasNext()) {
        // Path type = iterator.next();
        // System.out.println(type + "  " + type.toFile());
        //
        // }
        //
        // for (File file : root.listFiles()) {
        // System.out.println(file + "   " + view.getSystemDisplayName(file));
        // }

        for (File root : view.getRoots()) {
            System.out.println(root.getPath());
            System.out.println("   " + view.getSystemDisplayName(root) + "  " + root.getAbsolutePath() + "   " + root.getParent());

            for (File sub : root.listFiles()) {
                System.out.println(sub.getPath());
                System.out.println("    " + "   " + view.getSystemDisplayName(sub) + "           " + view.getParentDirectory(sub) + "    " + (sub.getParentFile() == root));

                for (File ss : sub.listFiles()) {
                    System.out.println("        " + "   " + view.getSystemDisplayName(ss) + "           " + view.getParentDirectory(ss));

                    if (!view.isFileSystemRoot(ss)) {
                        File[] ssa = ss.listFiles();
                        if (ssa != null) {
                            for (File sss : ssa) {
                                System.out.println("            " + "   " + view.getSystemDisplayName(sss) + "           " + view.getParentDirectory(view.getParentDirectory(sss)));

                                File[] sssa = sss.listFiles();

                                if (sssa != null) {
                                    for (File ssss : sssa) {
                                        System.out.println("                " + "   " + view.getSystemDisplayName(ssss) + "           " + view.getParentDirectory(ssss));

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        boolean result = view.getRoots()[0].equals(new File("C:\\Documents and Settings\\Teletha\\デスクトップ"));
        System.out.println(result);
        System.out.println(new File("::{20D04FE0-3AEA-1069-A2D8-08002B30309D}"));

    }
}
