// john jozwiak on 2020 June 14 (sunday) to practice in hopes of a new good and stable job, and to clean my decades of files.

package main

import (
	"fmt"
	"os"
	"path/filepath"
	"strings"
)

type CloneGroup struct {
	file bool        //  true means File; false means Folder.
	size int64       //  Go FileMode uses int64 not uint64 here.
	set  SetOfString //  the paths which are clones.
}

var nodeDescriptors = make(map[string]CloneGroup) // identical hashes might occur at multiple paths...the entire point.

func verifyOk(e error) {
	if e != nil {
		panic(e)
	}
}

func examinePath(path string) (string, int64) {

	if path == "." { //  some defensive programming here: docs for ioutil.ReadDir do not specify if "." is returned.
		fmt.Printf("WAIT, how did a dot get sent to examinePath?\n\n")
		return "", 0
	}

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

	if len(os.Args) == 1 {
		_, _ = fmt.Fprintf(os.Stderr, "%s needs a list of filesystem paths:  it will examine all files at the given paths, and recursively below, to identify clones.", filepath.Clean(os.Args[0]))
		os.Exit(1)
	}

	//  commandline args are either this binary's name, or paths to explore, or command flags.

	rawRoots := make(SetOfString)
	rawCmnds := make(SetOfString)

	//  run through the commandline args to grab paths to explore, and commands (to be defined later).

	for _, s := range os.Args[1:] { //  because os.Args[0] is this binary's name.
		if strings.HasPrefix(s, "-") {
			rawCmnds.InsertBang(s)
		} else {
			s = filepath.Clean(s)
			if s == "." { // replace "." on the commandline with the current working directory.
				var err error
				s, err = os.Getwd()
				verifyOk(err)
			}
			rawRoots.InsertBang(filepath.Clean(s))
		}
	}

	cmnds := rawCmnds.ToSlice()
	roots := rawRoots.ToSlice()

	for _, c := range cmnds {
		fmt.Printf("the command %v is being ignored at the moment\n", c)
	}

	for _, p := range roots {
		examinePath(p)
	}

	//  at this point, nodeDescriptors is a map from strings, each a sha256 digest of a file or a composition thereof for a directory,  to sets of strings, each a path.
	//  We can iterate through the keys to see which keys (i.e., unique sha256 digest as a signature) occurs at more than one path!

	totalSquandered := int64(0)

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
}


// detectclones Swift 5 version, 2020 June 25, john jozwiak for my own update on Swift since 2019, and earlier 2015.

import Foundation
import Swift

for a in 1...(CommandLine.argc-1) {  //  0th arg is the name of this as a compiled binary, as invoked.

  let arg = CommandLine.arguments[Int(a)]
  print("arg \(a) = \"\(arg)\"\n")

}
