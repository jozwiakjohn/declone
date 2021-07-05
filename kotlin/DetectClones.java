// john jozwiak on 2020 Aug 11-13 (tisdag,onsdag,torsdag), a Java14 version.


import java.nio.file.Path;


public class DetectClones {

//  def calculateFileDigest(path):
//      with open(path,"rb") as f:
//          bytes = f.read(-1)
//      h = hashlib.sha256(bytes).hexdigest()
//      return h

//  def calculateFolderDigest(path):
//  
//      //  build up an alphabetically sorted list of names with their digests
//  
//      dgs = []
//      containedsize = 0
//  
//      for e in sorted(glob.glob(os.path.join(path,"*"))):
//  
//          name = os.path.basename(e)
//          d, sz = examinePath(e)
//          dgs.append(str(name) + ":" + str(d))
//          containedsize += sz
//  
//      return "".join(dgs), containedsize

//  def calculatePathDigestTypeAndSize(path):
//  
//      lstat = os.lstat(path)
//  
//      isRegularFile = stat.S_ISREG( lstat.st_mode )
//  
//      if isRegularFile :
//        digest = calculateFileDigest(path)
//        sizeAtPath = lstat.st_size
//      else :
//        digest, sizeAtPath = calculateFolderDigest(path)
//  
//      return (digest, isRegularFile, sizeAtPath)

//  def examinePath(path):  #  returns a string digest, and a long size.
//  
//      if path == ".":  // some defensive programming here: docs for ioutil.ReadDir do not specify if "." is returned.
//          print("WAIT, how did a dot get sent to examinePath?\n\n")
//          return "", 0
//  
//      // paths name files or folders, and each needs a sense of probable-identity:
//      // for a file, use the hexadecimal string representing the sha256 hash of the file contents;
//      // for a folder, use the string composed of the sorted folder's local filenames and hashes, semicolon-separated.
//  
//      (digest, isRegularFile, sizeAtPath) = calculatePathDigestTypeAndSize(path)
//  
//      //  if we have NOT seen this digest before, initialize a CloneGroup to hold it.
//      if digest not in nodeDescriptors:
//          //  ensure a CloneGroup now exists at this key.
//          nodeDescriptors[digest] = CloneGroup( isRegularFile, sizeAtPath )
//      //  and add this path to the CloneGroup at this digest.
//      nodeDescriptors[digest].paths.add(path)
//  
//      return digest, sizeAtPath  //  return the digest to recursively use it for folder digest construction.

  public static void main( String[] a )
  {
     if (a.length == 1) {
        System.out.println( a[0] + " needs a list of filesystem paths:  it will examine all files at the given paths, and recursively below, to identify clones.");
        System.exit(1);
     }

     PathNormalizer.selfTest();

     //  commandline args are either this binary's name (not in Java, but in Swift and Go and Python), or paths to explore, or command flags.
    
     var rawCmnds = new HashSet<String>();
     var rawRoots = new HashSet<String>();
    
     //  run through the commandline args to grab paths to explore, and commands (to be defined later).
    
//   Path norman = Path.of(".").toAbsolutePath().normalize();
//   System.out.printf( "Path.of(\".\") = \"%s\"\nPath.of(\".\").normalize() --> \"%s\"\n" , Path.of("."), norman.toString() );

     String currentWorkingDir = System.getProperty("user.dir");
     System.out.printf("cwd looks to be : \"%s\"\n",currentWorkingDir);

     for (int i = 0; i < a.length ; i++) {
        String s = a[i];
        Path   p; 

        if (s.startsWith("-")) {
           rawCmnds.add(s);
        } else {
           String sn = PathNormalizer.normalize(s);
           rawRoots.add( sn );
           System.out.printf( "\"%s\" -> \"%s\"\n", s , sn.toString() );
        }
     }
    
     var cmnds = rawCmnds.toArray( new String[ rawCmnds.size() ]);
     var roots = rawRoots.toArray( new String[ rawRoots.size() ]);

     var nodeDescriptors = new HashMap<String,CloneGroup>();

     for (String c: cmnds) {
        System.out.printf("the command "+c+" is being ignored at the moment\n");
     }

     for (String p: roots) {
        System.out.printf("the PATH    "+p+" is being ignored at the moment\n");
        examinePath(p)
     }

     // at this point, nodeDescriptors is a map from strings thereof for a directory,  to sets of strings, each a path.
     // images of keys with cardinality greater than 1 are paths for identical maximal subtrees.
    
     long totalSquandered = 0;

     for ( String p : nodeDescriptors.keySet() ) {

        var clonegroup  = nodeDescriptors.get(p)  ;
        var cardinality = clonegroup.paths.size() ;
        if (cardinality > 1) // then we have found a clone, so tidy up the english commentary on such.
        {
            var what       = clonegroup.isafile ? "files" : "folders" ;
            var squandered = (cardinality-1) * clonegroup.size ;

            var label =
               String.format(
                  "the following %d %s seem to hold identical content; each instance uses %d bytes in file(s), so %d bytes are squandered in duplication:\n",
                  cardinality, what, clonegroup.size, squandered
               );

            System.out.println(label);

            for( String path: clonegroup.paths ) {
               System.out.printf("    %s\n",p);
            }
            System.out.println();
            totalSquandered += squandered;
        }
     };

    System.out.printf("Total bytes squandered in duplication is %d\n",totalSquandered);
  }
}


class CloneGroup {

  boolean          isafile ;
  long             size    ;
  HashSet<String>  paths   ;

  CloneGroup( boolean isafile, long size ) {
    this.isafile = isafile;
    this.size    = size;
    paths        = new HashSet<String>();
  }
}


class PathNormalizer {

  static String normalize( String s ) {

    // replace "." on the commandline with the current working directory.
    s = Path.of(s).normalize().toString();
//  s = ((s == ".") ? p.toAbsolutePath() : p.normalize()).toString();
    s = s.equals("") ? "." : s;
    return s;
  }

  static void selfTest() {
     var examples = new HashMap<String,String>();
     examples.put( "a//b" , "a/b" );
     examples.put( "."    , "."   );
     examples.put( "a/.." , "."   );

     for (String question: examples.keySet() ) {
       String got = normalize( question );
       if (! examples.get(question).equals( got )) {
         System.out.printf("test failure :  \"%s\" normalized to \"%s\" while expecting \"%s\"\n", question, got, examples.get(question) );
         System.exit(1);
       }
     }
  }
}
