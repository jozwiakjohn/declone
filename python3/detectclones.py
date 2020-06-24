# john jozwiak on 2020 June 21 (sunday) to practice in hopes of a new good and stable job, and to clean my decades of files.


import hashlib,glob,os,os.path,sys

class CloneGroup:
    def __init__(self,isFile,size,pathsSet):
        self.file = isFile    ## bool        : true means File; false means Folder.
        self.size = size      ## int64       : Go FileMode uses int64 not uint64 here.
        self.paths = pathsSet ## SetOfString : the paths which are clones.

nodeDescriptors = {} ##      map of string -> CloneGroup ## identical hashes might occur at multiple paths...the entire point.

def  calculateFileDigest(path):

    with open(path,"rb") as f:
        bytes = f.read(-1)
    h = hashlib.sha256().update(bytes).hexdigest()
    return h

def calculateFolderDigest(path): ## (string, int64)

    #  build up an alphabetically sorted list of names with their digests
    #  sorted order is delivered by ioutil.ReadDir.

    dgs = []
    containedsize = 0

    for e in glob.glob(os.path.join(path,"*")):

        name = os.path.basename(e)
        print(e,name)
        d, sz = examinePath(os.path.join(path, name))
        dgs.append(str(name) + ":" + str(d))
        containedsize += sz

    dg = ""
    for d in dgs: # _, d := range dgs {
        dg = dg + d

    return dg, containedsize

def calculatePathDigestTypeAndSize(path): ## (string, bool, int64)

    lstat, err := os.Lstat(path)
    verifyOk(err)

    isRegularFile := lstat.Mode().IsRegular()

    var sizeAtPath int64
    var digest string

    if isRegularFile {
    digest = calculateFileDigest(path)
    sizeAtPath = lstat.Size()
    } else {
    digest, sizeAtPath = calculateFolderDigest(path)
    }

    return digest, isRegularFile, sizeAtPath
#  def  ToString(s set, style string, label string) string :
#  items = list(set)
#  r := ""
#  switch style {
#      case "scheme", "lisp", "racket", "t":
#  r += "("
#  for _, i := range items {
#  r += fmt.Sprintf("\"%s\"  ", i)
#  }
#  r += ")"
#  case "go", "golang":
#      r = fmt.Sprintf("%#v", items)
#  case "verbose", "english", "human":
#  r += label
#  for _, i := range items {
#  r += fmt.Sprintf("   \"%s\"\n", i)
#  }
#  r += "\n"
#  default:
#      r = fmt.Sprintf("%#v", items)
#  }
#  return r

def  examinePath(path): ## (string, int64)

    if path == ".":  ##  some defensive programming here: docs for ioutil.ReadDir do not specify if "." is returned.
        print("WAIT, how did a dot get sent to examinePath?\n\n")
        return "", 0


    ##  paths name files or folders, and each needs a sense of probable-identity:
    ##  for a file, use the hexadecimal string representing the sha256 hash of the file contents;
    ##  for a folder, use the string composed of the sorted folder's folder-local filenames and hashes, semicolon-separated.

    (digest, isRegularFile, sizeAtPath) = calculatePathDigestTypeAndSize(path)

    ##  if we have NOT seen this digest before, initialize a CloneGroup to hold it within the map from digests to CloneGroups.
    if digest not in nodeDescriptors:  #  python's syntax for checking if a map has a "key".
        #  ensure a CloneGroup now exists at this key.
        nodeDescriptors[digest] = CloneGroup(file: isRegularFile, size: sizeAtPath, set: SetOfString{} )
    ##  and add this path to the CloneGroup at this digest.
    nodeDescriptors[digest].paths.add(path)

    return digest, sizeAtPath  ##  return the digest to recursively use it for folder digest construction.


def main():

    if len(sys.argv) == 1 :
        print(sys.Argv[0], " needs a list of filesystem paths:  it will examine all files at the given paths, and recursively below, to identify clones.")
        sys.exit(1)

    ##  commandline args are either this binary's name, or paths to explore, or command flags.

    rawCmnds = set()
    rawRoots = set()

    ##  run through the commandline args to grab paths to explore, and commands (to be defined later).

    for s in sys.argv[1:] : ##  because os.Args[0] is this binary's name.

        if s.startswith("-"):
            rawCmnds.add(s)
        else :
            s = os.path.normpath(s)
            if s == "." : ## replace "." on the commandline with the current working directory.
                s = os.getcwd()
            rawRoots.add(os.path.normpath(s))

    cmnds = list(rawCmnds)
    roots = list(rawRoots)

    for _, c in cmnds :
        print("the command "+str(c)+" is being ignored at the moment\n")

    for _, p in roots :
        examinePath(p)

    ##  at this point, nodeDescriptors is a map from strings, each a sha256 digest of a file or a composition thereof for a directory,  to sets of strings, each a path.
    ##  We can iterate through the keys to see which keys (i.e., unique sha256 digest as a signature) occurs at more than one path!

    totalSquandered = 0

    for v in nodeDescriptors :
        cardinality = len(v.set)
        if cardinality > 1 : ##  then we have found a clone, so tidy up the english commentary on such.
            what = ""
            if v.file :
                what = "files"
            else :
                what = "folders"
            squandered = (cardinality-1) * v.size
            label = "the following " + str(cardinality) + " " + str(what) + " seem to hold identical content, each instance uses " + str(v.size) + " bytes in file(s), so " + str(squandered) + " bytes are squandered in duplication:\n"

            print(v.set.ToString("verbose", label))
            totalSquandered += squandered
    print("Total bytes squandered in duplication is ",str(totalSquandered), ".\n")

main()
