package main

// john jozwiak on 2020 June 14 (sunday) to practice in hopes of a new good and stable job, and to clean my decades of files.

import (
	"crypto/sha256"
	"fmt"
	"io"
	"io/ioutil"
	"os"
	"path/filepath"
	"strings"
)

/*
var treeDescriptors map[string]string

func filterStrings(ss []string, criterion func(s string) bool) []string {
	var filtration = make([]string, 0) //  not sure how many will "survive" filtration.
	for _, s := range ss {
		if criterion(s) {
			filtration = append(filtration, s)
		}
	}
	return filtration
}

func mapStrings(ss []string, f func(s string) string) []string { // yes the map is string -> string for now.
	rslt := make([]string, len(ss))
	for i, s := range ss {
		rslt[i] = f(s)
	}
	return rslt
}
*/

type SetOfString map[string]struct{}

func (set *SetOfString) Insert(s string) *SetOfString {
	(*set)[s] = struct{}{} //  takes no useless byte space.
	return set
}

func (set *SetOfString) Remove(s string) *SetOfString {
	delete(*set, s)
	return set
}

func (set *SetOfString) Contains(s string) bool {
	_, found := (*set)[s]
	return found
}

/*
func (set * SetOfString) union(set2 SetOfString) * SetOfString {

}

func (set * SetOfString) intersect(set2 SetOfString) * SetOfString {

}
*/

type nodeDescriptor struct {
	path   string
	size   int64 // in bytes
	isFile bool
	digest string
}

var nodeDescriptors = make(map[int64]nodeDescriptor)

func verifyOk(e error) {
	if e != nil {
		panic(e)
	}
}

func calculateFileDigest(path string) []byte {

	// p names a file, so calculate a sha256 hash of it to store with its length.

	f, err := os.Open(path)
	defer func() { _ = f.Close() }()
	verifyOk(err)

	h := sha256.New()
	_, err = io.Copy(h, f)
	verifyOk(err)

	return h.Sum(nil)
}

func examinePath(p string) {

	lstat, err := os.Lstat(p)
	verifyOk(err)

	disco := nodeDescriptor{path: p, size: lstat.Size(), isFile: !lstat.IsDir()}

	//  now if this is a file, compute a hash and store it, then index the thing for later.

	if lstat.IsDir() {

		//  p names a directory, so examine it recursively.

		dirContents, err := ioutil.ReadDir(p)
		if err != nil {
			fmt.Printf("ERROR : %s is not a directory: %v\n", p, err)
			return
		}
		for _, e := range dirContents {

			examinePath(p + "/" + e.Name())
		}
	} else {

		// p names a file, so memoize its descriptor by size, for now.

		disco.digest = fmt.Sprintf("%x", calculateFileDigest(p))
		fmt.Printf("file : \"%s\"\n", p)
	}

	nodeDescriptors[disco.size] = disco
}

func main() {

	if len(os.Args) == 1 {
		_, _ = fmt.Fprintf(os.Stderr, "%s needs a list of filesystem paths:  it will examine all files at the given paths, and recursively below, to identify clones.", filepath.Clean(os.Args[0]))
		os.Exit(1)
	}

	//  run through the commandline args to grab paths to explore, and commands (to be defined later).

	roots := make([]string, 0)
	cmnds := make([]string, 0)

	for _, s := range os.Args[1:] {
		if strings.HasPrefix(s, "-") { //  thus "--" is a viable prefix for a command too.
			cmnds = append(cmnds, s)
		} else {
			roots = append(roots, s)
		}
	}

	if len(roots) > 0 {

		for _, p := range roots {
			examinePath(p)
		}
	}

	for k, v := range nodeDescriptors {
		fmt.Printf("%v -> %v\n", k, v)
	}

}
