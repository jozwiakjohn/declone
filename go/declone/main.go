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

func examinePath(path string) string {

	if path == "." { //  some defensive programming here: docs for ioutil.ReadDir do not specify if "." is returned.
		fmt.Printf("WAIT, how did a dot get sent to examinePath?\n\n")
		return ""
	}
	//	fmt.Printf("examining \"%s\"\n", p)

	//  paths name files or folders, and each needs a sense of probable-identity:
	//  for a file, use the hexadecimal string representing the sha256 hash of the file contents;
	//  for a folder, use the string composed of the sorted folder name's hashes, newline-separated.

	digest, isRegularFile, sizeAtPath := calculatePathDigestTypeAndSize(path)

	_, found := nodeDescriptors[digest] //  Go's irregular syntax for checking if a map contains a key.
	if !found {                         //  ensure a SetOfString exists at this key.
		nodeDescriptors[digest] = CloneGroup{file: isRegularFile, size: sizeAtPath, set: SetOfString{}}
	}
	nodeDescriptors[digest].set.InsertBang(path)

	return digest
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

	for _, v := range nodeDescriptors {
		if v.set.Cardinality() > 1 {
			var what string
			if v.file {
				what = "files"
			} else {
				what = "folders"
			}
			fmt.Println(v.set.ToString("verbose", fmt.Sprintf("the following %s seem to hold identical content...\n", what)))
		}
	}
}
