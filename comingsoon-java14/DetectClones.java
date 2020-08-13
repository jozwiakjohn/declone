// john jozwiak on 2020 Aug 11 (tuesday), a Java14 version.


//  import hashlib,glob,os,os.path,stat,sys
//  
//  
//  class CloneGroup {
//      def __init__(self,isFile,size):
//          self.file = isFile    // bool        : true means File; false means Folder.
//          self.size = size      // int64       : Go FileMode uses int64 not uint64 here.
//          self.paths = set([])  // SetOfString : the paths which are clones.
//  
//  
//  
//  nodeDescriptors = {} // map of string -> CloneGroup ; identical hashes might occur at multiple paths...the entire point.
//  
/////////////////////////////////////////////////////////////////////////////////////
//  
//  def calculateFileDigest(path):
//      with open(path,"rb") as f:
//          bytes = f.read(-1)
//      h = hashlib.sha256(bytes).hexdigest()
//      return h
//  
/////////////////////////////////////////////////////////////////////////////////////
//  
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
//  
/////////////////////////////////////////////////////////////////////////////////////
//  
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
//  
/////////////////////////////////////////////////////////////////////////////////////
//  
//  def examinePath(path):
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
//  
/////////////////////////////////////////////////////////////////////////////////////

import java.util.HashSet;
import java.nio.file.Path;

public class DetectClones {
  public static void main( String[] a ) {
    if (a.length == 1) {
      System.out.println( a[0] + " needs a list of filesystem paths:  it will examine all files at the given paths, and recursively below, to identify clones.");
      System.exit(1);
    }

//  for (int i = 0 ; i < a.length ; i++) {  //  args to main in java do NOT include the binary's name as the 0th arg.
//      System.out.printf( "I see position %2d has \"%s\".\n", i, a[i] );
//  }

    //  commandline args are either this binary's name, or paths to explore, or command flags.
    
    HashSet<String> rawCmnds = new HashSet<String>();
    HashSet<String> rawRoots = new HashSet<String>();
    
    //  run through the commandline args to grab paths to explore, and commands (to be defined later).
    
//  Path norman = Path.of(".").toAbsolutePath().normalize();
//  System.out.printf( "Path.of(\".\") = \"%s\"\nPath.of(\".\").normalize() --> \"%s\"\n" , Path.of("."), norman.toString() );

    String currentWorkingDir = System.getProperty("user.dir");
    System.out.printf("cwd looks to be : \"%s\"\n",currentWorkingDir);

    for (int i = 0; i < a.length ; i++) {

      String s = a[i];
      Path   p; 

      if (s.startsWith("-")) {
          rawCmnds.add(s);
      } else {
          if (s == ".") {   // replace "." on the commandline with the current working directory.
            p = Path.of(s).toAbsolutePath();
          } else {
            p = Path.of(s).normalize();
          }
          String sn = p.toString();
          rawRoots.add( sn );
          System.out.printf( "\"%s\" -> \"%s\"\n", s , sn.toString() );
      }
    }
    
    String[] cmnds = rawCmnds.toArray( new String[ rawCmnds.size() ]);
    String[] roots = rawRoots.toArray( new String[ rawRoots.size() ]);

//  for _, c in cmnds :
//      print("the command "+str(c)+" is being ignored at the moment\n")
//  
//  for p in roots :
//      examinePath(p)
//  
//  // at this point, nodeDescriptors is a map from strings thereof for a directory,  to sets of strings, each a path.
//  // images of keys with cardinality greater than 1 are paths for identical maximal subtrees.
//  
//  totalSquandered = 0
//  
//  for v in nodeDescriptors:
//      clonegroup = nodeDescriptors[v]
//      cardinality = len(clonegroup.paths)
//      if cardinality > 1 : // then we have found a clone, so tidy up the english commentary on such.
//          if clonegroup.file:
//              what = "files"
//          else :
//              what = "folders"
//          squandered = (cardinality-1) * clonegroup.size
//          label = ( "the following " + str(cardinality) + " " + str(what) +
//                    " seem to hold identical content, each instance uses " +
//                    str(clonegroup.size) + " bytes in file(s), so " +
//                    str(squandered) + " bytes are squandered in duplication:\n" )
// 
//          print(label)
//          for p in clonegroup.paths:
//              print("   ",p)
//          print()
//          totalSquandered += squandered
//  print("Total bytes squandered in duplication is ",str(totalSquandered), ".\n")
  }
}
