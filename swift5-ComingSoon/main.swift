// detectclones Swift 5 version, 2020 June 25, john jozwiak for my own update on Swift since 2019, and earlier 2015.

import Foundation
import Swift

struct CloneGroup {
    var file  : Bool        //  true means File; false means Folder.
    var size  : UInt64      //  Go FileMode uses int64 not uint64 here.
    var paths : Set<String> //  the paths which are clones.
}

var nodeDescriptors = [String:CloneGroup]() // identical hashes might occur at multiple paths...the entire point.
let filemanager = FileManager.default

func examinePath(_ path : String) -> (digestForPath : String, sizeInBytes: UInt64) {

    if path == "." { //  some defensive programming here: docs for ioutil.ReadDir do not specify if "." is returned.
        print("WAIT, how did a dot get sent to examinePath?\n\n")
        return ("", 0)
    }
            
    //  paths name files or folders, and each needs a sense of probable-identity:
    //  for a file, use the hexadecimal string representing the sha256 hash of the file contents;
    //  for a folder, use the string composed of the sorted folder's folder-local filenames and hashes, semicolon-separated.

    let (digest, isRegularFile, sizeAtPath) = calculatePathDigestTypeAndSize(path: path)

    //  if we have NOT seen this digest before, initialize a CloneGroup to hold it within the map from digests to CloneGroups.
    let found = nodeDescriptors[digest] //  Swift returns an optional for dictionary index, so returns nil for an invalid key.
    if found == nil {                   //  ensure a SetOfString exists at this key.
        nodeDescriptors[digest] = CloneGroup(file: isRegularFile, size: sizeAtPath, paths: Set<String>())
    }
    //  and add this path to the CloneGroup at this digest.
    nodeDescriptors[digest]?.paths.insert(path)

    return (digest, sizeAtPath) //  return the digest to recursively use it for folder digest construction.
}


func main() {

    let osargs = CommandLine.arguments
    if  osargs.count == 1 {
        print("\(osargs[0]) needs a list of filesystem paths:  it will examine all files at the given paths, and recursively below, to identify clones.")
        exit(1)
    }

    //  commandline args are either this binary's name, or paths to explore, or command flags.

    var rawRoots = Set<String>()
    var rawCmnds = Set<String>()
    
    //  run through the commandline args to grab paths to explore, and commands (to be defined later).
    
    for a in 1...(CommandLine.argc-1) {  //  0th arg is the name of this as a compiled binary, as invoked.
        
        var arg = CommandLine.arguments[Int(a)]
        if arg.hasPrefix("-") {
            rawCmnds.insert(arg)
        } else {
            if arg == "." { // replace "." on the commandline with the current working directory
                let cwd = FileManager.default.currentDirectoryPath
                print("looks like the current working directory is \"\(cwd)\"")
                arg = cwd
            }
            rawRoots.insert(arg)  //  sanitize that filepath first
        }
    }

    for c in rawCmnds.sorted() {
      print("the command \(c) is being ignored at the moment\n")
    }

    for p in rawRoots.sorted() {
        _ = examinePath(p)
    }
     
    //  at this point, nodeDescriptors is a map from strings, each a sha256 digest of a file or a composition thereof for a directory,  to CloneGroup, each holding a sets of paths as strings.
    //  We can iterate through the keys to see which keys (i.e., unique sha256 digest as a signature) occurs at more than one path!

    var totalSquandered : UInt64 = 0

    for clonegroup in nodeDescriptors.values {

        let cardinality = clonegroup.paths.count
        if cardinality > 1 { //  then we have found a clone, so tidy up the english commentary on such.
            let what : String = clonegroup.file ? "files" : "folders"
            let squandered = UInt64(cardinality-1) * clonegroup.size
            let label = "the following \(cardinality) \(what) seem to hold identical content, each instance uses \(clonegroup.size) bytes in file(s), so \(squandered) bytes are squandered in duplication:\n"
            print(label,clonegroup.paths,"\n")

            totalSquandered += squandered
        }
    }

    print("Total bytes squandered in duplication is \(totalSquandered).\n")
}

main()  //  in a single file app, the file can be named detectclones.swift, and main will be found, but in multiple .swift files, main() is seemingly only sought in main.swift.
