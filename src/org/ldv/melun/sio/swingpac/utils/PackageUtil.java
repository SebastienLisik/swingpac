package org.ldv.melun.sio.swingpac.utils;

/**
 *  http://testng.org/testng-4.7.zip/src/main/org/testng/internal/
 *  remodele par kpu le 28/12/2004 (no recurse quand execution à partir d'un jar)
 */

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;



public class PackageUtil {
  /**
   * 
   * @param packageName
   * @return The list of all the classes inside this package
   * @throws IOException
   */
  public static String[] findClassesInPackage(String packageName,
      List<String> included, List<String> excluded) throws IOException {
    String packageOnly = packageName;
    String packageNameSave = packageName.replace('/', '.');
    boolean recursive = false;
    if (packageName.endsWith(".*")) {
      packageOnly = packageName.substring(0, packageName
          .lastIndexOf(".*"));
      recursive = true;
    }

    List<String> vResult = new ArrayList<String>();
    String packageDirName = packageOnly.replace('.', '/');
    Enumeration<URL> dirs = PackageUtil.class.getClassLoader()
        .getResources(packageDirName);
    while (dirs.hasMoreElements()) {
      URL url = dirs.nextElement();
      String protocol = url.getProtocol();
      if ("file".equals(protocol)) {
        findClassesInDirPackage(packageOnly, included, excluded,
            URLDecoder.decode(url.getFile(), "UTF-8"), recursive,
            vResult);
      } else if ("jar".equals(protocol)) {
        JarFile jar = ((JarURLConnection) url.openConnection())
            .getJarFile();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          String name = entry.getName();
          if (name.charAt(0) == '/') {
            name = name.substring(1);
          }
          if (name.startsWith(packageDirName)) {
            int idx = name.lastIndexOf('/');
            if (idx != -1) {
              packageName = name.substring(0, idx).replace('/',
                  '.');
            }

            //PackageUtils.log("Package name is " + packageName);
            if ((idx != -1) || recursive) {
              // it's not inside a deeper dir
              if (name.endsWith(".class") && !entry.isDirectory() && packageName.equals(packageNameSave)) {
                String className = name.substring(packageName
                    .length() + 1, name.length() - 6);
                // PackageUtils.log("Found class " + className +
                // ", seeing it if it's included or excluded");
                includeOrExcludeClass(packageName, className, included, excluded, vResult);
              }
            }
          }
        }
      }
    }
    String[] result = vResult.toArray(new String[vResult.size()]);
    return result;
  }

  private static void findClassesInDirPackage(String packageName,
      List<String> included, List<String> excluded, String packagePath,
      final boolean recursive, List<String> classes) {
    File dir = new File(packagePath);

    if (!dir.exists() || !dir.isDirectory()) {
      return;
    }

    File[] dirfiles = dir.listFiles(new FileFilter() {
      public boolean accept(File file) {
        return (recursive && file.isDirectory())
            || (file.getName().endsWith(".class"));
      }
    });

    // PackageUtils.log("Looking for test classes in the directory: " +
    // dir);
    for (File file : dirfiles) {
      if (file.isDirectory() && recursive) {
        findClassesInDirPackage(packageName + "." + file.getName(),
            included, excluded, file.getAbsolutePath(), recursive,
            classes);
      } else {
        String className = file.getName().substring(0,
            file.getName().length() - 6);
        // PackageUtils.log("Found class " + className + ", seeing it if
        // it's included or excluded");
        includeOrExcludeClass(packageName, className, included, excluded, classes);
      }
    }
  }
   private static void includeOrExcludeClass(String packageName, String className,
          List<String> included, List<String> excluded, List<String> classes)
      {
        if (isIncluded(className, included, excluded)) {
          //Utils.log("... Including class " + className);
          //classes.add(packageName + '.' + className);
          classes.add(className);
        }  
//        }       else {
          //Utils.log("... Excluding class " + className);
//        }
      }

      /**
       * @return true if name should be included.
       */
      private static boolean isIncluded(String name,
          List<String> included, List<String> excluded)
      {
        boolean result = false;

        //
        // If no includes nor excludes were specified, return true.
        //
        if (included.size() == 0 && excluded.size() == 0) {
          result = true;
        }
        else {
          boolean isIncluded = PackageUtil.find(name, included);
          boolean isExcluded = PackageUtil.find(name, excluded);
          if (isIncluded && !isExcluded) {
            result = true;
          }
          else if (isExcluded) {
            result = false;
          }
          else {
            result = included.size() == 0;
          }
        }
        return result;
      }

      private static boolean find(String name, List<String> list) {
        for (String regexpStr : list) {
          if (Pattern.matches(regexpStr, name)) return true;
        }
        return false;
      }
      

      static public String[] getClasses(String packageName) {

        FilenameFilter filter = new FilenameFilter() {
          public boolean accept(File dir, String name) {
            String nomClasse = this.getClass().getName();
            nomClasse = nomClasse.substring(nomClasse.lastIndexOf('.') + 1,
                nomClasse.lastIndexOf('$'));
            return name.endsWith(".class")
                && !(name.indexOf(nomClasse) >= 0);
          }
        };
        return getNameClasses(packageName, filter);
      }

      static private String[] getNameClasses(String pckgname, FilenameFilter filter) {
        List<String> included = new ArrayList<String>();
        List<String> excluded = new ArrayList<String>();
        try {
          return PackageUtil.findClassesInPackage(pckgname, included,
              excluded);
        } catch (IOException e) {
          System.out.println("Erreur grave : ");
          e.printStackTrace();
          System.exit(1);
        }
        return null;
      }
      
}