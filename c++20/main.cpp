#include<iostream>
#include<cstdio>
#import <unordered_set>
#include<algorithm>
#include<string>
#include<filesystem>

using namespace std;

//
// john jozwiak on 2021 August 3 (tuesday) to practice in hopes of a new good and stable job, and to clean my decades of files.
//

int main(int argc, char ** args) {
    printf("hej daa\n");
    std::cout << "Hello, World!" << endl;

    if (1 == argc) {
        auto explanation = string(args[0]) + string(" needs a list of filesystem paths:  it will examine all files at the given paths, and recursively below, to identify clones.");
        cout << explanation << endl;
        exit(1);
    }

    //  commandline args are either this binary's name, or paths to explore, or command flags.

    auto rawRoots = unordered_set<string>();
    auto rawCmnds = unordered_set<string>();

    //  run through the commandline args to grab paths to explore, and commands (to be defined later).

    for (int i = 1; i < argc; ++i) { //  because os.Args[0] is this binary's name.

        if (args[i][0] == '-') {     //  in case i ever want to add commandline commands prefixed by hyphen
            rawCmnds.insert(string(args[i]));
        }
        else {

            auto p = filesystem::current_path();
            if (args[i][0] != '.') {
               rawRoots.insert(filesystem::current_path());
            } else {
               rawRoots.insert(filesystem::path(args[i]) );
            }
        }
    }

    cout << "iterator..." << endl;

    for( auto i = rawRoots.begin(); i != rawRoots.end(); ++i) {
        cout << *i << endl;
        // examinePath(*i);
    }

    //  at this point, nodeDescriptors is a map from strings, each a sha256 digest of a file or a composition thereof for a directory,  to sets of strings, each a path.
    //  We can iterate through the keys to see which keys (i.e., unique sha256 digest as a signature) occurs at more than one path!

    auto totalSquandered = uint64_t(0);

//  for _, v := range nodeDescriptors {
//          cardinality := v.set.Cardinality()
//          if cardinality > 1 { //  then we have found a clone, so tidy up the english commentary on such.
//                          var what string
//                          if v.file {
//                              what = "files"
//                          } else {
//                              what = "folders"
//                          }
//                          squandered := int64(cardinality-1) * v.size
//                          label := fmt.Sprintf("the following %d %s seem to hold identical content, each instance uses %d bytes in file(s), so %d bytes are squandered in duplication:\n", cardinality, what, v.size, squandered)
//                          fmt.Println(v.set.ToString("verbose", label))
//                          totalSquandered += squandered
//                      }
//          }
    cout << "Total bytes squandered in duplication is " << totalSquandered << endl;

    return 0;
}
/*
type CloneGroup struct {
	file bool        //  true means File; false means Folder.
	size int64       //  Go FileMode uses int64 not uint64 here.
	set  SetOfString //  the paths which are clones.
}

var nodeDescriptors = make(map[string]CloneGroup) // identical hashes might occur at multiple paths...the entire point.

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

func calculateFileDigest(path string) string {

	// p names a file, so calculate a sha256 hash of it to store with its length.

	f, err := os.Open(path)
	defer func() { _ = f.Close() }()
	verifyOk(err)

	h := sha256.New()
	_, err = io.Copy(h, f)
	verifyOk(err)

	dbytes := h.Sum(nil)
	return fmt.Sprintf("%x", dbytes)
}

func calculateFolderDigest(path string) (string, int64) {

	dirContents, err := ioutil.ReadDir(path)
	if err != nil {
		panic(fmt.Errorf("error: %s is not a directory: %v", path, err))
	}

	// build up an alphabetically sorted list of names with their digests;
	// sorted order is delivered by ioutil.ReadDir.

	dgs := make([]string, 0)
	containedsize := int64(0)

	for _, e := range dirContents {

		name := e.Name()
		d, sz := examinePath(filepath.Join(path, name))
		dgs = append(dgs, fmt.Sprintf("%s:%s;", name, d))
		containedsize += sz
	}
	dg := ""
	for _, d := range dgs {
		dg = dg + d
	}
	return dg, containedsize
}

func calculatePathDigestTypeAndSize(path string) (string, bool, int64) {

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
}
*/
