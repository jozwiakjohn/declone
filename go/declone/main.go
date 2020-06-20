// john jozwiak on 2020 June 14 (sunday) to practice in hopes of a new good and stable job, and to clean my decades of files.

package main

import (
	"fmt"
	"os"
	"path/filepath"
	"strings"
)

type nodeDescriptor struct {
	path   string // from the root of descent
	name   string // occurs at the end of path
	size   int64  // in bytes
	isFile bool
	digest string
}

var nodeDescriptors = make(map[string]SetOfString) // identical hashes might occur at multiple paths...the entire point.

func verifyOk(e error) {
	if e != nil {
		panic(e)
	}
}

func examinePath(p string) string {

	if p == "." { //  some defensive programming here: docs for ioutil.ReadDir do not specify if "." is returned.
		fmt.Printf("WAIT, how did a dot get sent to examinePath?\n\n")
		return ""
	}
	//	fmt.Printf("examining \"%s\"\n", p)

	lstat, err := os.Lstat(p)
	verifyOk(err)

	descr := nodeDescriptor{
		path:   p,
		name:   lstat.Name(),
		size:   lstat.Size(),
		isFile: lstat.Mode().IsRegular(),
	}

	//  now if this is a file, compute a hash and store it, then index the thing for later.
	//  path objects are files or folders, and each needs a sense of probable-identity:
	//  for a file, use the hexadecimal string representing the sha256 hash of the file contents;
	//  for a folder, use the string composed of the sorted folder name's hashes, newline-separated.

	if descr.isFile {
		descr.digest = calculateFileDigest(p)
	} else {
		descr.digest = calculateFolderDigest(p)
	}

	_, found := nodeDescriptors[descr.digest] //  Go's irregular syntax for checking if a map contains a key.

	if !found { //  ensure a SetOfString exists at this key.
		nodeDescriptors[descr.digest] = SetOfString{}
	}
	//
	nodeDescriptors[descr.digest].InsertBang(p)
	return descr.digest
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
		if v.Cardinality() > 1 {
			fmt.Println(v.ToString("verbose"))
		}
	}
}
