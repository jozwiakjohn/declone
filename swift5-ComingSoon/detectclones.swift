// detectclones Swift 5 version, 2020 June 25, john jozwiak for my own update on Swift since 2019, and earlier 2015.

import Foundation
import Swift


struct CloneGroup {
    file : Bool        //  true means File; false means Folder.
    size : Int64       //  Go FileMode uses int64 not uint64 here.
    set  : Set<String> //  the paths which are clones.
}

var nodeDescriptors = [String:CloneGroup]() // identical hashes might occur at multiple paths...the entire point.
let filemanager = FileManager.default

func examinePath(path : String) -> (digestForPath : String, sizeInBytes: Int64) {

    if path == "." { //  some defensive programming here: docs for ioutil.ReadDir do not specify if "." is returned.
        print("WAIT, how did a dot get sent to examinePath?\n\n")
        return ("", 0)
    }
            
    //        let listing = try! filemanager.contentsOfDirectory( atPath: arg )
    //        for o in listing {
    //            print(o)
    //        }


    //  paths name files or folders, and each needs a sense of probable-identity:
    //  for a file, use the hexadecimal string representing the sha256 hash of the file contents;
    //  for a folder, use the string composed of the sorted folder's folder-local filenames and hashes, semicolon-separated.

    digest, isRegularFile, sizeAtPath := calculatePathDigestTypeAndSize(path)

    //  if we have NOT seen this digest before, initialize a CloneGroup to hold it within the map from digests to CloneGroups.
    _, found := nodeDescriptors[digest] //  Go's irregular syntax for checking if a map contains a key.
    if !found {                         //  ensure a SetOfString exists at this key.
        nodeDescriptors[digest] = CloneGroup{file: isRegularFile, size: sizeAtPath, set: SetOfString{}}
    }
    //  and add this path to the CloneGroup at this digest.
    nodeDescriptors[digest].set.InsertBang(path)

    return digest, sizeAtPath //  return the digest to recursively use it for folder digest construction.
}


func main() {

    let osargs = CommandLine.arguments
    if  osargs.count == 1 {
        print("\(osargs[0]) needs a list of filesystem paths:  it will examine all files at the given paths, and recursively below, to identify clones.")
        exit(1)
    }
    
    //  commandline args are either this binary's name, or paths to explore, or command flags.
    
    let rawRoots = Set<String>() // make(SetOfString)
    let rawCmnds = Set<String>() // make(SetOfString)
    
    //  run through the commandline args to grab paths to explore, and commands (to be defined later).
    
    for a in 1...(CommandLine.argc-1) {  //  0th arg is the name of this as a compiled binary, as invoked.
        
        let arg = CommandLine.arguments[Int(a)]
        if arg.hasPrefix("-") {
            rawCmnds.insert(arg)
        } else {
            var a = arg
            if a == "." { // replace "." on the commandline with the current working directory
                let cwd = FileManager.default.cwd
                print("looks like the current working directory is \"\(cwd)\"")
                a = cwd
            }
            rawRoots.insert(a)  //  sanitize that filepath first
        }
    }
    
    for c in rawCmnds.sorted() {
      print("the command \(c) is being ignored at the moment\n")
    }

    for p in rawRoots.sorted() {
        examinePath(p)
    }
     
    //  at this point, nodeDescriptors is a map from strings, each a sha256 digest of a file or a composition thereof for a directory,  to CloneGroup, each holding a sets of paths as strings.
    //  We can iterate through the keys to see which keys (i.e., unique sha256 digest as a signature) occurs at more than one path!

    var totalSquandered : Int64 = 0

    /*

    for _, v := range nodeDescriptors {
        cardinality := v.set.Cardinality()
        if cardinality > 1 { //  then we have found a clone, so tidy up the english commentary on such.
            var what string
            if v.file {
                what = "files"
            } else {
                what = "folders"
            }
            squandered := int64(cardinality-1) * v.size
            label := fmt.Sprintf("the following %d %s seem to hold identical content, each instance uses %d bytes in file(s), so %d bytes are squandered in duplication:\n", cardinality, what, v.size, squandered)

            fmt.Println(v.set.ToString("verbose", label))
            totalSquandered += squandered
        }
    }
    fmt.Printf("Total bytes squandered in duplication is %d.\n", totalSquandered)
 */
}

main()