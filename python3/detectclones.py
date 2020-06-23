# john jozwiak on 2020 June 21 (sunday) to practice in hopes of a new good and stable job, and to clean my decades of files.


import	sys
import  os.path

class CloneGroup:
    def __init__(self,isFile,size,pathsSet):
        self.file = isFile    ## bool        : true means File; false means Folder.
        self.size = size      ## int64       : Go FileMode uses int64 not uint64 here.
        self.paths = pathsSet ## SetOfString : the paths which are clones.

nodeDescriptors = {} ##      map of string -> CloneGroup ## identical hashes might occur at multiple paths...the entire point.

def  verifyOk(e):
    if e != "":
        print(e)
        sys.exit(1)


def  examinePath(path): ## (string, int64)

    if path == ".":  ##  some defensive programming here: docs for ioutil.ReadDir do not specify if "." is returned.
        print("WAIT, how did a dot get sent to examinePath?\n\n")
        return "", 0


    ##  paths name files or folders, and each needs a sense of probable-identity:
    ##  for a file, use the hexadecimal string representing the sha256 hash of the file contents;
    ##  for a folder, use the string composed of the sorted folder's folder-local filenames and hashes, semicolon-separated.

    (digest, isRegularFile, sizeAtPath) = calculatePathDigestTypeAndSize(path)

    ##  if we have NOT seen this digest before, initialize a CloneGroup to hold it within the map from digests to CloneGroups.
    _, found := nodeDescriptors[digest] ##  Go's irregular syntax for checking if a map contains a key.
    if !found :                         ##  ensure a SetOfString exists at this key.
        nodeDescriptors[digest] = CloneGroup{file: isRegularFile, size: sizeAtPath, set: SetOfString{}}

    ##  and add this path to the CloneGroup at this digest.
    nodeDescriptors[digest].set.InsertBang(path)

    return digest, sizeAtPath  ##  return the digest to recursively use it for folder digest construction.


def main():

    if len(sys.argv) == 1 :
        print(sys.Argv[0], " needs a list of filesystem paths:  it will examine all files at the given paths, and recursively below, to identify clones.")
        sys.exit(1)

    ##  commandline args are either this binary's name, or paths to explore, or command flags.

    rawRoots = set()
    rawCmnds = set()

    ##  run through the commandline args to grab paths to explore, and commands (to be defined later).

    for s in sys.argv[1:] : ##  because os.Args[0] is this binary's name.


        if strings.HasPrefix(s, "-") :


            rawCmnds.InsertBang(s)
        else :
            s = os.path.normpath(s)
            if s == "." : ## replace "." on the commandline with the current working directory.
                s = os.getcwd()
            rawRoots.InsertBang(os.path.normpath(s))

    cmnds = rawCmnds.ToSlice()
    roots = rawRoots.ToSlice()

    for _, c in cmnds :
        print("the command "+str(c)+" is being ignored at the moment\n")

    for _, p in roots :
        examinePath(p)

    ##  at this point, nodeDescriptors is a map from strings, each a sha256 digest of a file or a composition thereof for a directory,  to sets of strings, each a path.
    ##  We can iterate through the keys to see which keys (i.e., unique sha256 digest as a signature) occurs at more than one path!

    totalSquandered = 0

    for v in nodeDescriptors :
        cardinality = v.set.Cardinality()
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
